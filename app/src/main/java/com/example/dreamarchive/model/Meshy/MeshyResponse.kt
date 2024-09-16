package com.example.dreamarchive.model.Meshy

import com.google.gson.annotations.SerializedName

data class MeshyCreateResponse(
    @SerializedName("result") val result: String? // "result" フィールドを追加
)

data class MeshyResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("model_urls") val modelUrls: ModelUrls?,
    @SerializedName("thumbnail_url") val thumbnailUrl: String?,
    @SerializedName("prompt") val prompt: String?,
    @SerializedName("art_style") val artStyle: String?,
    @SerializedName("status") val status: String? // SUCCEEDED など
)

data class ModelUrls(
    @SerializedName("glb") val glb: String?,
    @SerializedName("fbx") val fbx: String?,
    @SerializedName("usdz") val usdz: String?,
    @SerializedName("obj") val obj: String?,
    @SerializedName("mtl") val mtl: String?
)