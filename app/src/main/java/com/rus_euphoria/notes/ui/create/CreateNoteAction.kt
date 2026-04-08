package com.rus_euphoria.notes.ui.create

sealed interface CreateNoteAction {
    data object NavigateBack : CreateNoteAction
}
