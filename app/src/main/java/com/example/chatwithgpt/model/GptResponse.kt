package com.example.chatwithgpt.model

data class GptResponse(
    val id: String,
    val choices: List<GptChoice>
)

data class GptChoice(
    val message: GptMessage
)