package com.example.dreamarchive.model.Meshy

data class MeshyRequest(
    val text: String, // OpenAIで生成された文章
    val modelType: String = "basic" // 任意でMeshyが提供するモデルの種類を指定
)