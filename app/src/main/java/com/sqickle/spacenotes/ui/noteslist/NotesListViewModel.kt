package com.sqickle.spacenotes.ui.noteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val repository: NotesRepository,
) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cachedNotes = repository.getAllNotes(false)
                if (cachedNotes.isNotEmpty()) {
                    _notes.value = cachedNotes
                }
                syncWithServer()
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Failed to load notes: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun syncWithServer() {
        repository.syncWithBackend()
        _notes.value = repository.getAllNotes(true)
    }

    fun refreshNotes() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.syncWithBackend()
                _notes.value = repository.getAllNotes(true)
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.Error("Sync failed: ${e.message}"))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _notes.value = _notes.value.filter { it.uid != noteId }

                repository.deleteNoteFromCache(noteId)
                repository.deleteNoteFromBackend(noteId).fold(
                    onSuccess = {
                        _uiEvents.emit(UiEvent.NoteDeleted)
                    },
                    onFailure = { error ->
                        syncWithServer()
                        _uiEvents.emit(UiEvent.Error("Delete failed: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                syncWithServer()
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