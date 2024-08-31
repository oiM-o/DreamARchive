package com.example.dreamarchive.ui.screen.talk

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkScreen(
        talkViewModel: TalkViewModel = viewModel()
) {

    val messages by talkViewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "TalkRoom"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ }
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

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New_Talk")
            }
        }
    ) { innerpadding ->
        Column(
            modifier =
                Modifier.fillMaxWidth().padding(innerpadding)
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
    TalkScreen()
}