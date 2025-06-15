package com.sqickle.spacenotes.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GetElementResponse(
    val status: String,
    val element: NoteDto,
    val revision: Int
)

@Serializable
data class GetListResponse(
    val status: String,
    val list: List<NoteDto>,
    val revision: Int
)

@Serializable
data class ElementResponse(
    val status: String,
    val element: NoteDto,
    val revision: Int
)