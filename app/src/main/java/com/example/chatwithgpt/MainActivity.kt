package com.example.chatwithgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.chatwithgpt.ui.screen.talk.TalkScreen
import com.example.chatwithgpt.ui.theme.ChatWithGPTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatWithGPTTheme {
                TalkScreen()
            }
        }
    }
}