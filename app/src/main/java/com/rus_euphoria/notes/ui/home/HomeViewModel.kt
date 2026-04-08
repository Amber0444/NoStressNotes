package com.rus_euphoria.notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rus_euphoria.notes.FileNotebook
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.Calendar

class HomeViewModel(
    private val notebook: FileNotebook,
    private val file: File
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _action = Channel<HomeAction>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()

    init {
        notebook.loadFromFile(file)
        refresh()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> refresh()
            is HomeEvent.NoteClicked -> _action.trySend(HomeAction.NavigateToNote(event.uid))
            is HomeEvent.NoteDeleted -> deleteNote(event.uid)
            is HomeEvent.AddNoteClicked -> _action.trySend(HomeAction.NavigateToNewNote)
        }
    }

    private fun deleteNote(uid: String) {
        notebook.remove(uid)
        notebook.saveToFile(file)
        refresh()
    }

    private fun refresh() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 6 -> "Good night"
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
        _state.update {
            it.copy(
                notes = notebook.notes,
                greeting = greeting
            )
        }
    }

    class Factory(
        private val notebook: FileNotebook,
        private val file: File
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(notebook, file) as T
        }
    }
}
