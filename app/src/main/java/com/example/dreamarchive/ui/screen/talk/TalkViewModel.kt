package com.example.dreamarchive.ui.screen.talk

import android.util.Log
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class TalkViewModel(
    private val settingViewModel: SettingViewModel,
    private val navController: NavController
): ViewModel() {
    private val TAG = "TalkViewModel"
    private val _messages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages: StateFlow<List<Pair<String, Boolean>>> = _messages

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // 現在のMeshy APIのステータスを保持するStateFlowを追加
    private val _currentStatus = MutableStateFlow<String?>(null)
    val currentStatus: StateFlow<String?> = _currentStatus

    private val OPENAI_BASE_URL = "https://api.openai.com/"
    private val MESHY_BASE_URL = "https://api.meshy.ai/"

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofitOpenAI = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(OPENAI_BASE_URL)
        .build()

    private val retrofitMeshy = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(MESHY_BASE_URL)
        .build()

    private val openAIApi = retrofitOpenAI.create(OpenAIApiService::class.java)
    private val meshyApi = retrofitMeshy.create(MeshyApiService::class.java) // Meshy APIのインターフェース

    // BuildConfig 経由で API キーを取得
    private val OpenAIApiKey = BuildConfig.OPENAI_API_KEY
    private val MeshyApiKey = BuildConfig.MESHY_API_KEY

    @Volatile
    private var isProcessingMeshyTask = false

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
        // 同時実行タスクがある場合は新たなリクエストを送信しない
        if (isProcessingMeshyTask) {
            Log.e(TAG, "A Meshy task is already in progress. Please wait.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isProcessingMeshyTask = true
                Log.d(TAG, "sendTextToMeshy called with generatedText: $generatedText")

                val request = MeshyRequest(
                    mode = "preview",
                    prompt = generatedText,
                )

                val response = meshyApi.generate3DModel("Bearer $MeshyApiKey",request)

                if (response.isSuccessful) {
                    val meshyCreateResponse = response.body()
                    Log.d(TAG, "MeshyAPI Response: $meshyCreateResponse") // 修正後

                    val taskId = meshyCreateResponse?.result // "result" フィールドから取得
                    val status = "PENDING" // 初期ステータス。APIが即時にステータスを返さない場合
                    _currentStatus.value = status

                    Log.d(TAG, "MeshyAPI Status: $status") // ステータスをログ出力

                    taskId?.let {
                        // 3Dモデルの生成が完了するまでステータスをチェック
                        checkModelStatus(it)
                    }?: run {
                        Log.e(TAG, "Failed to retrieve taskId from MeshyAPI response.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Error: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to communicate with Meshy API: ${e.message}", e)
            }finally {
                isProcessingMeshyTask = false // タスク終了
            }
        }
    }

    // 3Dモデルの生成ステータスをポーリングしてチェック
    private fun checkModelStatus(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var modelUrl: String? = null
                var status: String
                var attempts = 0
                val maxAttempts = 100

                // 定期的にステータスをチェック（ポーリング）
                do {
                    // 少し待ってからリクエスト
                    delay(10000) // 10秒ごとにチェック
                    attempts++

                    val response = meshyApi.checkModelStatus("Bearer $MeshyApiKey", taskId)

                    if (response.isSuccessful) {
                        val meshyResponse = response.body()
                        status = meshyResponse?.status ?: "UNKNOWN"
                        Log.d(TAG, "MeshyAPI Status: $status") // ステータスをログ出力
                        Log.d(TAG, "MeshyAPI Response: $meshyResponse") // レスポンス全体をログ出力

                        // 現在のステータスを更新
                        _currentStatus.value = status // コメントアウト: 現在のステータスを更新

                        if (status.equals("SUCCEEDED", ignoreCase = true)) {
                            modelUrl = meshyResponse?.modelUrls?.glb
                            Log.d(TAG, "GLB URL: ${meshyResponse?.modelUrls?.glb}")
                        }else if (status.equals("FAILED", ignoreCase = true)) {
                            // モデル生成に失敗した場合
                            Log.e(TAG, "Model generation failed.")
                            break
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Log.e(TAG, "Error: ${response.code()} - $errorBody")
                        break
                    }
                    if (attempts >= maxAttempts) {
                        Log.e(TAG, "Model generation timed out.")
                        break
                    }
                } while (status != "SUCCEEDED")

                // モデルURLが取得できた場合、AR画面に遷移
                modelUrl?.let {
                    val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                    Log.d(TAG, "3Dモデル生成完了: $it")
                    withContext(Dispatchers.Main) {
                        _navigationEvent.emit("ar_screen?modelUrl=$encodedUrl")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check model status: ${e.message}", e)
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