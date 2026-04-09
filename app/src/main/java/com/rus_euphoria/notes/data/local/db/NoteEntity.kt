package com.rus_euphoria.notes.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rus_euphoria.notes.model.Importance
import com.rus_euphoria.notes.model.Note
import java.util.Date

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val uid: String,
    val title: String,
    val content: String,
    val color: Int,
    val importance: Importance,
    val selfDestructDate: Date?,
    val pinned: Boolean = false,
)

fun NoteEntity.toDomain(): Note = Note(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
    selfDestructDate = selfDestructDate,
    pinned = pinned,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
    selfDestructDate = selfDestructDate,
    pinned = pinned,
)
