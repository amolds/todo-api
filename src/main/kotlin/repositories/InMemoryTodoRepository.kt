package com.olds.repositories

import com.olds.models.Priority
import com.olds.models.Todo
import com.olds.interfaces.TodoRepository

class InMemoryTodoRepository : TodoRepository {
    private val todos = mutableListOf<Todo>(
        Todo("1", "user1", "Learn Ktor", "Master Ktor framework", Priority.High),
        Todo("2", "user2", "Build API", "Create todo api", Priority.Medium),
    )

    override fun allTodos(username: String): List<Todo> = todos.filter { it.username == username }

    override fun todoById(id: String, username: String) = todos.find { it.id == id && it.username == username }

    override fun todosByPriority(username: String, priority: Priority) = todos.filter { it.username == username && it.priority == priority }

    override fun addTodo(username: String, todo: Todo) {
        todo.username = username
        if (todoById(todo.id, username) != null) {
            throw IllegalStateException("Todo $todo.id already exists")
        }

        todos.add(todo)
    }

    override fun updateTodo(username: String, todo: Todo) {
        todo.username = username
        val index = todos.indexOfFirst { it.id == todo.id && it.username == username }
        if (index < 0) {
            throw IllegalStateException("Todo $todo.id does not exist")
        }

        todos[index] = todo
    }

    override fun completeTodo(username: String, id: String) {
        val index = todos.indexOfFirst { it.id == id && it.username == username }
        if (index < 0) {
            throw IllegalStateException("Todo $id does not exist")
        }

        val todo = todos[index]
        todos[index] = todo.copy(completed = true)
    }

    override fun removeTodo(username: String, todo: Todo) {
        todos.removeIf { it.id == todo.id && it.username == username }
    }
}