package com.rus_euphoria.notes.data.remote

import com.rus_euphoria.notes.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class RemoteDataSource(private val syncManager: SyncManager) {

    private val log = LoggerFactory.getLogger(RemoteDataSource::class.java)

    suspend fun fetchNotes(): List<Note> = withContext(Dispatchers.IO) {
        log.info("fetchNotes")
        syncManager.fetchAll()
    }

    suspend fun addNote(note: Note): Note = withContext(Dispatchers.IO) {
        log.info("addNote uid=${note.uid}")
        syncManager.addRemote(note)
    }

    suspend fun updateNote(note: Note): Note = withContext(Dispatchers.IO) {
        log.info("updateNote uid=${note.uid}")
        syncManager.updateRemote(note)
    }

    suspend fun deleteNote(uid: String) = withContext(Dispatchers.IO) {
        log.info("deleteNote uid=$uid")
        syncManager.deleteRemote(uid)
    }

    suspend fun pushAll(notes: List<Note>): List<Note> = withContext(Dispatchers.IO) {
        log.info("pushAll size=${notes.size}")
        syncManager.pushAll(notes)
    }
}
