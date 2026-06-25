package com.olds.routes

import com.olds.models.Todo
import com.olds.interfaces.TodoRepository
import com.olds.security.currentUsername
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.authenticate

fun Route.todoRoutes(todoRepository: TodoRepository) {
    authenticate("auth-jwt") {
        get("/todos") {
            call.respond(todoRepository.allTodos())
        }

        get("/todos/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing id")
                return@get
            }

            val todo = todoRepository.todoById(id) ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(todo)
        }

        post("/todos") {
            try {
                val todo = call.receive<Todo>()
                if (todoRepository.todoById(todo.id) != null) {
                    call.respond(HttpStatusCode.Conflict, "Todo ${todo.id} already exists")
                    return@post
                }

                todoRepository.addTodo(todo)
                call.respond(HttpStatusCode.Created, todo)
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        put("/todos/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing id")
                return@put
            }

            try {
                val todo = call.receive<Todo>()
                if (todoRepository.todoById(todo.id) == null) {
                    call.respond(HttpStatusCode.NotFound, "Todo ${todo.id} not found")
                    return@put
                }

                todoRepository.updateTodo(todo)
                call.respond(HttpStatusCode.OK, todo)
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            }
        }

        delete("/todos/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing id")
                return@delete
            }

            val todo = todoRepository.todoById(id) ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            todoRepository.removeTodo(todo)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
