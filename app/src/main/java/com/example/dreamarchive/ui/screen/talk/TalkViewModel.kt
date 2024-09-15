package com.example.dreamarchive.ui.screen.talk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dreamarchive.BuildConfig
import com.example.dreamarchive.model.ChatGpt.GptMessage
import com.example.dreamarchive.model.ChatGpt.GptRequest
import com.example.dreamarchive.model.Meshy.MeshyRequest
import com.example.dreamarchive.network.ChatGpt.OpenAIApiService
import com.example.dreamarchive.network.Meshy.MeshyApiService
import com.example.dreamarchive.ui.screen.setting.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory


class TalkViewModel(
    private val settingViewModel: SettingViewModel,
    private val navController: NavController
): ViewModel() {
    private val _messages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages: StateFlow<List<Pair<String, Boolean>>> = _messages

    private val OPENAI_BASE_URL = "https://api.openai.com/"
    private val MESHY_BASE_URL = "https://api.meshy.ai/"

    private val retrofitOpenAI = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(OPENAI_BASE_URL)
        .build()

    private val retrofitMeshy = Retrofit.Builder() // Meshy用のRetrofitインスタンスを追加
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(MESHY_BASE_URL)
        .build()

    private val openAIApi = retrofitOpenAI.create(OpenAIApiService::class.java)
    private val meshyApi = retrofitMeshy.create(MeshyApiService::class.java) // Meshy APIのインターフェース


    // BuildConfig 経由で API キーを取得
    private val OpenAIApiKey = BuildConfig.OPENAI_API_KEY
    private val MeshyApiKey = BuildConfig.MESHY_API_KEY

    fun addMessage(message: String, isUser: Boolean) {
        _messages.value = _messages.value + (message to isUser)
    }

    fun sendMessageToGpt(inputText: String) {
        addMessage(inputText, isUser = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 良い夢モードの状態を取得
                val isGoodDreamMode = settingViewModel.isGoodDreamMode.value

                // トグルボタンに応じたプロンプトの切り替え
                val systemMessage = if (isGoodDreamMode) {
                    "あなたは作家です。今からユーザーが今朝見た夢の内容を入力するので、その続きの物語を140字程度で考えてください。必ず140字程度という文字制限を守ってください。必ず物語をハッピーエンドで終わらせてください。"
                } else {
                    "あなたは作家です。今からユーザーが今朝見た夢の内容を入力するので、その続きの物語を140字程度で考えてください。必ず140字程度という文字制限を守ってください。"
                }

                val request = GptRequest(
                    messages = listOf(
                        GptMessage("system", systemMessage),
                        GptMessage("user", inputText)
                    )
                )

                // APIキーを"Bearer "と一緒にAuthorizationヘッダーに渡す
                val authHeader = "Bearer $OpenAIApiKey"
                val response = openAIApi.sendMessage(authHeader, request).awaitResponse()

                if (response.isSuccessful) {
                    val gptResponse = response.body()
                    val gptMessage = gptResponse?.choices?.firstOrNull()?.message?.content

                    gptMessage?.let {
                        addMessage(it, isUser = false)
                        // ChatGPTからの応答が成功した後にMeshy APIを呼び出す
                        sendTextToMeshy(it) // ここでMeshy APIにテキストを送信
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

    // MeshyAPIへのリクエストを送信する関数を追加
    private fun sendTextToMeshy(generatedText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = MeshyRequest(
                    mode = "preview",
                    prompt = generatedText,
                )

                val response = meshyApi.generate3DModel("Bearer $MeshyApiKey",request)

                if (response.isSuccessful) {
                    val meshyResponse = response.body()
                    val taskId = meshyResponse?.id

                    taskId?.let {
                        addMessage("タスクID取得: $taskId", isUser = false)
                        // 3Dモデルの生成が完了するまでステータスをチェック
                        checkModelStatus(taskId)
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    addMessage("Error: ${response.code()} - $errorBody", isUser = false)
                }
            } catch (e: Exception) {
                addMessage("Failed to communicate with Meshy API: ${e.message}", isUser = false)
            }
        }
    }

    // 3Dモデルの生成ステータスをポーリングしてチェック
    private fun checkModelStatus(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var modelUrl: String? = null
                var status: String

                // 定期的にステータスをチェック（ポーリング）
                do {
                    // 少し待ってからリクエスト
                    delay(5000) // 5秒ごとにチェック

                    val response = meshyApi.checkModelStatus("Bearer $MeshyApiKey", taskId)

                    if (response.isSuccessful) {
                        val meshyResponse = response.body()
                        status = meshyResponse?.status ?: "UNKNOWN"

                        if (status == "SUCCEEDED") {
                            modelUrl = meshyResponse?.modelUrls?.glb
                        }
                    } else {
                        status = "FAILED"
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        addMessage("Error: ${response.code()} - $errorBody", isUser = false)
                        break
                    }
                } while (status != "SUCCEEDED" && status != "FAILED")

                // モデルURLが取得できた場合、AR画面に遷移
                modelUrl?.let {
                    addMessage("3Dモデル生成完了: $it", isUser = false)
                    navController.navigate("ar_screen?modelUrl=$it")
                }
            } catch (e: Exception) {
                addMessage("Failed to check model status: ${e.message}", isUser = false)
            }
        }
    }
}

class TalkViewModelFactory(
    private val settingViewModel: SettingViewModel,
    private val navController: NavController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TalkViewModel::class.java)) {
            return TalkViewModel(settingViewModel, navController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}