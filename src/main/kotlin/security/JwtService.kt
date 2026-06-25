package com.olds.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(private val config: JwtConfig) {
    private val algorithm = Algorithm.HMAC256(config.secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .build()

    fun createToken(username: String): String = JWT.create()
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .withSubject(username)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000))
        .sign(algorithm)
}
