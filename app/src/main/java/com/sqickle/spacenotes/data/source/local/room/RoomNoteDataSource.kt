package com.sqickle.spacenotes.data.source.local.room

import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.model.NoteEntity
import com.sqickle.spacenotes.data.source.local.LocalNoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomNoteDataSource @Inject constructor(
    private val noteDao: NoteDao
) : LocalNoteDataSource {

    override fun getAllNotesStream(): Flow<List<Note>> =
        noteDao.getAllNotesStream()
            .map { entities ->
                entities.map { it.toNote() }
                    .sortedByDescending { it.createdAt }
            }

    override fun getNoteByIdStream(id: String): Flow<Note?> =
        noteDao.getNoteByIdStream(id)
            .map { entity -> entity?.toNote() }

    override suspend fun getAllNotes(): List<Note> =
        noteDao.getAllNotes().map { it.toNote() }

    override suspend fun getNoteById(id: String): Note? =
        noteDao.getNoteById(id)?.toNote()

    override suspend fun saveNote(note: Note) {
        noteDao.insertNote(NoteEntity.fromNote(note))
    }

    override suspend fun deleteNote(id: String) {
        noteDao.getNoteById(id)?.let { noteDao.deleteNote(it) }
    }

    override suspend fun saveAllNotes(notes: List<Note>) {
        noteDao.insertAllNotes(notes.map { NoteEntity.fromNote(it) })
    }

    suspend fun clearDatabase() {
        noteDao.deleteAllNotes()
    }
}