package com.example.dreamarchive.network.Meshy

import com.example.dreamarchive.model.Meshy.MeshyRequest
import com.example.dreamarchive.model.Meshy.MeshyResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface MeshyApiService {
    @Headers("Content-Type: application/json")
    @POST("v2/text-to-3d")
    suspend fun generate3DModel(
        @Header("Authorization") apiKey: String,
        @Body request: MeshyRequest
    ): Response<MeshyResponse>

    // モデルのステータスを確認するためのエンドポイント
    @GET("v2/tasks/{taskId}")
    suspend fun checkModelStatus(
        @Header("Authorization") apiKey: String,
        @Path("taskId") taskId: String
    ): Response<MeshyResponse>
}