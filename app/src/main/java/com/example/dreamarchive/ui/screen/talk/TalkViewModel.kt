package com.example.dreamarchive.ui.screen.talk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreamarchive.BuildConfig
import com.example.dreamarchive.model.GptMessage
import com.example.dreamarchive.model.GptRequest
import com.example.dreamarchive.network.OpenAIApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory


class TalkViewModel: ViewModel() {
    private val _messages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages: StateFlow<List<Pair<String, Boolean>>> = _messages

    private val BASE_URL =
        "https://api.openai.com/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val openAIApi = retrofit.create(OpenAIApiService::class.java)

    // BuildConfig 経由で API キーを取得
    private val apiKey = BuildConfig.OPENAI_API_KEY

    fun addMessage(message: String, isUser: Boolean){
        _messages.value = _messages.value + (message to isUser)
    }

    fun sendMessageToGpt(inputText: String){
        addMessage(inputText, isUser = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = GptRequest(
                    messages = listOf(
                        GptMessage("user", inputText)
                    )
                )

                // APIキーを"Bearer "と一緒にAuthorizationヘッダーに渡す
                val authHeader = "Bearer $apiKey"
                val response = openAIApi.sendMessage(authHeader, request).awaitResponse()

                if (response.isSuccessful) {
                    val gptResponse = response.body()
                    val gptMessage = gptResponse?.choices?.firstOrNull()?.message?.content

                    gptMessage?.let {
                        addMessage(it, isUser = false)
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    addMessage("Error: ${response.code()} - $errorBody", isUser = false)
                }
            } catch (e: Exception) {
                addMessage("Failed to communicate with GPT: ${e.message}", isUser = false)
            }
        }
    }
}