package com.rus_euphoria.notes.data

import com.rus_euphoria.notes.model.Note
import com.rus_euphoria.notes.data.local.LocalDataSource
import com.rus_euphoria.notes.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    val notes: Flow<List<Note>> = localDataSource.notes

    fun getNote(uid: String): Note? = localDataSource.getNote(uid)

    suspend fun saveNote(note: Note) {
        localDataSource.saveNote(note)
        remoteDataSource.saveNote(note)
    }

    suspend fun deleteNote(uid: String) {
        localDataSource.deleteNote(uid)
        remoteDataSource.deleteNote(uid)
    }

    suspend fun refreshFromRemote() {
        val remoteNotes = remoteDataSource.fetchNotes()
        remoteNotes.forEach { localDataSource.saveNote(it) }
    }
}
