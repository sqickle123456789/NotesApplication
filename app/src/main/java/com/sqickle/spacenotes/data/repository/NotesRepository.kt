package com.sqickle.spacenotes.data.repository

import com.sqickle.spacenotes.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    // Локальные операции
    fun getAllNotesStream(): Flow<List<Note>>
    fun getNoteByIdStream(id: String): Flow<Note?>
    suspend fun saveNoteToCache(note: Note)
    suspend fun deleteNoteFromCache(id: String)

    // Сетевые операции
    suspend fun fetchNotesFromBackend(): Result<Unit>
    suspend fun pushNoteToBackend(note: Note): Result<Unit>
    suspend fun deleteNoteFromBackend(id: String): Result<Unit>

    // Комбинированные операции
    suspend fun syncWithBackend()
    suspend fun getNote(id: String, forceRefresh: Boolean = false): Note?
    suspend fun getAllNotes(forceRefresh: Boolean = false): List<Note>
}