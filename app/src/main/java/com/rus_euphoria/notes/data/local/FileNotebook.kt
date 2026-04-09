package com.rus_euphoria.notes.data.local

import com.rus_euphoria.notes.model.Note
import com.rus_euphoria.notes.model.json
import com.rus_euphoria.notes.model.parse
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Date

class FileNotebook {

    private val log = LoggerFactory.getLogger(FileNotebook::class.java)

    private val _notes = mutableListOf<Note>()

    val notes: List<Note>
        get() {
            removeExpiredNotes()
            return _notes.toList()
        }

    fun add(note: Note) {
        val existed = _notes.removeAll { it.uid == note.uid }
        _notes.add(note)
        if (existed) {
            log.info("Updated note: uid=${note.uid}, title='${note.title}'")
        } else {
            log.info("Added note: uid=${note.uid}, title='${note.title}'")
        }
    }

    fun remove(uid: String) {
        val removed = _notes.removeAll { it.uid == uid }
        if (removed) {
            log.info("Removed note: uid=$uid")
        } else {
            log.warn("Note not found for removal: uid=$uid")
        }
    }

    fun replaceAll(notes: List<Note>) {
        _notes.clear()
        _notes.addAll(notes)
        log.info("Replaced notebook with ${notes.size} notes")
    }

    fun saveToFile(file: File) {
        removeExpiredNotes()
        val jsonArray = JSONArray()
        for (note in _notes) {
            jsonArray.put(note.json)
        }
        file.writeText(jsonArray.toString())
        log.info("Saved ${_notes.size} notes to ${file.name}")
    }

    fun loadFromFile(file: File) {
        if (!file.exists()) {
            log.warn("File not found: ${file.name}")
            return
        }
        val jsonArray = JSONArray(file.readText())
        _notes.clear()
        for (i in 0 until jsonArray.length()) {
            val note = Note.Companion.parse(jsonArray.getJSONObject(i))
            if (note != null) {
                _notes.add(note)
            } else {
                log.error("Failed to parse note at index $i")
            }
        }
        log.info("Loaded ${_notes.size} notes from ${file.name}")
        removeExpiredNotes()
    }

    private fun removeExpiredNotes() {
        val now = Date()
        val expired = _notes.filter { note ->
            note.selfDestructDate != null && note.selfDestructDate.before(now)
        }
        if (expired.isNotEmpty()) {
            log.info("Removing ${expired.size} expired notes")
            _notes.removeAll(expired.toSet())
        }
    }
}