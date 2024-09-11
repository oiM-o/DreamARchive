package com.example.dreamarchive.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    settingViewModel: SettingViewModel = viewModel() // ViewModelを取得
){
    // ViewModelの状態を監視
    val isGoodDreamMode by settingViewModel.isGoodDreamMode.collectAsState()

    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Setting"
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "SettingDrawer"
                            )
                        }
                    }
                )
            },
        bottomBar = {
            NavigationBar {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navController.navigate("talkscreen") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )

                    }

                    IconButton(
                        onClick = { navController.navigate("archivescreen") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star"
                        )
                    }

                }
            }
        }
    ) {paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues)
        ){
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Text(text = "いい夢モード")
                Switch(
                    checked = isGoodDreamMode,
                    onCheckedChange = { enabled ->
                        settingViewModel.toggleGoodDreamMode(enabled) // 状態を更新
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingScreenPreview(){
    val navController = rememberNavController() //NavControllerのモックを作成
    SettingScreen(navController)
}