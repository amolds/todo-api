package com.olds.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val passwordHash: String,
)
