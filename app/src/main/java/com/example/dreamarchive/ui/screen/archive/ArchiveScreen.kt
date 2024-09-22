package com.example.dreamarchive.ui.screen.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
                            "Archive",
                            color = Color.LightGray
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigate("settingscreen") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
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
                    containerColor = darkPurple
                ) {
                    Row{
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
            },
        ) {paddingValues ->
            Column (
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()//Columnも画面全体に埋める
                    .background(darkPurple)
            ){
                Text(
                    text = "This is the archive screen.",
                    color = Color.LightGray
                )
            }
        }
    }
}

@Preview
@Composable
fun ArchiveScreenPreview(){
    val navController = rememberNavController()  //NavControllerのモックを作成
    ArchiveScreen(navController)
}