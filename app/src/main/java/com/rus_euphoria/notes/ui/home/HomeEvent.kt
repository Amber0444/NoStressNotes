package com.rus_euphoria.notes.ui.home

sealed interface HomeEvent {
    data class NoteClicked(val uid: String) : HomeEvent
    data object AddNoteClicked : HomeEvent
}
