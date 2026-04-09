package com.rus_euphoria.notes.ui.home

import com.rus_euphoria.notes.model.Importance
import com.rus_euphoria.notes.model.Note

data class NoteSection(
    val importance: Importance,
    val notes: List<Note>
)

data class HomeState(
    val sections: List<NoteSection> = emptyList(),
    val totalCount: Int = 0,
    val greeting: String = ""
)
