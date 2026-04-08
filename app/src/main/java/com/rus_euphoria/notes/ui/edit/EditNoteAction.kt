package com.rus_euphoria.notes.ui.edit

sealed interface EditNoteAction {
    data object NavigateBack : EditNoteAction
}
