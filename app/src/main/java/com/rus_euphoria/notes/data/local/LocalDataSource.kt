package com.rus_euphoria.notes.data.local

import com.rus_euphoria.notes.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

class LocalDataSource(private val file: File) {

    private val notebook = FileNotebook()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: Flow<List<Note>> = _notes.asStateFlow()

    init {
        notebook.loadFromFile(file)
        _notes.value = notebook.notes
    }

    fun getNote(uid: String): Note? = notebook.notes.find { it.uid == uid }

    suspend fun saveNote(note: Note) = withContext(Dispatchers.IO) {
        notebook.add(note)
        notebook.saveToFile(file)
        _notes.value = notebook.notes
    }

    suspend fun deleteNote(uid: String) = withContext(Dispatchers.IO) {
        notebook.remove(uid)
        notebook.saveToFile(file)
        _notes.value = notebook.notes
    }

    suspend fun replaceAll(notes: List<Note>) = withContext(Dispatchers.IO) {
        notebook.replaceAll(notes)
        notebook.saveToFile(file)
        _notes.value = notebook.notes
    }
}
