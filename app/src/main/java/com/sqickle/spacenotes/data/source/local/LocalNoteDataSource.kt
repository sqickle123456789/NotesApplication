package com.sqickle.spacenotes.data.source.local

import com.sqickle.spacenotes.data.model.Note
import kotlinx.coroutines.flow.Flow

interface LocalNoteDataSource {
    fun getAllNotesStream(): Flow<List<Note>>
    fun getNoteByIdStream(id: String): Flow<Note?>
    suspend fun getAllNotes(): List<Note>
    suspend fun getNoteById(id: String): Note?
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun saveAllNotes(notes: List<Note>)
}