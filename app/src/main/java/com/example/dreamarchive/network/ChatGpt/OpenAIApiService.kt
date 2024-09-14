package com.example.dreamarchive.network.ChatGpt

import com.example.dreamarchive.model.ChatGpt.GptRequest
import com.example.dreamarchive.model.ChatGpt.GptResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun sendMessage(
        @Header("Authorization") authHeader: String, // Authorizationヘッダーを動的に渡す
        @Body request: GptRequest
    ): Call<GptResponse>
}