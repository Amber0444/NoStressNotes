package com.rus_euphoria.notes.data

import com.rus_euphoria.notes.data.local.LocalDataSource
import com.rus_euphoria.notes.data.remote.RemoteDataSource
import com.rus_euphoria.notes.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class NoteRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val externalScope: CoroutineScope,
) {
    private val log = LoggerFactory.getLogger(NoteRepository::class.java)

    val notes: Flow<List<Note>> = localDataSource.notes

    fun getNote(uid: String): Note? = localDataSource.getNote(uid)

    suspend fun createNote(note: Note) {
        localDataSource.saveNote(note)
        externalScope.launch {
            runCatching { remoteDataSource.addNote(note) }
                .onFailure { log.error("createNote: remote add failed uid=${note.uid}", it) }
        }
    }

    suspend fun updateNote(note: Note) {
        localDataSource.saveNote(note)
        externalScope.launch {
            runCatching { remoteDataSource.updateNote(note) }
                .onFailure { log.error("updateNote: remote update failed uid=${note.uid}", it) }
        }
    }

    suspend fun deleteNote(uid: String) {
        localDataSource.deleteNote(uid)
        externalScope.launch {
            runCatching { remoteDataSource.deleteNote(uid) }
                .onFailure { log.error("deleteNote: remote delete failed uid=$uid", it) }
        }
    }

    fun refreshFromRemote() {
        externalScope.launch {
            runCatching { remoteDataSource.fetchNotes() }
                .onSuccess { remoteNotes ->
                    val pinnedUids = localDataSource.notes.value
                        .filter { it.pinned }
                        .map { it.uid }
                        .toSet()
                    val merged = remoteNotes.map { note ->
                        if (note.uid in pinnedUids) note.copy(pinned = true) else note
                    }
                    log.info("refreshFromRemote: got ${remoteNotes.size} notes, preserved ${pinnedUids.size} pinned")
                    localDataSource.replaceAll(merged)
                }
                .onFailure { log.error("refreshFromRemote failed", it) }
        }
    }
}
