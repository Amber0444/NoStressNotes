package com.rus_euphoria.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rus_euphoria.notes.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        LeakyHandler(this).startLeaking()

        val notebook = FileNotebook()
        notebook.add(Note(title = "Первая заметка", content = "Содержимое первой заметки"))
        notebook.add(Note(title = "Вторая заметка", content = "Содержимое второй заметки"))

        setContent {
            NotesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NoteList(
                        notes = notebook.notes,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteList(notes: List<Note>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes) { note ->
            NoteCard(note)
        }
    }
}

@Composable
private fun NoteCard(note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = note.title,
            modifier = Modifier.padding(16.dp)
        )
    }
}