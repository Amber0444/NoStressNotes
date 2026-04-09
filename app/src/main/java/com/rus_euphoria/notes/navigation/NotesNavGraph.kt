package com.rus_euphoria.notes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.rus_euphoria.notes.data.NoteRepository
import com.rus_euphoria.notes.navigation.Screen.EditNote
import com.rus_euphoria.notes.ui.create.CreateNoteScreen
import com.rus_euphoria.notes.ui.create.CreateNoteViewModel
import com.rus_euphoria.notes.ui.edit.EditNoteScreen
import com.rus_euphoria.notes.ui.edit.EditNoteViewModel
import com.rus_euphoria.notes.ui.home.HomeScreen
import com.rus_euphoria.notes.ui.home.HomeViewModel

@Composable
fun NotesNavGraph(repository: NoteRepository) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Home> {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(repository)
                )
                HomeScreen(
                    viewModel = viewModel,
                    onNoteClick = { uid -> backStack.add(EditNote(uid)) },
                    onAddNote = { backStack.add(Screen.CreateNote) }
                )
            }

            entry<Screen.CreateNote> {
                val viewModel: CreateNoteViewModel = viewModel(
                    key = "create_note_${System.currentTimeMillis()}",
                    factory = CreateNoteViewModel.Factory(repository)
                )
                CreateNoteScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            entry<EditNote> { screen ->
                val viewModel: EditNoteViewModel = viewModel(
                    key = screen.noteUid,
                    factory = EditNoteViewModel.Factory(repository, screen.noteUid)
                )
                EditNoteScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
