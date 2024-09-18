package com.example.dreamarchive.ui.screen.archive

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dreamarchive.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    navController: NavController
){

    //紫系統のカラーを定義
    val darkPurple = Color(0xFF6A1B9A)
    val lightPurple = Color(0xFFCE93D8)
    val mediumPurple = Color(0xFF8E24AA)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Archive",
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
            NavigationBar (
                containerColor = darkPurple
            ) {
                Row{
                    NavigationBarItem(
                        onClick = { navController.navigate("talkscreen") },
                        modifier = Modifier.weight(1f),
                        icon = {
                            Icon(imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        },
                        label = { Text("Edit", color = Color.White) },
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
                                tint = Color.White
                            )
                        },
                        label = { Text("MyARchive", color = Color.White) },
                        selected = false
                    )

                }
            }
        },
    ) {paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues)
        ){
            Text(text = "This is the archive screen.")
        }
    }
}

@Preview
@Composable
fun ArchiveScreenPreview(){
    val navController = rememberNavController()  //NavControllerのモックを作成
    ArchiveScreen(navController)
}