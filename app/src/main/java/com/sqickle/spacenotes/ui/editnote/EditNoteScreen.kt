package com.sqickle.spacenotes.ui.editnote

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditNoteScreen(
    noteId: String,
    onBack: () -> Unit,
    viewModel: EditNoteViewModel = hiltViewModel(),
) {
    val note by viewModel.note.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (note == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Note not found")
        }
        LaunchedEffect(Unit) {
            onBack()
        }
    } else {
        NoteEditContent(
            note = note!!,
            topBarTitle = "Редактировать заметку",
            onTitleChange = viewModel::updateTitle,
            onContentChange = viewModel::updateContent,
            onColorChange = viewModel::updateColor,
            onImportanceChange = viewModel::updateImportance,
            onSelfDestructDateChange = viewModel::updateSelfDestructDate,
            onSave = {
                viewModel.saveNote()
                onBack()
            },
            onCancel = onBack
        )
    }
}