package com.olds.dto

import com.olds.models.Priority
import kotlinx.serialization.Serializable

@Serializable
data class CreateTodoRequest(
    val id: String? = null,
    val title: String,
    val description: String,
    val priority: Priority,
    val completed: Boolean = false,
)
