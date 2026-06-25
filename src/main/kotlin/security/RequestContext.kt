package com.olds.security

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.util.AttributeKey

data class RequestContext(
    val username: String,
)

val RequestContextKey = AttributeKey<RequestContext>("RequestContext")

val RequestContextPlugin = createRouteScopedPlugin("RequestContextPlugin") {
    onCall { call ->
        val principal = call.principal<JWTPrincipal>() ?: return@onCall
        val username = principal.payload.subject
            ?: principal.payload.getClaim("username").asString()
            ?: return@onCall

        call.attributes.put(RequestContextKey, RequestContext(username))
    }
}

fun ApplicationCall.requestContext(): RequestContext? =
    if (attributes.contains(RequestContextKey)) attributes[RequestContextKey] else null

fun ApplicationCall.currentUsername(): String? =
    requestContext()?.username
        ?: principal<JWTPrincipal>()?.payload?.subject
        ?: principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString()
