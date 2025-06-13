package com.sqickle.spacenotes.data.repository

import com.sqickle.spacenotes.data.model.Note

interface NotesRepository {
    suspend fun getAllNotes(): List<Note>
    suspend fun getNoteById(id: String): Note?
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(id: String)
}
