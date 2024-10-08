package com.example.dreamarchive.model.ChatGpt

data class GptRequest(
    val model: String = "gpt-4-turbo",
    val messages: List<GptMessage>
)

data class GptMessage(
    val role: String,
    val content: String
)