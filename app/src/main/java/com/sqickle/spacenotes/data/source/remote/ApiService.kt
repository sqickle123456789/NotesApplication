package com.sqickle.spacenotes.data.source.remote

import com.sqickle.spacenotes.data.model.ElementRequest
import com.sqickle.spacenotes.data.model.ElementResponse
import com.sqickle.spacenotes.data.model.GetElementResponse
import com.sqickle.spacenotes.data.model.GetListResponse
import com.sqickle.spacenotes.data.model.PatchListRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("list")
    suspend fun getNotes(
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetListResponse

    @GET("list/{id}")
    suspend fun getNote(
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetElementResponse

    @POST("list")
    suspend fun addNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: ElementRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): Response<ElementResponse>

    @PUT("list/{id}")
    suspend fun updateNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Body request: ElementRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): Response<ElementResponse>

    @PATCH("list")
    suspend fun patchNotes(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: PatchListRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetListResponse

    @DELETE("list/{id}")
    suspend fun deleteNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): Response<ElementResponse>
}