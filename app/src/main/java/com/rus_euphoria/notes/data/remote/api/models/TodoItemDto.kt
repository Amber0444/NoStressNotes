package com.rus_euphoria.notes.data.remote.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoItemDto(
    val id: String,
    val text: String,
    val importance: String,
    val deadline: Long? = null,
    val done: Boolean = false,
    val color: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long,
    @SerialName("last_updated_by") val lastUpdatedBy: String,
)
