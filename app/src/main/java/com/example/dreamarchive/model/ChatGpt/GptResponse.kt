package com.example.dreamarchive.model.ChatGpt

data class GptResponse(
    val id: String,
    val choices: List<GptChoice>
)

data class GptChoice(
    val message: GptMessage
)