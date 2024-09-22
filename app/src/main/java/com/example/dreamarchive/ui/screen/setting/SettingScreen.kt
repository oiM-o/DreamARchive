package com.example.dreamarchive.ui.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dreamarchive.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    settingViewModel: SettingViewModel = viewModel() // ViewModelを取得
){
    // ViewModelの状態を監視
    val isGoodDreamMode by settingViewModel.isGoodDreamMode.collectAsState()

    //紫系統のカラーを定義g
    val darkPurple = Color(0xE6030126)
    val lightPurple = Color(0xFFCE93D8)
    val mediumPurple = Color(0xFF8E24AA)
    val lightGrey = Color(0xFFE0E0E0)
    val mediumGrey = Color(0xD9201D3A)
    val darkGrey = Color(0xFF211A3A)

    Box( //全体の背景色を指定
        modifier = Modifier
            .fillMaxSize() // 画面全体を埋める
            .background(darkPurple) // 背景色をdarkPurpleに設定
    ){
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Setting" ,
                            color = Color.LightGray
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "SettingDrawer",
                                tint = Color.LightGray
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = darkGrey
                    )
                )
            },
            bottomBar = {
                NavigationBar (
                    containerColor = darkGrey
                ) {
                    Row {
                        NavigationBarItem(
                            onClick = { navController.navigate("talkscreen") },
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.partly_cloudy_night_24dp_5f6368_fill0_wght400_grad0_opsz24),
                                    contentDescription = "Edit",
                                    tint = Color.LightGray
                                )
                            },
                            label = { Text("Edit", color = Color.LightGray) },
                            selected = false
                        )

                        NavigationBarItem(
                            onClick = { navController.navigate("archivescreen") },
                            modifier = Modifier.weight(1f),
                            icon = {
                                // drawable フォルダにあるリソースを呼び出し
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_import_contacts_24),
                                    contentDescription = "MyARchive",
                                    tint = Color.LightGray
                                )
                            },
                            label = { Text("MyARchive", color = Color.LightGray) },
                            selected = false
                        )
                    }
                }
            }
        ) {paddingValues ->
            Row (
                modifier = Modifier
                    .fillMaxSize() // 画面全体を使う
                    .padding(paddingValues)
                    .background(darkPurple),
                horizontalArrangement = Arrangement.SpaceBetween, // 横方向の中央揃え
                verticalAlignment = Alignment.Top// 縦方向の中央揃え

            ){
                // TextとSwitchの配置を縦に
                Text(
                    text = "いい夢モード",
                    modifier = Modifier.padding(bottom = 8.dp), // Switchとの間に少しスペースを作成
                    style = MaterialTheme.typography.headlineSmall,// テキストスタイルを少し大きめに
                    color = Color.LightGray
                )
                Switch(
                    checked = isGoodDreamMode,
                    onCheckedChange = { enabled ->
                        settingViewModel.toggleGoodDreamMode(enabled) // 状態を更新
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = lightPurple,
                        uncheckedThumbColor = Color.Gray
                    )
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