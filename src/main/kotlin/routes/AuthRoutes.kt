package com.olds.routes

import com.olds.interfaces.UserRepository
import com.olds.helpers.PasswordHasher
import com.olds.models.User
import com.olds.security.RequestContextPlugin
import com.olds.security.RequestLoggingPlugin
import com.olds.security.currentUsername
import com.olds.security.JwtService
import kotlinx.serialization.Serializable
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.auth.authenticate

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
)

@Serializable
data class RegisterResponse(
    val username: String,
    val token: String,
)

@Serializable
data class CurrentUserResponse(
    val username: String,
)

fun Route.authRoutes(
    userRepository: UserRepository,
    passwordHasher: PasswordHasher,
    jwtService: JwtService,
) {
    post("/auth/register") {
        val request = call.receive<RegisterRequest>()

        if (userRepository.findByUsername(request.username) != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return@post
        }

        userRepository.save(
            User(
                username = request.username,
                passwordHash = passwordHasher.hash(request.password),
            ),
        )

        call.respond(HttpStatusCode.Created, RegisterResponse(username = request.username, token = jwtService.createToken(request.username)))
    }

    authenticate("auth-jwt") {
        install(RequestLoggingPlugin)
        install(RequestContextPlugin)

        get("/auth/me") {
            val username = call.currentUsername()
            if (username == null) {
                call.respond(HttpStatusCode.Unauthorized, "Missing user identity")
                return@get
            }

            call.respond(CurrentUserResponse(username = username))
        }
    }

    post("/auth/login") {
        val request = call.receive<LoginRequest>()

        val user = userRepository.findByUsername(request.username)
        if (user == null || !passwordHasher.matches(request.password, user.passwordHash)) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
            return@post
        }

        call.respond(LoginResponse(token = jwtService.createToken(request.username)))
    }
}
