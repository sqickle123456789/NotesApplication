package com.sqickle.spacenotes.ui.noteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val repository: NotesRepository,
) : ViewModel() {
    val notes: StateFlow<List<Note>> = repository.getAllNotesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllNotes(false)
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Failed to load notes: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteNoteFromBackend(noteId).fold(
                    onSuccess = {
                        _uiEvents.emit(UiEvent.NoteDeleted)
                        repository.deleteNoteFromCache(noteId)
                    },
                    onFailure = { error ->
                        _uiEvents.emit(UiEvent.Error("Delete failed: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Failed to delete note: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }


    sealed class UiEvent {
        object NoteDeleted : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}