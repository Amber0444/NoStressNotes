package com.rus_euphoria.notes.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rus_euphoria.notes.ui.home.components.NoteGrid
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNoteClick: (String) -> Unit,
    onAddNote: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var noteToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.action.collectLatest { action ->
            when (action) {
                is HomeAction.NavigateToNote -> onNoteClick(action.uid)
                is HomeAction.NavigateToNewNote -> onAddNote()
            }
        }
    }

    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Delete note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(HomeEvent.NoteDeleted(noteToDelete!!))
                    noteToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(HomeEvent.AddNoteClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add note")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GreetingSection(
                greeting = state.greeting,
                noteCount = state.totalCount
            )

            NoteGrid(
                sections = state.sections,
                onNoteClick = { uid -> viewModel.onEvent(HomeEvent.NoteClicked(uid)) },
                onNoteDelete = { uid -> noteToDelete = uid },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GreetingSection(greeting: String, noteCount: Int) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (noteCount == 0) "No notes yet" else "You have $noteCount notes",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
