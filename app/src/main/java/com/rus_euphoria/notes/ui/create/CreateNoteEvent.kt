package com.rus_euphoria.notes.ui.create

import com.rus_euphoria.notes.Importance
import java.util.Date

sealed interface CreateNoteEvent {
    data class TitleChanged(val title: String) : CreateNoteEvent
    data class ContentChanged(val content: String) : CreateNoteEvent
    data class ColorSelected(val color: Int) : CreateNoteEvent
    data class ImportanceSelected(val importance: Importance) : CreateNoteEvent
    data class SelfDestructToggled(val enabled: Boolean) : CreateNoteEvent
    data class SelfDestructDateChanged(val date: Date?) : CreateNoteEvent
    data class CustomColorReceived(val color: Int) : CreateNoteEvent
    data object OpenColorPicker : CreateNoteEvent
    data object CloseColorPicker : CreateNoteEvent
    data object SaveClicked : CreateNoteEvent
}
