package com.rus_euphoria.notes

import android.graphics.Color
import java.util.Date
import java.util.UUID

data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.NORMAL,
    val selfDestructDate: Date? = null
) {
    companion object
}
