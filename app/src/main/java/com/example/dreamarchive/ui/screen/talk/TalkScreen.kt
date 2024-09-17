package com.example.dreamarchive.ui.screen.talk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dreamarchive.ui.screen.setting.SettingViewModel
import androidx.compose.ui.res.painterResource
import com.example.dreamarchive.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkScreen(
    navController: NavController,
    settingViewModel: SettingViewModel = viewModel(),  // 設定のViewModelを取得
    talkViewModel: TalkViewModel = viewModel(factory = TalkViewModelFactory(settingViewModel, navController)) // TalkViewModelに設定のViewModelを渡す
) {
    LaunchedEffect(Unit) {
        talkViewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    val messages by talkViewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    //紫系統のカラーを定義
    val darkPurple = Color(0xFF6A1B9A)
    val lightPurple = Color(0xFFCE93D8)
    val mediumPurple = Color(0xFF8E24AA)

    //キーボードを閉じるためのFocusManagerを取得
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "DreamARchive",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate("settingscreen") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "SettingDrawer",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkPurple
                )
            )
        },
        bottomBar = {
            Column {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { newText -> inputText = newText },
                    singleLine = false,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                talkViewModel.sendMessageToGpt(inputText)
                                inputText = "" // 送信後にテキストフィールドをクリア

                                //フォーカスを解除してキーボードを閉じる
                                focusManager.clearFocus()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "send_message_to_GPT",
                                tint = mediumPurple
                            )
                        }
                    },
                    label = { Text(text = "Type your message...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = mediumPurple,
                        unfocusedTextColor = lightPurple
                    )
                )
                NavigationBar(
                    containerColor = darkPurple
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        },
                        label = { Text("Edit", color = Color.White) },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    NavigationBarItem(
                        icon = {
                            // drawable フォルダにあるリソースを呼び出し
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_import_contacts_24),
                                contentDescription = "MyARchive",
                                tint = Color.White
                            )
                        },
                        label = { Text("MyARchive", color = Color.White) },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0), // これでキーボード表示時のレイアウト調整を防ぐ
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),// キーボード表示時のパディング調整
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("arscreen") }
            ) {
                Text("Go to AR Screen")
            }
        },
    )
    { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()//サイズをスクリーン全体に
                .padding(innerpadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)//画面の残りの領域を使用
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(messages) { message ->
                    // メッセージを枠で囲む
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp) // メッセージ間の余白
                            .background(lightPurple.copy(alpha = 0.2f))//背景に淡い紫
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)) // 枠の設定
                            .padding(16.dp) // 枠の内側にパディングを追加

                    ) {
                        Text(text = message.first, color = darkPurple)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TalkScreenPreview(){
    val navController = rememberNavController() //NavControllerのモックを作成
    TalkScreen(navController)
}