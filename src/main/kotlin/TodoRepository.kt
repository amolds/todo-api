package com.olds

interface TodoRepository {
    fun allTodos(): List<Todo>
    fun todoById(id: String): Todo?
    fun todosByPriority(priority: Priority): List<Todo>
    fun addTodo(todo: Todo)
    fun updateTodo(todo: Todo)
    fun removeTodo(todo: Todo)
}

