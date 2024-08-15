package com.example.chatwithgpt.network

import com.example.chatwithgpt.model.GptRequest
import com.example.chatwithgpt.model.GptResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApiService {
    @Headers(
        "Authorization: Bearer YOUR API KEY",
        "Content-Type: application/json")
    @POST("v1/chat/completions")
    fun sendMessage(@Body request: GptRequest): Call<GptResponse>
}