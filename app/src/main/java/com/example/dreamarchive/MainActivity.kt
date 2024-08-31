package com.example.dreamarchive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dreamarchive.ui.screen.talk.TalkScreen
import com.example.dreamarchive.ui.theme.DreamARchiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamARchiveTheme {
                TalkScreen()
            }
        }
    }
}