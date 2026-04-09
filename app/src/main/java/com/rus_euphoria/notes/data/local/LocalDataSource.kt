package com.rus_euphoria.notes.data.local

import com.rus_euphoria.notes.data.local.db.NoteDao
import com.rus_euphoria.notes.data.local.db.toDomain
import com.rus_euphoria.notes.data.local.db.toEntity
import com.rus_euphoria.notes.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LocalDataSource(
    private val dao: NoteDao,
    externalScope: CoroutineScope,
) {
    val notes: StateFlow<List<Note>> = dao.observeAll()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    fun getNote(uid: String): Note? = notes.value.find { it.uid == uid }

    suspend fun saveNote(note: Note) {
        dao.upsert(note.toEntity())
    }

    suspend fun deleteNote(uid: String) {
        dao.deleteByUid(uid)
    }

    suspend fun replaceAll(notes: List<Note>) {
        dao.replaceAll(notes.map { it.toEntity() })
    }
}
