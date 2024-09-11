package com.example.dreamarchive.ui.screen.talk

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dreamarchive.ui.screen.setting.SettingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkScreen(
    navController: NavController,
    settingViewModel: SettingViewModel = viewModel(),  // 設定のViewModelを取得
    talkViewModel: TalkViewModel = viewModel(factory = TalkViewModelFactory(settingViewModel)) // TalkViewModelに設定のViewModelを渡す
) {

    val messages by talkViewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "DreamARchive"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate("settingscreen") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "SettingDrawer"
                        )
                    }
                }
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
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "send_message_to_GPT"
                            )
                        }
                    },
                    label = { Text(text = "Type your message...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
                NavigationBar {
                    NavigationBarItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        },
                        label = { Text("Edit") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Star")
                        },
                        label = { Text("Star") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        },
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
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(innerpadding)
        ) {
            LazyColumn(
                contentPadding = innerpadding,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(messages) { message ->
                    // メッセージを枠で囲む
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp) // メッセージ間の余白
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)) // 枠の設定
                            .padding(8.dp) // 枠の内側にパディングを追加
                    ) {
                        Text(text = message.first)
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