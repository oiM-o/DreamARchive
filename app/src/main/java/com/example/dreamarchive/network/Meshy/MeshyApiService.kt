package com.example.dreamarchive.network.Meshy

import com.example.dreamarchive.model.Meshy.MeshyRequest
import com.example.dreamarchive.model.Meshy.MeshyResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MeshyApiService {
    @Headers("Content-Type: application/json")
    @POST("v2/text-to-3d")
    suspend fun generate3DModel(
        @Header("Authorization") apiKey: String,
        @Body request: MeshyRequest
    ): Call<MeshyResponse>
}