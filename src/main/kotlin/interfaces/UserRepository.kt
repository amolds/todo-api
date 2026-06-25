package com.olds.interfaces

import com.olds.models.User

interface UserRepository {
    fun findByUsername(username: String): User?
    fun save(user: User)
}
