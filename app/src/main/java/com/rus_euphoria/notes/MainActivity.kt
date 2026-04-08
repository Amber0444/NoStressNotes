package com.rus_euphoria.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rus_euphoria.notes.navigation.NotesNavGraph
import com.rus_euphoria.notes.ui.theme.NotesAppTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private val notebook = FileNotebook()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val file = File(filesDir, "notes.json")

        setContent {
            NotesAppTheme {
                NotesNavGraph(
                    notebook = notebook,
                    file = file
                )
            }
        }
    }
}