package com.sqickle.spacenotes.ui.editnote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqickle.spacenotes.data.model.Importance
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val repository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: String = checkNotNull(savedStateHandle["noteId"])

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var localNote = repository.getNoteByIdStream(noteId).first()

                if (localNote == null) {
                    repository.fetchNotesFromBackend()
                    localNote = repository.getNoteByIdStream(noteId).first()
                }

                _note.value = localNote
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Failed to load note: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTitle(title: String) {
        _note.value = _note.value?.copy(title = title)
    }

    fun updateContent(content: String) {
        _note.value = _note.value?.copy(content = content)
    }

    fun updateColor(color: Int) {
        _note.value = _note.value?.copy(color = color)
    }

    fun updateImportance(importance: Importance) {
        _note.value = _note.value?.copy(importance = importance)
    }

    fun updateSelfDestructDate(date: Date?) {
        _note.value = _note.value?.copy(selfDestructDate = date)
    }

    fun saveNote() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentNote = _note.value ?: return@launch
                repository.saveNoteToCache(currentNote)

                repository.pushNoteToBackend(currentNote).onSuccess {
                    _uiEvents.emit(UiEvent.NoteSaved)
                }.onFailure { error ->
                    _uiEvents.emit(UiEvent.Error("Failed to sync changes: ${error.message}"))
                }
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Failed to save changes: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteNoteFromBackend(noteId).onSuccess {
                    _uiEvents.emit(UiEvent.NoteDeleted)
                    repository.deleteNoteFromCache(noteId)
                }.onFailure { error ->
                    _uiEvents.emit(UiEvent.Error("Failed to delete note: ${error.message}"))
                }
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Failed to delete note: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class UiEvent {
        object NoteSaved : UiEvent()
        object NoteDeleted : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}