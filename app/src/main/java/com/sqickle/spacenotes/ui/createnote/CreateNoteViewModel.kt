package com.sqickle.spacenotes.ui.createnote

import android.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqickle.spacenotes.data.model.Importance
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
            importance = Importance.NORMAL,
            createdAt = Date()
        )
    )
    val note = _note

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

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

    fun saveNote() {
        viewModelScope.launch {
            try {
                repository.saveNoteToCache(_note.value)

                repository.pushNoteToBackend(_note.value).onSuccess {
                    _uiEvents.emit(UiEvent.NoteSaved)
                }.onFailure { error ->
                    _uiEvents.emit(UiEvent.Error("Failed to sync note: ${error.message}"))
                }
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Failed to save note: ${e.message}"))
            }
        }
    }

    sealed class UiEvent {
        object NoteSaved : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}