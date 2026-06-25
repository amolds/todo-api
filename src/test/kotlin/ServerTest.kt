package com.olds

import com.olds.routes.LoginResponse
import com.olds.routes.CurrentUserResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.*

class ServerTest {

    @Test
    fun `test todo routes require a bearer token`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ktor.security.jwt.realm" to "todo-api",
                "ktor.security.jwt.issuer" to "todo-api",
                "ktor.security.jwt.audience" to "todo-api",
                "ktor.security.jwt.secret" to "test-secret",
            )
        }

        application {
            module()
        }

        assertEquals(HttpStatusCode.Unauthorized, client.get("/todos").status)
    }

    @Test
    fun `test register login returns token and unlocks todo routes`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "ktor.security.jwt.realm" to "todo-api",
                "ktor.security.jwt.issuer" to "todo-api",
                "ktor.security.jwt.audience" to "todo-api",
                "ktor.security.jwt.secret" to "test-secret",
            )
        }

        application {
            module()
        }

        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"jane","password":"secret"}""")
        }

        assertEquals(HttpStatusCode.Created, registerResponse.status)

        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"jane","password":"secret"}""")
        }

        assertEquals(HttpStatusCode.OK, loginResponse.status)

        val token = Json.decodeFromString(LoginResponse.serializer(), loginResponse.bodyAsText()).token

        assertEquals(
            HttpStatusCode.OK,
            client.get("/todos") {
                headers.append(HttpHeaders.Authorization, "Bearer $token")
            }.status,
        )

        assertEquals(
            CurrentUserResponse(username = "jane"),
            Json.decodeFromString(
                CurrentUserResponse.serializer(),
                client.get("/auth/me") {
                    headers.append(HttpHeaders.Authorization, "Bearer $token")
                }.bodyAsText(),
            ),
        )
    }
}
