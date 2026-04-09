package com.rus_euphoria.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rus_euphoria.notes.data.NoteRepository
import com.rus_euphoria.notes.data.local.LocalDataSource
import com.rus_euphoria.notes.data.local.db.NotesDatabase
import com.rus_euphoria.notes.data.remote.DeviceProvider
import com.rus_euphoria.notes.data.remote.RemoteDataSource
import com.rus_euphoria.notes.data.remote.SyncManager
import com.rus_euphoria.notes.data.remote.api.TodoApiClient
import com.rus_euphoria.notes.navigation.NotesNavGraph
import com.rus_euphoria.notes.ui.theme.NotesAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MainActivity : ComponentActivity() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val repository by lazy {
        val database = NotesDatabase.get(applicationContext)
        val localDataSource = LocalDataSource(database.noteDao(), applicationScope)
        val deviceProvider = DeviceProvider(applicationContext)
        val syncManager = SyncManager(
            api = TodoApiClient.apiService,
            deviceIdProvider = { deviceProvider.deviceId },
        )
        val remoteDataSource = RemoteDataSource(syncManager)
        NoteRepository(localDataSource, remoteDataSource, externalScope = applicationScope)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository.refreshFromRemote()

        setContent {
            NotesAppTheme {
                NotesNavGraph(repository = repository)
            }
        }
    }
}
