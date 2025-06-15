package com.sqickle.spacenotes.data.repository

import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.source.local.LocalNoteDataSource
import com.sqickle.spacenotes.data.source.remote.RemoteNoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val localDataSource: LocalNoteDataSource,
    private val remoteDataSource: RemoteNoteDataSource
) : NotesRepository {
    private val syncMutex = Mutex()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        initializeRepository()
    }

    private fun initializeRepository() {
        repositoryScope.launch {
            try {
                remoteDataSource.init()
                syncWithBackend()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
        withContext(Dispatchers.IO + SupervisorJob()) {
            try {
                syncMutex.withLock {
                    try {
                        remoteDataSource.pushNote(note)
                        Result.success(Unit)
                    } catch (e: Exception) {
                        if (e is HttpException && e.code() == 400) {
                            remoteDataSource.updateNote(note)
                            Result.success(Unit)
                        } else {
                            throw e
                        }
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteNoteFromBackend(id: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                syncMutex.withLock {
                    remoteDataSource.deleteNote(id)
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun syncWithBackend() {
        withContext(Dispatchers.IO + SupervisorJob()) {
            try {
                val remoteNotes = remoteDataSource.fetchNotes()
                localDataSource.saveAllNotes(remoteNotes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getNote(id: String, forceRefresh: Boolean): Note? {
        return withContext(Dispatchers.IO) {
            if (forceRefresh) {
                fetchNotesFromBackend()
            }
            localDataSource.getNoteById(id) ?: run {
                fetchNotesFromBackend()
                localDataSource.getNoteById(id)
            }
        }
    }

    override suspend fun getAllNotes(forceRefresh: Boolean): List<Note> {
        return withContext(Dispatchers.IO) {
            if (forceRefresh || localDataSource.getAllNotes().isEmpty()) {
                fetchNotesFromBackend()
            }
            localDataSource.getAllNotes()
        }
    }
}