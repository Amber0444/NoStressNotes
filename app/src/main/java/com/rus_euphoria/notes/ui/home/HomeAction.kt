package com.rus_euphoria.notes.ui.home

sealed interface HomeAction {
    data class NavigateToNote(val uid: String) : HomeAction
    data object NavigateToNewNote : HomeAction
}
