package com.rus_euphoria.notes.ui.home

import com.rus_euphoria.notes.model.Importance
import com.rus_euphoria.notes.model.Note

sealed interface NoteSection {
    val notes: List<Note>

    data class Pinned(override val notes: List<Note>) : NoteSection
    data class ByImportance(val importance: Importance, override val notes: List<Note>) : NoteSection
}

data class HomeState(
    val sections: List<NoteSection> = emptyList(),
    val totalCount: Int = 0,
    val greeting: String = ""
)
