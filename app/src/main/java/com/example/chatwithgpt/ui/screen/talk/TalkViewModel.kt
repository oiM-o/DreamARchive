package com.example.chatwithgpt.ui.screen.talk

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatwithgpt.model.GptMessage
import com.example.chatwithgpt.model.GptRequest
import com.example.chatwithgpt.network.OpenAIApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import io.github.cdimascio.dotenv.dotenv


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

                val response = openAIApi.sendMessage(request).awaitResponse()

                if (response.isSuccessful) {
                    val gptResponse = response.body()
                    val gptMessage = gptResponse?.choices?.firstOrNull()?.message?.content

                    gptMessage?.let {
                        addMessage(it, isUser = false)
                    }
                } else {
                    addMessage("Error: ${response.code()}", isUser = false)
                }
            } catch (e: Exception) {
                addMessage("Failed to communicate with GPT: ${e.message}", isUser = false)
            }
        }
    }
}