package com.rus_euphoria.notes

import org.json.JSONArray
import java.io.File
import java.util.Date

class FileNotebook {

    private val _notes = mutableListOf<Note>()

    val notes: List<Note>
        get() {
            removeExpiredNotes()
            return _notes.toList()
        }

    fun add(note: Note) {
        _notes.removeAll { it.uid == note.uid }
        _notes.add(note)
    }

    fun remove(uid: String) {
        _notes.removeAll { it.uid == uid }
    }

    fun saveToFile(file: File) {
        removeExpiredNotes()
        val jsonArray = JSONArray()
        for (note in _notes) {
            jsonArray.put(note.json)
        }
        file.writeText(jsonArray.toString())
    }

    fun loadFromFile(file: File) {
        if (!file.exists()) return
        val jsonArray = JSONArray(file.readText())
        _notes.clear()
        for (i in 0 until jsonArray.length()) {
            val note = Note.parse(jsonArray.getJSONObject(i))
            if (note != null) {
                _notes.add(note)
            }
        }
        removeExpiredNotes()
    }

    private fun removeExpiredNotes() {
        val now = Date()
        _notes.removeAll { note ->
            note.selfDestructDate != null && note.selfDestructDate.before(now)
        }
    }
}
