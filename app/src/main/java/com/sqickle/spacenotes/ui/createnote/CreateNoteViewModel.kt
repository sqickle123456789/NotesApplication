package com.sqickle.spacenotes.ui.createnote

import android.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqickle.spacenotes.data.model.Importance
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val repository: NotesRepository,
) : ViewModel() {
    private val _note = mutableStateOf(
        Note(
            title = "",
            content = "",
            color = Color.WHITE,
            importance = Importance.NORMAL
        )
    )

    fun updateTitle(title: String) {
        _note.value = _note.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _note.value = _note.value.copy(content = content)
    }

    fun updateColor(color: Int) {
        _note.value = _note.value.copy(color = color)
    }

    fun updateImportance(importance: Importance) {
        _note.value = _note.value.copy(importance = importance)
    }

    fun updateSelfDestructDate(date: Date?) {
        _note.value = _note.value.copy(selfDestructDate = date)
    }

    fun saveNote(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveNote(_note.value)
            onSuccess()
        }
    }
}