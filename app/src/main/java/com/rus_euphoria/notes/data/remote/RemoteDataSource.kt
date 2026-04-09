package com.rus_euphoria.notes.data.remote

import com.rus_euphoria.notes.model.Note
import org.slf4j.LoggerFactory

class RemoteDataSource {

    private val log = LoggerFactory.getLogger(RemoteDataSource::class.java)

    suspend fun fetchNotes(): List<Note> {
        log.info("fetchNotes: loading notes from backend (stub)")
        return emptyList()
    }

    suspend fun saveNote(note: Note) {
        log.info("pushNote: sending note uid=${note.uid}, title='${note.title}' to backend (stub)")
    }

    suspend fun deleteNote(uid: String) {
        log.info("deleteNote: deleting note uid=$uid from backend (stub)")
    }
}
