package com.example.dreamarchive.model

data class GptRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<GptMessage>
)

data class GptMessage(
    val role: String,
    val content: String
)