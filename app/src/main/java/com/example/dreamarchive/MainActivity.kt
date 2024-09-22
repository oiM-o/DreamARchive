package com.example.dreamarchive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        composable(
            route = "ar_screen?modelUrl={modelUrl}",
            arguments = listOf(
                navArgument("modelUrl") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val modelUrl = backStackEntry.arguments?.getString("modelUrl")
            ARScreen(navController, decodedUrl = modelUrl)
        }
    }
}

@Composable
fun MinimalDialog(
    onDismissRequest: () -> Unit,
    text: String,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )

        }
    }
}