package com.sqickle.spacenotes.data.source.remote

import android.graphics.Color
import com.sqickle.spacenotes.data.model.ElementRequest
import com.sqickle.spacenotes.data.model.Importance
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.data.model.NoteDto
import com.sqickle.spacenotes.data.model.PatchListRequest
import kotlinx.coroutines.delay
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteNoteDataSource @Inject constructor(
    private val api: ApiService,
    private val oauthTokenProvider: OAuthTokenProvider
) : Authenticator {
    private companion object {
        const val MAX_RETRIES = 3
        const val RETRY_DELAY_MS = 1000L
        const val OAUTH_CLIENT_ID = "0d0970774e284fa8ba9ff70b6b06479a"
    }

    private var lastKnownRevision: Int = 0
    private val isRefreshingToken = AtomicBoolean(false)

    suspend fun init() {
        fetchNotes()
    }

    suspend fun fetchNotes(): List<Note> {
        return executeWithRetry {
            val response = api.getNotes()
            lastKnownRevision = response.revision
            response.list.map { dto -> dto.toNote() }
        }
    }

    suspend fun pushNote(note: Note): Note {
        return executeWithRetry(isModifyingOperation = true) {
            try {
                val response = api.addNote(
                    revision = lastKnownRevision,
                    request = ElementRequest(note.toDto())
                ).also { checkResponse(it) }

                lastKnownRevision = response.body()?.revision ?: lastKnownRevision
                response.body()!!.element.toNote()
            } catch (e: IllegalStateException) {
                if (e.message == "Data is out of sync") {
                    fetchNotes()
                    val retryResponse = api.addNote(
                        revision = lastKnownRevision,
                        request = ElementRequest(note.toDto())
                    ).also { checkResponse(it) }

                    lastKnownRevision = retryResponse.body()?.revision ?: lastKnownRevision
                    retryResponse.body()!!.element.toNote()
                } else {
                    throw e
                }
            }
        }
    }

    suspend fun updateNote(note: Note): Note {
        return executeWithRetry(isModifyingOperation = true) {
            try {
                val response = api.updateNote(
                    revision = lastKnownRevision,
                    noteUid = note.uid,
                    request = ElementRequest(note.toDto())
                ).also { checkResponse(it) }

                lastKnownRevision = response.body()?.revision ?: lastKnownRevision
                response.body()!!.element.toNote()
            } catch (e: IllegalStateException) {
                if (e.message == "Data is out of sync") {
                    fetchNotes()
                    val retryResponse = api.updateNote(
                        revision = lastKnownRevision,
                        noteUid = note.uid,
                        request = ElementRequest(note.toDto())
                    ).also { checkResponse(it) }

                    lastKnownRevision = retryResponse.body()?.revision ?: lastKnownRevision
                    retryResponse.body()!!.element.toNote()
                } else {
                    throw e
                }
            }
        }
    }

    suspend fun deleteNote(id: String): Boolean {
        return executeWithRetry(isModifyingOperation = true) {
            try {
                val response = api.deleteNote(
                    revision = lastKnownRevision,
                    noteUid = id
                ).also { checkResponse(it) }

                lastKnownRevision = response.body()?.revision ?: lastKnownRevision
                true
            } catch (e: IllegalStateException) {
                if (e.message == "Data is out of sync") {
                    fetchNotes()
                    val retryResponse = api.deleteNote(
                        revision = lastKnownRevision,
                        noteUid = id
                    ).also { checkResponse(it) }

                    lastKnownRevision = retryResponse.body()?.revision ?: lastKnownRevision
                    true
                } else {
                    throw e
                }
            }
        }
    }

    suspend fun patchNotes(notes: List<Note>): List<Note> {
        return executeWithRetry(isModifyingOperation = true) {
            try {
                val response = api.patchNotes(
                    revision = lastKnownRevision,
                    request = PatchListRequest(notes.map { it.toDto() })
                )
                lastKnownRevision = response.revision
                response.list.map { it.toNote() }
            } catch (e: IllegalStateException) {
                if (e.message == "Data is out of sync") {
                    fetchNotes()
                    val retryResponse = api.patchNotes(
                        revision = lastKnownRevision,
                        request = PatchListRequest(notes.map { it.toDto() })
                    )
                    lastKnownRevision = retryResponse.revision
                    retryResponse.list.map { it.toNote() }
                } else {
                    throw e
                }
            }
        }
    }

    private fun checkResponse(response: retrofit2.Response<*>) {
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw when {
                errorBody?.contains("unsynchronized data") == true -> {
                    IllegalStateException("Data is out of sync")
                }
                else -> HttpException(response)
            }
        }
    }

    private suspend fun <T> executeWithRetry(
        isModifyingOperation: Boolean = false,
        block: suspend () -> T
    ): T {
        var lastException: Exception? = null
        var attempt = 0

        while (attempt < MAX_RETRIES) {
            try {
                return block()
            } catch (e: Exception) {
                lastException = e
                if (shouldRetry(e, isModifyingOperation)) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                    attempt++
                } else {
                    throw e
                }
            }
        }
        throw lastException ?: IllegalStateException("Unknown error occurred")
    }

    private fun shouldRetry(e: Exception, isModifyingOperation: Boolean): Boolean {
        return when (e) {
            is HttpException -> e.code() in 500..599 || e.code() == 401
            is SocketTimeoutException -> true
            is IllegalStateException -> true
            else -> false
        }
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (isRefreshingToken.getAndSet(true)) return null

        return try {
            val newToken = oauthTokenProvider.refreshToken(OAUTH_CLIENT_ID)
            response.request.newBuilder()
                .header("Authorization", "OAuth $newToken")
                .build()
        } finally {
            isRefreshingToken.set(false)
        }
    }

    private fun Note.toDto(): NoteDto = NoteDto(
        id = this.uid,
        text = "${this.title}\n${this.content}",
        importance = when (this.importance) {
            Importance.HIGH -> "important"
            Importance.LOW -> "low"
            else -> "basic"
        },
        done = false,
        createdAt = this.createdAt.time,
        changedAt = Date().time,
        lastUpdatedBy = "android-client",
        color = this.color.takeIf { it != Color.WHITE }?.let {
            String.format("#%06X", 0xFFFFFF and it)
        }
    )

    private fun NoteDto.toNote(): Note {
        val (title, content) = if (text.contains('\n')) {
            val parts = text.split('\n', limit = 2)
            parts[0] to parts[1]
        } else {
            text to ""
        }

        return Note(
            uid = id,
            title = title,
            content = content,
            color = color?.let { Color.parseColor(it) } ?: Color.WHITE,
            importance = when (importance.lowercase()) {
                "important" -> Importance.HIGH
                "low" -> Importance.LOW
                else -> Importance.NORMAL
            },
            createdAt = Date(createdAt)
        )
    }
}

interface OAuthTokenProvider {
    fun refreshToken(clientId: String): String
}