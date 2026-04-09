package com.rus_euphoria.notes.ui.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rus_euphoria.notes.ui.edit.components.ColorPickerDialog
import com.rus_euphoria.notes.ui.edit.components.ColorSelectionRow
import com.rus_euphoria.notes.ui.edit.components.ContentField
import com.rus_euphoria.notes.ui.edit.components.EditNoteTopBar
import com.rus_euphoria.notes.ui.edit.components.ImportancePicker
import com.rus_euphoria.notes.ui.edit.components.SelfDestructSection
import com.rus_euphoria.notes.ui.edit.components.TitleField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateNoteScreen(
    viewModel: CreateNoteViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.action.collectLatest { action ->
            when (action) {
                is CreateNoteAction.NavigateBack -> onNavigateBack()
            }
        }
    }

    if (state.showColorPicker) {
        ColorPickerDialog(
            initialColor = state.color,
            onColorConfirmed = { viewModel.onEvent(CreateNoteEvent.CustomColorReceived(it)) },
            onDismiss = { viewModel.onEvent(CreateNoteEvent.CloseColorPicker) }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditNoteTopBar(
                isExistingNote = false,
                pinned = false,
                onBack = onNavigateBack,
                onSave = { viewModel.onEvent(CreateNoteEvent.SaveClicked) },
                onDelete = {},
                onTogglePin = {},
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            TitleField(
                value = state.title,
                onValueChange = { viewModel.onEvent(CreateNoteEvent.TitleChanged(it)) }
            )

            ContentField(
                value = state.content,
                onValueChange = { viewModel.onEvent(CreateNoteEvent.ContentChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ImportancePicker(
                selected = state.importance,
                onSelect = { viewModel.onEvent(CreateNoteEvent.ImportanceSelected(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ColorSelectionRow(
                selectedColor = state.color,
                customColor = state.customColor,
                onColorSelected = { viewModel.onEvent(CreateNoteEvent.ColorSelected(it)) },
                onOpenColorPicker = { viewModel.onEvent(CreateNoteEvent.OpenColorPicker) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SelfDestructSection(
                enabled = state.selfDestructEnabled,
                onEnabledChange = { viewModel.onEvent(CreateNoteEvent.SelfDestructToggled(it)) },
                date = state.selfDestructDate,
                onDateChange = { viewModel.onEvent(CreateNoteEvent.SelfDestructDateChanged(it)) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
