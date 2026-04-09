package com.rus_euphoria.notes.data.remote.api.models

import kotlinx.serialization.Serializable

@Serializable
data class ListResponse(
    val status: String,
    val list: List<TodoItemDto>,
    val revision: Int,
)

@Serializable
data class ElementResponse(
    val status: String,
    val element: TodoItemDto,
    val revision: Int,
)

@Serializable
data class ElementRequest(
    val element: TodoItemDto,
)

@Serializable
data class PatchListRequest(
    val list: List<TodoItemDto>,
)

@Serializable
data class ErrorResponse(
    val status: String,
    val message: String? = null,
)
