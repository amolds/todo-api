package com.olds.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.homeRoutes() {
    get("/") {
        call.respondText("Hello, World!")
    }

    get("/json/kotlinx-serialization") {
        call.respond(mapOf("hello" to "world"))
    }
}
