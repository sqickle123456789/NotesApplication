package com.sqickle.spacenotes.data.repository

import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.source.local.LocalNoteDataSource
import com.sqickle.spacenotes.data.source.remote.RemoteNoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val localDataSource: LocalNoteDataSource,
    private val remoteDataSource: RemoteNoteDataSource
) : NotesRepository {

    override fun getAllNotesStream(): Flow<List<Note>> =
        localDataSource.getAllNotesStream().flowOn(Dispatchers.IO)

    override fun getNoteByIdStream(id: String): Flow<Note?> =
        localDataSource.getNoteByIdStream(id).flowOn(Dispatchers.IO)

    override suspend fun saveNoteToCache(note: Note) =
        withContext(Dispatchers.IO) { localDataSource.saveNote(note) }

    override suspend fun deleteNoteFromCache(id: String) =
        withContext(Dispatchers.IO) { localDataSource.deleteNote(id) }

    override suspend fun fetchNotesFromBackend(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val notes = remoteDataSource.fetchNotes()
                localDataSource.saveAllNotes(notes)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun pushNoteToBackend(note: Note): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                remoteDataSource.pushNote(note)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteNoteFromBackend(id: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                remoteDataSource.deleteNote(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun syncWithBackend() {
        withContext(Dispatchers.IO) {
            try {
                val notes = remoteDataSource.fetchNotes()
                localDataSource.saveAllNotes(notes)
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun getNote(id: String, forceRefresh: Boolean): Note? {
        return if (forceRefresh) {
            fetchNotesFromBackend()
            localDataSource.getNoteById(id)
        } else {
            localDataSource.getNoteById(id) ?: run {
                fetchNotesFromBackend()
                localDataSource.getNoteById(id)
            }
        }
    }

    override suspend fun getAllNotes(forceRefresh: Boolean): List<Note> {
        return if (forceRefresh) {
            fetchNotesFromBackend()
            localDataSource.getAllNotes()
        } else {
            localDataSource.getAllNotes().ifEmpty {
                fetchNotesFromBackend()
                localDataSource.getAllNotes()
            }
        }
    }
}