package com.sqickle.spacenotes.data.source.local

import android.content.Context
import android.util.Log
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.model.json
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalNoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val logger = LoggerFactory.getLogger(LocalNoteDataSource::class.java)
    private val file: File by lazy {
        File(context.filesDir, "notes.json").also {
            if (!it.exists()) {
                it.createNewFile()
                logger.info("Created new notes file")
                Log.d("LocalNoteDataSource", "Created new notes file")
            }
        }
    }

    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())

    init {
        loadInitialData()
    }

    fun getAllNotesStream(): Flow<List<Note>> = notesFlow

    fun getNoteByIdStream(id: String): Flow<Note?> = notesFlow.map { notes ->
        notes.find { it.uid == id }
    }

    suspend fun getAllNotes(): List<Note> = notesFlow.value

    suspend fun getNoteById(id: String): Note? {
        return try {
            notesFlow.value.find { it.uid == id }
        } catch (e: Exception) {
            logger.error("Error getting note by ID: $id", e)
            Log.e("LocalNoteDataSource", "Error getting note by ID: $id", e)
            null
        }
    }

    suspend fun saveNote(note: Note) {
        try {
            val updatedNotes = notesFlow.value.filter { it.uid != note.uid } + note
            saveAllNotes(updatedNotes)
            logger.info("Note saved: ${note.title} (ID: ${note.uid})")
            Log.d("LocalNoteDataSource", "Note saved: ${note.title}")
        } catch (e: Exception) {
            logger.error("Error saving note", e)
            Log.e("LocalNoteDataSource", "Save error", e)
        }
    }

    suspend fun deleteNote(id: String) {
        try {
            val updatedNotes = notesFlow.value.filter { it.uid != id }
            saveAllNotes(updatedNotes)
            logger.info("Note deleted: ID $id")
            Log.d("LocalNoteDataSource", "Note deleted: $id")
        } catch (e: Exception) {
            logger.error("Error deleting note", e)
            Log.e("LocalNoteDataSource", "Delete error", e)
        }
    }

    suspend fun saveAllNotes(notes: List<Note>) {
        try {
            saveToFile(notes)
            notesFlow.update { notes }
            logger.info("Saved ${notes.size} notes to cache")
            Log.d("LocalNoteDataSource", "Saved ${notes.size} notes to cache")
        } catch (e: Exception) {
            logger.error("Error saving all notes", e)
            Log.e("LocalNoteDataSource", "Error saving all notes", e)
        }
    }

    private fun loadInitialData() {
        try {
            val notes = if (!file.exists() || file.length() == 0L) {
                logger.info("No notes found in cache")
                Log.d("LocalNoteDataSource", "No notes found in cache")
                emptyList()
            } else {
                val jsonString = file.readText()
                val jsonArray = JSONArray(jsonString)
                val loadedNotes = (0 until jsonArray.length()).mapNotNull { i ->
                    Note.parse(jsonArray.getJSONObject(i))
                }
                logger.info("Loaded ${loadedNotes.size} notes from cache")
                Log.d("LocalNoteDataSource", "Loaded ${loadedNotes.size} notes from cache")
                loadedNotes
            }
            notesFlow.value = notes
        } catch (e: Exception) {
            logger.error("Error loading initial data", e)
            Log.e("LocalNoteDataSource", "Error loading initial data", e)
            notesFlow.value = emptyList()
        }
    }

    private suspend fun saveToFile(notes: List<Note>) {
        try {
            val jsonArray = JSONArray()
            notes.forEach { jsonArray.put(it.json) }
            file.writeText(jsonArray.toString())
            logger.debug("Notes successfully written to file")
            Log.d("LocalNoteDataSource", "Notes successfully written to file")
        } catch (e: Exception) {
            logger.error("Error writing notes to file", e)
            Log.e("LocalNoteDataSource", "Error writing notes to file", e)
            throw e
        }
    }
}