package com.olds

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val priority: Priority,
    val completed: Boolean = false,
)
