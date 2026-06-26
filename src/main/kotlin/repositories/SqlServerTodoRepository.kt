package com.olds.repositories

import com.olds.interfaces.TodoRepository
import com.olds.models.Priority
import com.olds.models.Todo
import java.sql.Connection
import java.sql.DriverManager

class SqlServerTodoRepository(
    private val jdbcUrl: String,
    private val username: String? = null,
    private val password: String? = null,
) : TodoRepository {
    init {
        //initializeSchema()
    }

    override fun allTodos(username: String): List<Todo> = withConnection { connection ->
        connection.prepareStatement(
            """
                SELECT id, username, title, description, priority, completed
                FROM dbo.Tasks
                WHERE username = ?
                ORDER BY id
            """.trimIndent(),
        ).use { statement ->
            statement.setString(1, username)
            statement.executeQuery().use { resultSet ->
                buildList {
                    while (resultSet.next()) {
                        add(resultSet.toTodo())
                    }
                }
            }
        }
    }

    override fun todoById(id: String, username: String): Todo? = withConnection { connection ->
        connection.prepareStatement(
            """
                SELECT id, username, title, description, priority, completed
                FROM dbo.Tasks
                WHERE id = ? AND username = ?
            """.trimIndent(),
        ).use { statement ->
            statement.setString(1, id)
            statement.setString(2, username)
            statement.executeQuery().use { resultSet ->
                if (resultSet.next()) resultSet.toTodo() else null
            }
        }
    }

    override fun todosByPriority(username: String, priority: Priority): List<Todo> = withConnection { connection ->
        connection.prepareStatement(
            """
                SELECT id, username, title, description, priority, completed
                FROM dbo.Tasks
                WHERE username = ? AND priority = ?
                ORDER BY id
            """.trimIndent(),
        ).use { statement ->
            statement.setString(1, username)
            statement.setString(2, priority.name)
            statement.executeQuery().use { resultSet ->
                buildList {
                    while (resultSet.next()) {
                        add(resultSet.toTodo())
                    }
                }
            }
        }
    }

    override fun addTodo(username: String, todo: Todo) {
        withConnection { connection ->
            connection.prepareStatement(
                """
                    INSERT INTO dbo.Tasks (id, username, title, description, priority, completed)
                    VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, todo.id)
                statement.setString(2, username)
                statement.setString(3, todo.title)
                statement.setString(4, todo.description)
                statement.setString(5, todo.priority.name)
                statement.setBoolean(6, todo.completed)
                statement.executeUpdate()
            }
        }
    }

    override fun updateTodo(username: String, todo: Todo) {
        val updated = withConnection { connection ->
            connection.prepareStatement(
                """
                    UPDATE dbo.Tasks
                    SET title = ?, description = ?, priority = ?, completed = ?
                    WHERE id = ? AND username = ?
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, todo.title)
                statement.setString(2, todo.description)
                statement.setString(3, todo.priority.name)
                statement.setBoolean(4, todo.completed)
                statement.setString(5, todo.id)
                statement.setString(6, username)
                statement.executeUpdate()
            }
        }

        if (updated == 0) {
            throw IllegalStateException("Todo ${todo.id} does not exist")
        }
    }

    override fun completeTodo(username: String, id: String) {
        val updated = withConnection { connection ->
            connection.prepareStatement(
                """
                    UPDATE dbo.Tasks
                    SET completed = 1
                    WHERE id = ? AND username = ?
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, id)
                statement.setString(2, username)
                statement.executeUpdate()
            }
        }

        if (updated == 0) {
            throw IllegalStateException("Todo $id does not exist")
        }
    }

    override fun removeTodo(username: String, todo: Todo) {
        withConnection { connection ->
            connection.prepareStatement(
                """
                    DELETE FROM dbo.Tasks
                    WHERE id = ? AND username = ?
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, todo.id)
                statement.setString(2, username)
                statement.executeUpdate()
            }
        }
    }

//    private fun initializeSchema() {
//        withConnection { connection ->
//            connection.createStatement().use { statement ->
//                statement.execute(
//                    """
//                        IF OBJECT_ID(N'dbo.Tasks', N'U') IS NULL
//                        BEGIN
//                            CREATE TABLE dbo.Tasks (
//                                id NVARCHAR(100) NOT NULL PRIMARY KEY,
//                                title NVARCHAR(255) NOT NULL,
//                                description NVARCHAR(MAX) NOT NULL,
//                                priority NVARCHAR(20) NOT NULL,
//                                completed BIT NOT NULL
//                            )
//                        END
//                    """.trimIndent(),
//                )
//            }
//        }
//    }

    private inline fun <T> withConnection(block: (Connection) -> T): T {
        val connection = if (username != null && password != null) {
            DriverManager.getConnection(jdbcUrl, username, password)
        } else {
            DriverManager.getConnection(jdbcUrl)
        }

        connection.use { openConnection ->
            return block(openConnection)
        }
    }

    private fun java.sql.ResultSet.toTodo(): Todo =
        Todo(
            id = getString("id"),
            username = getString("username"),
            title = getString("title"),
            description = getString("description"),
            priority = Priority.valueOf(getString("priority")),
            completed = getBoolean("completed"),
        )
}
