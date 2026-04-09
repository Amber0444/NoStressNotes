package com.rus_euphoria.notes.data.remote.api

import com.rus_euphoria.notes.data.remote.api.models.ElementRequest
import com.rus_euphoria.notes.data.remote.api.models.ElementResponse
import com.rus_euphoria.notes.data.remote.api.models.ListResponse
import com.rus_euphoria.notes.data.remote.api.models.PatchListRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApiService {

    @GET("list")
    suspend fun getList(
        @Header("X-Generate-Fails") generateFails: Int? = null,
    ): ListResponse

    @PATCH("list")
    suspend fun patchList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: PatchListRequest,
        @Header("X-Generate-Fails") generateFails: Int? = null,
    ): ListResponse

    @GET("list/{id}")
    suspend fun getItem(
        @Path("id") id: String,
        @Header("X-Generate-Fails") generateFails: Int? = null,
    ): ElementResponse

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: ElementRequest,
        @Header("X-Generate-Fails") generateFails: Int? = null,
    ): ElementResponse

    @PUT("list/{id}")
    suspend fun updateItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body body: ElementRequest,
        @Header("X-Generate-Fails") generateFails: Int? = null,
    ): ElementResponse

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Header("X-Generate-Fails") generateFails: Int? = null,
    ): ElementResponse
}
