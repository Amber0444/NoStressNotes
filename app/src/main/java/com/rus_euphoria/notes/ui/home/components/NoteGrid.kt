package com.rus_euphoria.notes.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rus_euphoria.notes.Note
import com.rus_euphoria.notes.ui.components.SwipeableWrapper

@Composable
fun NoteGrid(
    notes: List<Note>,
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
        items(notes, key = { it.uid }) { note ->
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
