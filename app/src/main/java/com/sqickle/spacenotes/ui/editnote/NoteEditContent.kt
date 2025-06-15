package com.sqickle.spacenotes.ui.editnote

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sqickle.spacenotes.data.model.Importance
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.ui.editnote.components.ColorSelectionSection
import com.sqickle.spacenotes.ui.editnote.components.ImportanceSelectionSection
import com.sqickle.spacenotes.ui.editnote.components.SelfDestructSection
import com.sqickle.spacenotes.ui.noteslist.components.NoteEditTopBar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditContent(
    note: Note,
    topBarTitle: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onColorChange: (Int) -> Unit,
    onImportanceChange: (Importance) -> Unit,
    onSelfDestructDateChange: (Date?) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            NoteEditTopBar(
                topBarTitle = topBarTitle,
                onSave = onSave,
                onCancel = onCancel
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .imePadding()
        ) {
            OutlinedTextField(
                value = note.title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = note.content,
                    onValueChange = { newText ->
                        onContentChange(newText)
                    },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SelfDestructSection(
                hasDate = note.selfDestructDate != null,
                currentDate = note.selfDestructDate,
                onDateChange = onSelfDestructDateChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            ColorSelectionSection(
                currentColor = note.color,
                onColorSelected = onColorChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            ImportanceSelectionSection(
                currentImportance = note.importance,
                onImportanceSelected = onImportanceChange
            )
        }
    }
}