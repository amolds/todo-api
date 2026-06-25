package com.olds.repositories

import com.olds.models.User
import com.olds.interfaces.UserRepository

class InMemoryUserRepository(
    initialUsers: List<User> = emptyList(),
) : UserRepository {
    private val users = initialUsers.associateBy { it.username }.toMutableMap()

    override fun findByUsername(username: String): User? = users[username]

    override fun save(user: User) {
        users[user.username] = user
    }
}
