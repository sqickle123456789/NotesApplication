package com.sqickle.spacenotes.data

import com.sqickle.spacenotes.model.Note
import com.sqickle.spacenotes.model.json
import org.json.JSONArray
import java.io.File

class FileNotebook {
    private val _notes = mutableListOf<Note>()
    val notes: List<Note> get() = _notes.toList()

    fun addNote(note: Note) {
        _notes.add(note)
    }

    fun removeNote(uid: String): Boolean {
        return _notes.removeIf { it.uid == uid }
    }

    fun saveToFile(file: File): Boolean {
        return try {
            val jsonArray = JSONArray()
            _notes.forEach { note ->
                jsonArray.put(note.json)
            }
            file.writeText(jsonArray.toString())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loadFromFile(file: File): Boolean {
        return try {
            val jsonString = file.readText()
            val jsonArray = JSONArray(jsonString)
            _notes.clear()
            for (i in 0 until jsonArray.length()) {
                Note.parse(jsonArray.getJSONObject(i))?.let { note ->
                    _notes.add(note)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}