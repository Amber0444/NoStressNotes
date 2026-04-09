package com.rus_euphoria.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rus_euphoria.notes.data.NoteRepository
import com.rus_euphoria.notes.data.local.LocalDataSource
import com.rus_euphoria.notes.data.remote.RemoteDataSource
import com.rus_euphoria.notes.navigation.NotesNavGraph
import com.rus_euphoria.notes.ui.theme.NotesAppTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        val file = File(filesDir, "notes.json")
        val localDataSource = LocalDataSource(file)
        val remoteDataSource = RemoteDataSource()
        NoteRepository(localDataSource, remoteDataSource)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotesAppTheme {
                NotesNavGraph(repository = repository)
            }
        }
    }
}
