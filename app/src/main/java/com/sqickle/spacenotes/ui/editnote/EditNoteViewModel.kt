package com.sqickle.spacenotes.ui.editnote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqickle.spacenotes.data.model.Importance
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            _isLoading.value = true
            _note.value = repository.getNoteById(noteId)
            _isLoading.value = false
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
            _note.value?.let { repository.saveNote(it) }
        }
    }
}