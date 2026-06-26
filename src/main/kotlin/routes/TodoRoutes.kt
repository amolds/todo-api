package com.olds.routes

import com.olds.dto.CreateTodoRequest
import com.olds.models.Todo
import com.olds.models.Priority
import com.olds.interfaces.TodoRepository
import com.olds.security.requireUsernameOrUnauthorized
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.authenticate
import java.util.UUID

fun Route.todoRoutes(todoRepository: TodoRepository) {
    authenticate("auth-jwt") {
        get("/todos") {
            call.requireUsernameOrUnauthorized { username ->
                call.respond(todoRepository.allTodos(username))
            }
        }

        get("/todos/{id}") {
            call.requireUsernameOrUnauthorized { username ->
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Missing id")
                    return@requireUsernameOrUnauthorized
                }

                val todo = todoRepository.todoById(id, username) ?: run {
                    call.respond(HttpStatusCode.NotFound)
                    return@requireUsernameOrUnauthorized
                }

                call.respond(todo)
            }
        }

        post("/todos") {
            call.requireUsernameOrUnauthorized { username ->
                try {
                    val request = call.receive<CreateTodoRequest>()
                    val todo = Todo(
                        id = request.id ?: UUID.randomUUID().toString(),
                        username = username,
                        title = request.title,
                        description = request.description,
                        priority = request.priority,
                        completed = request.completed,
                    )
                    if (todoRepository.todoById(todo.id, username) != null) {
                        call.respond(HttpStatusCode.Conflict, "Todo ${todo.id} already exists")
                        return@requireUsernameOrUnauthorized
                    }

                    todoRepository.addTodo(username, todo)
                    call.respond(HttpStatusCode.Created, todo)
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }

        put("/todos/{id}") {
            call.requireUsernameOrUnauthorized { username ->
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Missing id")
                    return@requireUsernameOrUnauthorized
                }

                try {
                    val todo = call.receive<Todo>()
                    if (todo.id != id) {
                        call.respond(HttpStatusCode.BadRequest, "Path id does not match todo id")
                        return@requireUsernameOrUnauthorized
                    }

                    if (todoRepository.todoById(todo.id, username) == null) {
                        call.respond(HttpStatusCode.NotFound, "Todo ${todo.id} not found")
                        return@requireUsernameOrUnauthorized
                    }

                    todoRepository.updateTodo(username, todo)
                    call.respond(HttpStatusCode.OK, todo)
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                }
            }
        }

        patch("/todos/{id}/complete") {
            call.requireUsernameOrUnauthorized { username ->
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Missing id")
                    return@requireUsernameOrUnauthorized
                }

                if (todoRepository.todoById(id, username) == null) {
                    call.respond(HttpStatusCode.NotFound, "Todo $id not found")
                    return@requireUsernameOrUnauthorized
                }

                todoRepository.completeTodo(username, id)
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/todos/{id}") {
            call.requireUsernameOrUnauthorized { username ->
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Missing id")
                    return@requireUsernameOrUnauthorized
                }

                val todo = todoRepository.todoById(id, username) ?: run {
                    call.respond(HttpStatusCode.NotFound)
                    return@requireUsernameOrUnauthorized
                }

                todoRepository.removeTodo(username, todo)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
