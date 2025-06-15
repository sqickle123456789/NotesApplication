package com.sqickle.spacenotes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val uid: String,
    val title: String,
    val content: String,
    val color: Int,
    val importance: String,
    val createdAt: Long,
    val selfDestructDate: Long? = null
) {
    fun toNote(): Note = Note(
        uid = uid,
        title = title,
        content = content,
        color = color,
        importance = when (importance) {
            "HIGH" -> Importance.HIGH
            "LOW" -> Importance.LOW
            else -> Importance.NORMAL
        },
        createdAt = Date(createdAt),
        selfDestructDate = selfDestructDate?.let { Date(it) }
    )

    companion object {
        fun fromNote(note: Note): NoteEntity = NoteEntity(
            uid = note.uid,
            title = note.title,
            content = note.content,
            color = note.color,
            importance = when (note.importance) {
                Importance.HIGH -> "HIGH"
                Importance.LOW -> "LOW"
                else -> "NORMAL"
            },
            createdAt = note.createdAt.time,
            selfDestructDate = note.selfDestructDate?.time
        )
    }
}