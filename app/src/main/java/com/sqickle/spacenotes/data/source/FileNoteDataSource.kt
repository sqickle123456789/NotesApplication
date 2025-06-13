package com.sqickle.spacenotes.data.source

import android.content.Context
import android.util.Log
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.model.json
import com.sqickle.spacenotes.data.repository.NotesRepository
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

class FileNoteDataSource @Inject constructor(
    private val context: Context
) : NotesRepository {
    private val logger = LoggerFactory.getLogger(FileNoteDataSource::class.java)
    private val file: File by lazy {
        File(context.filesDir, "notes.json").also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }
    }

    override suspend fun getAllNotes(): List<Note> {
        return try {
            if (!file.exists() || file.length() == 0L) return emptyList()

            val jsonString = file.readText()
            val jsonArray = JSONArray(jsonString)

            (0 until jsonArray.length()).mapNotNull { i ->
                Note.parse(jsonArray.getJSONObject(i))
            }
        } catch (e: Exception) {
            logger.error("Error loading notes from file", e)
            Log.e("FileNoteDataSource", "Load error", e)
            emptyList()
        }
    }

    override suspend fun getNoteById(id: String): Note? {
        return getAllNotes().find { it.uid == id }
    }

    override suspend fun saveNote(note: Note) {
        try {
            val allNotes = getAllNotes().filter { it.uid != note.uid } + note
            saveAllNotes(allNotes)
            logger.info("Note saved: ${note.title} (ID: ${note.uid})")
            Log.d("FileNoteDataSource", "Note saved: ${note.title}")
        } catch (e: Exception) {
            logger.error("Error saving note", e)
            Log.e("FileNoteDataSource", "Save error", e)
        }
    }

    override suspend fun deleteNote(id: String) {
        try {
            val notes = getAllNotes().filter { it.uid != id }
            saveAllNotes(notes)
            logger.info("Note deleted: ID $id")
            Log.d("FileNoteDataSource", "Note deleted: $id")
        } catch (e: Exception) {
            logger.error("Error deleting note", e)
            Log.e("FileNoteDataSource", "Delete error", e)
        }
    }

    private suspend fun saveAllNotes(notes: List<Note>) {
        val jsonArray = JSONArray()
        notes.forEach { jsonArray.put(it.json) }
        file.writeText(jsonArray.toString())
    }
}