package com.example.dreamarchive.model

data class GptResponse(
    val id: String,
    val choices: List<GptChoice>
)

data class GptChoice(
    val message: GptMessage
)