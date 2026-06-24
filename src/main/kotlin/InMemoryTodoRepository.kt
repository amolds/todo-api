package com.olds

class InMemoryTodoRepository : TodoRepository {
    private val todos = mutableListOf<Todo>(
        Todo("1", "Learn Ktor", "Master Ktor framework", Priority.High),
        Todo("2", "Build API", "Create todo api", Priority.Medium),
    )

    override fun allTodos(): List<Todo> = todos

    override fun todoById(id: String) = todos.find { it.id == id }

    override fun todosByPriority(priority: Priority) = todos.filter { it.priority == priority }

    override fun addTodo(todo: Todo) {
        if (todoById(todo.id) != null) {
            throw IllegalStateException("Todo $todo.id already exists")
        }

        todos.add(todo)
    }

    override fun updateTodo(todo: Todo) {
        val index = todos.indexOfFirst { it.id == todo.id }
        if (index < 0) {
            throw IllegalStateException("Todo $todo.id does not exist")
        }

        todos[index] = todo
    }

    override fun removeTodo(todo: Todo) {
        todos.removeIf { it.id == todo.id }
    }
}