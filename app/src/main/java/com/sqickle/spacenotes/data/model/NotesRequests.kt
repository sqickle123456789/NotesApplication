package com.sqickle.spacenotes.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchListRequest(val list: List<NoteDto>)

@Serializable
data class ElementRequest(
    @SerialName("element") val element: NoteDto
)