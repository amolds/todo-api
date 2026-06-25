package com.olds.security

import io.ktor.server.application.Application

data class JwtConfig(
    val realm: String,
    val issuer: String,
    val audience: String,
    val secret: String,
)

fun Application.loadJwtConfig(): JwtConfig = JwtConfig(
    realm = environment.config.property("ktor.security.jwt.realm").getString(),
    issuer = environment.config.property("ktor.security.jwt.issuer").getString(),
    audience = environment.config.property("ktor.security.jwt.audience").getString(),
    secret = environment.config.property("ktor.security.jwt.secret").getString(),
)
