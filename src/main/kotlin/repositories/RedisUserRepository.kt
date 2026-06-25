package com.olds.repositories

import com.olds.interfaces.UserRepository
import com.olds.models.User
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.StringCodec
import kotlinx.serialization.json.Json
import java.io.Closeable

class RedisUserRepository(
    private val redisUrl: String,
) : UserRepository, Closeable {
    private val json = Json { ignoreUnknownKeys = true }
    private var client: RedisClient? = null
    private val redisUri by lazy { RedisURI.create(redisUrl.toRedisUriString()) }

    override fun findByUsername(username: String): User? {
        return withConnection { connection ->
            val storedUser = connection.sync().get(key(username)) ?: return@withConnection null
            json.decodeFromString(User.serializer(), storedUser)
        }
    }

    override fun save(user: User) {
        withConnection { connection ->
            connection.sync().set(key(user.username), json.encodeToString(User.serializer(), user))
        }
    }

    private fun key(username: String): String = "users:$username"

    private inline fun <T> withConnection(block: (StatefulRedisConnection<String, String>) -> T): T {
        val redisClient = client ?: RedisClient.create(redisUri).also { client = it }
        redisClient.connect(StringCodec.UTF8, redisUri).use { connection ->
            return block(connection)
        }
    }

    override fun close() {
        client?.shutdown()
    }

    private fun String.toRedisUriString(): String =
        if (startsWith("redis://") || startsWith("rediss://")) this else "redis://$this"
}
