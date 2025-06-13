package com.sqickle.spacenotes.data

import android.util.Log
import com.sqickle.spacenotes.model.Note
import com.sqickle.spacenotes.model.json
import org.json.JSONArray
import java.io.File
import org.slf4j.LoggerFactory

class FileNotebook {
    private val _notes = mutableListOf<Note>()
    val notes: List<Note> get() = _notes.toList()

    private val logger = LoggerFactory.getLogger(FileNotebook::class.java)

    fun addNote(note: Note) {
        _notes.add(note)
        logger.info("Note added: ${note.title} (ID: ${note.uid})")
        Log.d("FileNotebook", "Note added: ${note.title}")
    }

    fun removeNote(uid: String): Boolean {
        val note = _notes.find { it.uid == uid }
        return if (note != null) {
            _notes.remove(note)
            logger.info("Note removed: ${note.title} (ID: $uid)")
            Log.d("FileNotebook", "Note removed: ${note.title}")
            true
        } else {
            logger.warn("Note with ID $uid not found for removal")
            Log.w("FileNotebook", "Note not found: $uid")
            false
        }
    }

    fun saveToFile(file: File): Boolean {
        return try {
            val jsonArray = JSONArray()
            _notes.forEach { jsonArray.put(it.json) }
            file.writeText(jsonArray.toString())
            logger.info("Saved ${_notes.size} notes to ${file.absolutePath}")
            Log.i("FileNotebook", "Notes saved to file")
            true
        } catch (e: Exception) {
            logger.error("Error saving notes to file", e)
            Log.e("FileNotebook", "Save error", e)
            false
        }
    }

    fun loadFromFile(file: File): Boolean {
        return try {
            val jsonString = file.readText()
            val jsonArray = JSONArray(jsonString)
            _notes.clear()
            for (i in 0 until jsonArray.length()) {
                Note.parse(jsonArray.getJSONObject(i))?.let { _notes.add(it) }
            }
            logger.info("Loaded ${_notes.size} notes from ${file.absolutePath}")
            Log.i("FileNotebook", "Notes loaded from file")
            true
        } catch (e: Exception) {
            logger.error("Error loading notes from file", e)
            Log.e("FileNotebook", "Load error", e)
            false
        }
    }
}