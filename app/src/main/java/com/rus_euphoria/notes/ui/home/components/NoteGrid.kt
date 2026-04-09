package com.rus_euphoria.notes.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rus_euphoria.notes.model.Importance
import com.rus_euphoria.notes.ui.components.SwipeableWrapper
import com.rus_euphoria.notes.ui.home.NoteSection

@Composable
fun NoteGrid(
    sections: List<NoteSection>,
    onNoteClick: (String) -> Unit,
    onNoteDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        sections.forEach { section ->
            item(
                key = "header-${section.importance.name}",
                span = StaggeredGridItemSpan.FullLine
            ) {
                SectionHeader(section.importance, section.notes.size)
            }
            items(section.notes, key = { it.uid }) { note ->
                SwipeableWrapper(
                    onSwipeDelete = { onNoteDelete(note.uid) },
                ) {
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.uid) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(importance: Importance, count: Int) {
    val title = when (importance) {
        Importance.HIGH -> "Важные"
        Importance.NORMAL -> "Обычные"
        Importance.LOW -> "Не срочные"
    }
    Text(
        text = "$title · $count",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
    )
}
