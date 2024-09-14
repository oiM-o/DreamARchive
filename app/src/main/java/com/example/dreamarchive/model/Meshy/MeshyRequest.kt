package com.example.dreamarchive.model.Meshy

data class MeshyRequest(
    val mode: String = "preview",  // モードを "preview" に設定
    val prompt: String, // OpenAIで生成された文章
)