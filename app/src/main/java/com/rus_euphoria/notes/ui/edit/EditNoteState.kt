package com.rus_euphoria.notes.ui.edit

import com.rus_euphoria.notes.model.Importance
import java.util.Date

data class EditNoteState(
    val uid: String = "",
    val title: String = "",
    val content: String = "",
    val color: Int = android.graphics.Color.WHITE,
    val importance: Importance = Importance.NORMAL,
    val selfDestructEnabled: Boolean = false,
    val selfDestructDate: Date? = null,
    val customColor: Int? = null,
    val showColorPicker: Boolean = false,
    val pinned: Boolean = false,
)
