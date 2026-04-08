package com.rus_euphoria.notes.ui.create

import android.graphics.Color
import com.rus_euphoria.notes.Importance
import java.util.Date

data class CreateNoteState(
    val title: String = "",
    val content: String = "",
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.NORMAL,
    val selfDestructEnabled: Boolean = false,
    val selfDestructDate: Date? = null,
    val customColor: Int? = null,
    val showColorPicker: Boolean = false
)
