package com.example.dreamarchive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreamarchive.ui.screen.ar.ARScreen
import com.example.dreamarchive.ui.screen.archive.ArchiveScreen
import com.example.dreamarchive.ui.screen.setting.SettingScreen
import com.example.dreamarchive.ui.screen.talk.TalkScreen
import com.example.dreamarchive.ui.theme.DreamARchiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamARchiveTheme {
                NavigationApp()
            }
        }
    }
}

@Composable
fun NavigationApp(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "talkscreen") {
        composable("talkscreen"){ TalkScreen(navController) }
        composable("settingscreen"){ SettingScreen(navController) }
        composable("archivescreen"){ ArchiveScreen(navController) }
        composable("arscreen"){ ARScreen(navController) }
    }
}