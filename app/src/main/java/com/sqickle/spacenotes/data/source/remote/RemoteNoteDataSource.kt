package com.sqickle.spacenotes.data.source.remote

import com.sqickle.spacenotes.data.model.Note
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteNoteDataSource @Inject constructor() {
    private val simulatedNetworkDelay = 1000L

    suspend fun fetchNotes(): List<Note> {
        delay(simulatedNetworkDelay)
        return emptyList()
    }

    suspend fun pushNote(note: Note) {
        delay(simulatedNetworkDelay)
    }

    suspend fun deleteNote(id: String) {
        delay(simulatedNetworkDelay)
    }
}