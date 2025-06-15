package com.sqickle.spacenotes.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class NoteDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("importance") val importance: String,
    @SerialName("deadline") val deadline: Long? = null,
    @SerialName("done") val done: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long = Date().time,
    @SerialName("last_updated_by") val lastUpdatedBy: String,
    @SerialName("color") val color: String? = null,
)