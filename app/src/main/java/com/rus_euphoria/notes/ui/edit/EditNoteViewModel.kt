package com.rus_euphoria.notes.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rus_euphoria.notes.model.Note
import com.rus_euphoria.notes.data.NoteRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNoteViewModel(
    private val repository: NoteRepository,
    noteUid: String
) : ViewModel() {

    private val _state = MutableStateFlow(EditNoteState())
    val state: StateFlow<EditNoteState> = _state.asStateFlow()

    private val _action = Channel<EditNoteAction>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()

    init {
        val note = repository.getNote(noteUid)
        if (note != null) {
            _state.value = EditNoteState(
                uid = note.uid,
                title = note.title,
                content = note.content,
                color = note.color,
                importance = note.importance,
                selfDestructEnabled = note.selfDestructDate != null,
                selfDestructDate = note.selfDestructDate
            )
        }
    }

    fun onEvent(event: EditNoteEvent) {
        when (event) {
            is EditNoteEvent.TitleChanged -> _state.update { it.copy(title = event.title) }
            is EditNoteEvent.ContentChanged -> _state.update { it.copy(content = event.content) }
            is EditNoteEvent.ColorSelected -> _state.update { it.copy(color = event.color) }
            is EditNoteEvent.ImportanceSelected -> _state.update { it.copy(importance = event.importance) }
            is EditNoteEvent.SelfDestructToggled -> _state.update {
                it.copy(
                    selfDestructEnabled = event.enabled,
                    selfDestructDate = if (!event.enabled) null else it.selfDestructDate
                )
            }
            is EditNoteEvent.SelfDestructDateChanged -> _state.update { it.copy(selfDestructDate = event.date) }
            is EditNoteEvent.CustomColorReceived -> _state.update {
                it.copy(customColor = event.color, color = event.color, showColorPicker = false)
            }
            is EditNoteEvent.OpenColorPicker -> _state.update { it.copy(showColorPicker = true) }
            is EditNoteEvent.CloseColorPicker -> _state.update { it.copy(showColorPicker = false) }
            is EditNoteEvent.SaveClicked -> save()
            is EditNoteEvent.DeleteClicked -> delete()
        }
    }

    private fun save() {
        val s = _state.value
        if (s.title.isBlank()) return
        val note = Note(
            uid = s.uid,
            title = s.title,
            content = s.content,
            color = s.color,
            importance = s.importance,
            selfDestructDate = if (s.selfDestructEnabled) s.selfDestructDate else null
        )
        viewModelScope.launch {
            repository.saveNote(note)
            _action.trySend(EditNoteAction.NavigateBack)
        }
    }

    private fun delete() {
        viewModelScope.launch {
            repository.deleteNote(_state.value.uid)
            _action.trySend(EditNoteAction.NavigateBack)
        }
    }

    class Factory(
        private val repository: NoteRepository,
        private val noteUid: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditNoteViewModel(repository, noteUid) as T
        }
    }
}
