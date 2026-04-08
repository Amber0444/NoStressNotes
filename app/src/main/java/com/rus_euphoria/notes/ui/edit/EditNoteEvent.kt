package com.rus_euphoria.notes.ui.edit

import com.rus_euphoria.notes.Importance
import java.util.Date

sealed interface EditNoteEvent {
    data class TitleChanged(val title: String) : EditNoteEvent
    data class ContentChanged(val content: String) : EditNoteEvent
    data class ColorSelected(val color: Int) : EditNoteEvent
    data class ImportanceSelected(val importance: Importance) : EditNoteEvent
    data class SelfDestructToggled(val enabled: Boolean) : EditNoteEvent
    data class SelfDestructDateChanged(val date: Date?) : EditNoteEvent
    data class CustomColorReceived(val color: Int) : EditNoteEvent
    data object OpenColorPicker : EditNoteEvent
    data object CloseColorPicker : EditNoteEvent
    data object SaveClicked : EditNoteEvent
    data object DeleteClicked : EditNoteEvent
}
