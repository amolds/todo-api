package com.olds

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.olds.routes.authRoutes
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import com.olds.routes.homeRoutes
import com.olds.routes.todoRoutes
import com.olds.security.JwtService
import com.olds.security.configureSecurity
import com.olds.security.loadJwtConfig
import com.olds.helpers.BCryptPasswordHasher
import com.olds.helpers.PasswordHasher
import com.olds.interfaces.TodoRepository
import com.olds.interfaces.UserRepository
import com.olds.models.User
import com.olds.repositories.InMemoryTodoRepository
import com.olds.repositories.InMemoryUserRepository
import com.olds.security.RequestContextPlugin
import com.olds.security.RequestLoggingPlugin

fun Application.module() {
    val jwtConfig = loadJwtConfig()
    val appModule = module {
        single<TodoRepository> { InMemoryTodoRepository() }
        single<PasswordHasher> { BCryptPasswordHasher() }
        single<UserRepository> {
            val hasher = get<PasswordHasher>()
            InMemoryUserRepository(
                listOf(
                    User(
                        username = "admin",
                        passwordHash = hasher.hash("admin"),
                    ),
                ),
            )
        }
        single { jwtConfig }
        single { JwtService(get()) }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    configureSerialization()
    configureSecurity(jwtConfig = jwtConfig, jwtService = get())
    configureRouting(todoRepository = get(), userRepository = get(), passwordHasher = get(), jwtService = get())
}

fun Application.configureRouting(
    todoRepository: TodoRepository,
    userRepository: UserRepository,
    passwordHasher: PasswordHasher,
    jwtService: JwtService,
) {
    routing {
        install(RequestContextPlugin)
        install(RequestLoggingPlugin)

        homeRoutes()
        authRoutes(userRepository = userRepository, passwordHasher = passwordHasher, jwtService = jwtService)
        todoRoutes(todoRepository)
    }
}