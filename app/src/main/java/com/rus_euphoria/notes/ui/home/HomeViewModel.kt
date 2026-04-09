package com.rus_euphoria.notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rus_euphoria.notes.data.NoteRepository
import com.rus_euphoria.notes.model.Importance
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _action = Channel<HomeAction>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.notes.collect { notes ->
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val greeting = when {
                    hour < 6 -> "Good night"
                    hour < 12 -> "Good morning"
                    hour < 18 -> "Good afternoon"
                    else -> "Good evening"
                }
                val sectionOrder = listOf(Importance.HIGH, Importance.NORMAL, Importance.LOW)
                val grouped = notes.groupBy { it.importance }
                val sections = sectionOrder
                    .mapNotNull { imp -> grouped[imp]?.let { NoteSection(imp, it) } }
                _state.update {
                    it.copy(
                        sections = sections,
                        totalCount = notes.size,
                        greeting = greeting
                    )
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NoteClicked -> _action.trySend(HomeAction.NavigateToNote(event.uid))
            is HomeEvent.NoteDeleted -> viewModelScope.launch { repository.deleteNote(event.uid) }
            is HomeEvent.AddNoteClicked -> _action.trySend(HomeAction.NavigateToNewNote)
        }
    }

    class Factory(
        private val repository: NoteRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
