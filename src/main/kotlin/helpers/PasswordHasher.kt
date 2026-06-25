package com.olds.helpers

import org.mindrot.jbcrypt.BCrypt

interface PasswordHasher {
    fun hash(rawPassword: String): String
    fun matches(rawPassword: String, passwordHash: String): Boolean
}

class BCryptPasswordHasher : PasswordHasher {
    override fun hash(rawPassword: String): String = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

    override fun matches(rawPassword: String, passwordHash: String): Boolean = BCrypt.checkpw(rawPassword, passwordHash)
}
