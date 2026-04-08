package com.rus_euphoria.notes.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object Home : Screen

    @Serializable
    data object CreateNote : Screen

    @Serializable
    data class EditNote(val noteUid: String) : Screen
}
