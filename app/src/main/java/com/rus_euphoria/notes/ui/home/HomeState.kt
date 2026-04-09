package com.rus_euphoria.notes.ui.home

import com.rus_euphoria.notes.model.Note

data class HomeState(
    val notes: List<Note> = emptyList(),
    val greeting: String = ""
)
