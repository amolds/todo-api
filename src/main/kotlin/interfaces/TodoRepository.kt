package com.olds.interfaces

import com.olds.models.Priority
import com.olds.models.Todo

interface TodoRepository {
    fun allTodos(username: String): List<Todo>
    fun todoById(id: String, username: String): Todo?
    fun todosByPriority(username: String, priority: Priority): List<Todo>
    fun addTodo(username: String, todo: Todo)
    fun updateTodo(username: String, todo: Todo)
    fun completeTodo(username: String, id: String)
    fun removeTodo(username: String, todo: Todo)
}
