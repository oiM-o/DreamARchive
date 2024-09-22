package com.example.dreamarchive.ui.screen.talk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dreamarchive.ui.screen.setting.SettingViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.dreamarchive.MinimalDialog
import com.example.dreamarchive.R
import java.time.LocalDate



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
    val darkPurple = Color(0xE623054B)
    val lightPurple = Color(0xFFCE93D8)
    val mediumPurple = Color(0xFF8E24AA)
    val lightGrey = Color(0xFFE0E0E0)
    val mediumGrey = Color(0xD9373364)

    //キーボードを閉じるためのFocusManagerを取得
    val focusManager = LocalFocusManager.current


    // Meshy APIの現在のステータスを監視
    val currentStatus by talkViewModel.currentStatus.collectAsState()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    //popupの設定
    // ダイアログ表示用の状態を追加
    var showDialog by remember { mutableStateOf(true) }

    // ダイアログの表示
    if (showDialog) {
        MinimalDialog1(onDismissRequest = {
            showDialog = false
        })
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "DreamARchive",
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
                    containerColor = mediumGrey
                )
            )
        },
        bottomBar = {
            Column (
                modifier = Modifier
                    .background(darkPurple)
            ){
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
                                tint = darkPurple
                            )
                        }
                    },
                    label = { Text(text = "Tell me your dream...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = mediumGrey,
                        unfocusedTextColor = lightGrey
                    )
                )

                NavigationBar(
                    containerColor = mediumGrey
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.partly_cloudy_night_24dp_5f6368_fill0_wght400_grad0_opsz24),
                                contentDescription = "Edit",
                                tint = Color.LightGray
                            )
                        },
                        label = { Text("Edit", color = Color.LightGray) },
                        selected = currentRoute == "TalkScreen",
                        onClick = { navController.navigate("TalkScreen") },
                        // Active Indicatorの追加
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = darkPurple,
                            unselectedIconColor = Color.LightGray,
                            indicatorColor = lightPurple // 選択時のインジケーターの色
                        ),



                    )
                    NavigationBarItem(
                        icon = {
                            // drawable フォルダにあるリソースを呼び出し
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_import_contacts_24),
                                contentDescription = "MyARchive",
                                tint = Color.LightGray
                            )
                        },
                        label = { Text("MyARchive", color = Color.LightGray) },
                        selected = false,
                        onClick = { navController.navigate("ArchiveScreen") },
                        // Active Indicatorの追加
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = darkPurple,
                            unselectedIconColor = Color.LightGray,
                            indicatorColor = mediumPurple // 選択時のインジケーターの色
                        ),

                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0), // これでキーボード表示時のレイアウト調整を防ぐ
        modifier = Modifier
            .fillMaxSize()
            .background(darkPurple)
            .imePadding(),// キーボード表示時のパディング調整
    )


    { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()//サイズをスクリーン全体に
                .padding(innerpadding)
                .background(darkPurple)
        ) {
            LazyColumn(
                contentPadding = innerpadding,
                modifier = Modifier
                    .weight(1f)//画面の残りの領域を使用
                    .padding(horizontal = 16.dp, vertical = 8.dp)

            ) {

                items(messages) { message ->
                    //メッセージがユーザーからかGPTからかで分岐
                    val isUserMessage = message.second

                    //左上だけを尖らせるためのカスタムシェイプ
                    fun BubbleShape(): Shape {
                        return RoundedCornerShape(
                            topStart = CornerSize(0.dp),
                            topEnd = CornerSize(16.dp),
                            bottomEnd = CornerSize(16.dp),
                            bottomStart = CornerSize(16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start,//両方を左揃えに
                        verticalAlignment = Alignment.Top//アイコンとメッセージを上揃えにする
                    ) {
                        if (!isUserMessage) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp) // 円のサイズをアイコンより大きく
                                    .background(color = darkPurple,shape = CircleShape), // 丸い背景を作る
                                contentAlignment = Alignment.Center // 中央にアイコンを配置
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.smart_toy_24dp_5f6368_fill0_wght400_grad0_opsz24),
                                    contentDescription = "GPT Icon",
                                    modifier = Modifier
                                        .size(40.dp),
                                    tint = Color.LightGray
                                )
                            }


                            // GPTの吹き出しメッセージ
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 16.dp) // メッセージ間の余白
                                    .background(mediumGrey.copy(alpha = 0.2f), shape = BubbleShape())//背景に淡い紫, 左上を尖らせた吹き出し
                                    .border(1.dp, Color.Gray, BubbleShape()) // 枠の設定
                                    .padding(16.dp) // 枠の内側にパディングを追加

                            ) {
                                Text(text = message.first, color = lightGrey)
                            }
                        } else {
                            //ユーザー側のアイコン
                            Box(
                                modifier = Modifier
                                    .size(60.dp) // 円のサイズをアイコンより大きく
                                    .background(color = darkPurple,shape = CircleShape), // 丸い背景を作る
                                contentAlignment = Alignment.Center // 中央にアイコンを配置
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Icon",
                                    modifier = Modifier
                                        .size(40.dp),
                                    tint = Color.LightGray
                                )
                            }


                            //ユーザーの吹き出しメッセージ
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 16.dp) // メッセージ間の余白
                                    .background(mediumGrey.copy(alpha = 0.2f), shape = BubbleShape())//背景に淡い紫
                                    .border(1.dp, Color.Gray, BubbleShape()) // 枠の設定
                                    .padding(16.dp) // 枠の内側にパディングを追加

                            ) {
                                Text(text = message.first, color = lightGrey)
                            }
                        }
                    }
                }
            }
            // ローディングアニメーションの表示
            if (currentStatus == "PENDING" || currentStatus == "IN_PROGRESS") {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = progress,
                        modifier = Modifier.size(150.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun MinimalDialog1(onDismissRequest: () -> Unit) {
    MinimalDialog(
        onDismissRequest = onDismissRequest,
        text = "今日はどんな夢をみましたか？\nその夢もう一度思い出してみて"
    )
}





@Preview
@Composable
fun TalkScreenPreview(){
    val navController = rememberNavController() //NavControllerのモックを作成
    TalkScreen(navController)
}