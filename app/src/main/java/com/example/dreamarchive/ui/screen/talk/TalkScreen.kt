package com.example.dreamarchive.ui.screen.talk

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkScreen(
        navController: NavController,
        talkViewModel: TalkViewModel = viewModel()
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
            NavigationBar {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {navController.navigate("talkscreen")},
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )

                        }

                    IconButton(
                            onClick = {navController.navigate("archivescreen")},
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star"
                            )
                        }

                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("ar_screen") }
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
            LazyColumn(
                contentPadding = innerpadding,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(messages) { message ->
                    Text(text = message.first)
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