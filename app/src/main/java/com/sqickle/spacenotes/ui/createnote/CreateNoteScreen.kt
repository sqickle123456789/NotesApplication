package com.sqickle.spacenotes.ui.createnote

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.sqickle.spacenotes.ui.editnote.NoteEditContent

@Composable
fun CreateNoteScreen(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: CreateNoteViewModel = hiltViewModel()
) {
    val noteState = viewModel.note.value

    NoteEditContent(
        note = noteState,
        onTitleChange = viewModel::updateTitle,
        onContentChange = viewModel::updateContent,
        onColorChange = viewModel::updateColor,
        onImportanceChange = viewModel::updateImportance,
        onSelfDestructDateChange = viewModel::updateSelfDestructDate,
        onSave = {
            viewModel.saveNote(onSaveSuccess)
        },
        onCancel = onBack
    )
}