package com.rus_euphoria.notes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.rus_euphoria.notes.FileNotebook
import com.rus_euphoria.notes.navigation.Screen.EditNote
import com.rus_euphoria.notes.ui.create.CreateNoteScreen
import com.rus_euphoria.notes.ui.create.CreateNoteViewModel
import com.rus_euphoria.notes.ui.edit.EditNoteScreen
import com.rus_euphoria.notes.ui.edit.EditNoteViewModel
import com.rus_euphoria.notes.ui.home.HomeScreen
import com.rus_euphoria.notes.ui.home.HomeViewModel
import java.io.File

@Composable
fun NotesNavGraph(
    notebook: FileNotebook,
    file: File
) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Home> {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(notebook, file)
                )
                HomeScreen(
                    viewModel = viewModel,
                    onNoteClick = { uid -> backStack.add(EditNote(uid)) },
                    onAddNote = { backStack.add(Screen.CreateNote) }
                )
            }

            entry<Screen.CreateNote> {
                val viewModel: CreateNoteViewModel = viewModel(
                    factory = CreateNoteViewModel.Factory(notebook, file)
                )
                CreateNoteScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            entry<EditNote> { screen ->
                val viewModel: EditNoteViewModel = viewModel(
                    key = screen.noteUid,
                    factory = EditNoteViewModel.Factory(notebook, file, screen.noteUid)
                )
                EditNoteScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
