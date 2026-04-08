package com.rus_euphoria.notes.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rus_euphoria.notes.FileNotebook
import com.rus_euphoria.notes.Note
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.UUID

class CreateNoteViewModel(
    private val notebook: FileNotebook,
    private val file: File
) : ViewModel() {

    private val _state = MutableStateFlow(CreateNoteState())
    val state: StateFlow<CreateNoteState> = _state.asStateFlow()

    private val _action = Channel<CreateNoteAction>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()

    fun onEvent(event: CreateNoteEvent) {
        when (event) {
            is CreateNoteEvent.TitleChanged -> _state.update { it.copy(title = event.title) }
            is CreateNoteEvent.ContentChanged -> _state.update { it.copy(content = event.content) }
            is CreateNoteEvent.ColorSelected -> _state.update { it.copy(color = event.color) }
            is CreateNoteEvent.ImportanceSelected -> _state.update { it.copy(importance = event.importance) }
            is CreateNoteEvent.SelfDestructToggled -> _state.update {
                it.copy(
                    selfDestructEnabled = event.enabled,
                    selfDestructDate = if (!event.enabled) null else it.selfDestructDate
                )
            }
            is CreateNoteEvent.SelfDestructDateChanged -> _state.update { it.copy(selfDestructDate = event.date) }
            is CreateNoteEvent.CustomColorReceived -> _state.update {
                it.copy(customColor = event.color, color = event.color, showColorPicker = false)
            }
            is CreateNoteEvent.OpenColorPicker -> _state.update { it.copy(showColorPicker = true) }
            is CreateNoteEvent.CloseColorPicker -> _state.update { it.copy(showColorPicker = false) }
            is CreateNoteEvent.SaveClicked -> save()
        }
    }

    private fun save() {
        val s = _state.value
        if (s.title.isBlank()) return
        val note = Note(
            uid = UUID.randomUUID().toString(),
            title = s.title,
            content = s.content,
            color = s.color,
            importance = s.importance,
            selfDestructDate = if (s.selfDestructEnabled) s.selfDestructDate else null
        )
        notebook.add(note)
        notebook.saveToFile(file)
        _action.trySend(CreateNoteAction.NavigateBack)
    }

    class Factory(
        private val notebook: FileNotebook,
        private val file: File
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateNoteViewModel(notebook, file) as T
        }
    }
}
