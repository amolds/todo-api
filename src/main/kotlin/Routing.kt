package com.olds

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import com.olds.routes.homeRoutes
import com.olds.routes.todoRoutes

private val todoModule = module {
    single<TodoRepository> { InMemoryTodoRepository() }
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(todoModule)
    }

    configureSerialization()
    configureRouting(todoRepository = get())
}

fun Application.configureRouting(
    todoRepository: TodoRepository,
) {
    routing {
        homeRoutes()
        todoRoutes(todoRepository)
    }
}