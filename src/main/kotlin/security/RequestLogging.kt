package com.olds.security

import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.util.AttributeKey
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.time.Duration
import java.time.Instant

private val RequestLogStartKey = AttributeKey<Instant>("RequestLogStartKey")

val RequestLoggingPlugin = createRouteScopedPlugin("RequestLoggingPlugin") {
    val logger = LoggerFactory.getLogger("com.olds.RequestLogging")

    on(AuthenticationChecked) { call ->
        val startTime = Instant.now()
        call.attributes.put(RequestLogStartKey, startTime)

        val username = call.currentUsername() ?: "anonymous"
        MDC.put("username", username)

        logger.info(
            "request started method={} path={} user={} startTime={}",
            call.request.httpMethod.value,
            call.request.path(),
            username,
            startTime,
        )
    }

    onCallRespond { call, _ ->
        val startTime = if (call.attributes.contains(RequestLogStartKey)) {
            call.attributes[RequestLogStartKey]
        } else {
            null
        } ?: return@onCallRespond

        val endTime = Instant.now()
        val username = call.currentUsername() ?: "anonymous"
        val duration = Duration.between(startTime, endTime).toMillis()

        logger.info(
            "request completed method={} path={} user={} startTime={} endTime={} durationMs={}",
            call.request.httpMethod.value,
            call.request.path(),
            username,
            startTime,
            endTime,
            duration,
        )

        MDC.remove("username")
    }
}
