package com.rus_euphoria.notes.data.remote.api.models

import android.graphics.Color
import com.rus_euphoria.notes.model.Importance
import com.rus_euphoria.notes.model.Note
import java.util.Date

private const val TITLE_BODY_SEPARATOR = "\n"

fun TodoItemDto.toNote(): Note {
    val parts = text.split(TITLE_BODY_SEPARATOR, limit = 2)
    val parsedTitle = parts[0]
    val parsedContent = parts.getOrNull(1).orEmpty()
    val parsedColor = color
        ?.let { runCatching { Color.parseColor(it) }.getOrNull() }
        ?: Color.WHITE
    val parsedImportance = when (importance.lowercase()) {
        "low" -> Importance.LOW
        "important" -> Importance.HIGH
        else -> Importance.NORMAL
    }
    val parsedDeadline = deadline?.let { Date(it) }
    return Note(
        uid = id,
        title = parsedTitle,
        content = parsedContent,
        color = parsedColor,
        importance = parsedImportance,
        selfDestructDate = parsedDeadline,
    )
}

fun Note.toDto(deviceId: String, now: Long = System.currentTimeMillis()): TodoItemDto {
    val packedText = if (content.isBlank()) title else "$title$TITLE_BODY_SEPARATOR$content"
    val colorString = if (color == Color.WHITE) {
        null
    } else {
        String.format("#%06X", 0xFFFFFF and color)
    }
    val importanceString = when (importance) {
        Importance.LOW -> "low"
        Importance.NORMAL -> "basic"
        Importance.HIGH -> "important"
    }
    return TodoItemDto(
        id = uid,
        text = packedText,
        importance = importanceString,
        deadline = selfDestructDate?.time,
        done = false,
        color = colorString,
        createdAt = now,
        changedAt = now,
        lastUpdatedBy = deviceId,
    )
}
