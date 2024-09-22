package com.example.dreamarchive.ui.screen.ar

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dreamarchive.MinimalDialog

import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


//モデルインスタンスの最⼤数
private const val kMaxModelInstances = 10

@Composable
fun ARScreen(
    navController: NavController,
    decodedUrl: String? // MeshyAPIから取得したmodelUrlを引数として受け取る
){
    val viewModel: ArViewModel = viewModel()

    val decodedUrl = decodedUrl?.let {
        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
    }

    val engine = rememberEngine()
    val modelLoader=rememberModelLoader(engine =engine)

    // モデルが読み込まれたかどうかを管理するフラグ
    var isModelLoaded by remember { mutableStateOf(false) }

    val sessionConfiguration:(session: Session, Config)->Unit=
    {session,config->
        //深度モード設定
        config.depthMode=
            when(session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)){
                true->Config.DepthMode.AUTOMATIC
                else->Config.DepthMode.DISABLED
            }
        //即時配置モード設定
        config.instantPlacementMode=Config.InstantPlacementMode.LOCAL_Y_UP
        //光照モード設定
        config.lightEstimationMode=
            Config.LightEstimationMode.ENVIRONMENTAL_HDR
    }
    var planeRenderer by remember{mutableStateOf(true)}
    val view = rememberView(engine=engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    var frame by remember{mutableStateOf<Frame?>(null)}
    val materialLoader=rememberMaterialLoader(engine=engine)
    val modelInstances=remember{mutableListOf<ModelInstance>()}

    val onSessionUpdated:(session:Session, frame:Frame)->Unit=
    {session, updatedFrame->
        frame=updatedFrame
        if(childNodes.isEmpty() && isModelLoaded){
            //更新された平⾯情報から、⽔平平⾯を検索
            updatedFrame.getUpdatedPlanes()
                .firstOrNull{it.type== Plane.Type.HORIZONTAL_UPWARD_FACING}
                ?.let{it.createAnchorOrNull(it.centerPose)}
                ?.let { anchor ->
                    //追加
                    childNodes += createAnchorNode(
                        engine = engine,
                        modelLoader = modelLoader,
                        modelInstances = modelInstances,
                        anchor = anchor,
                    )
                }
        }

    }
    val onGestureListener=rememberOnGestureListener(
        //motionEventはジェスチャーイベント、nodeはタップされたノード
        onSingleTapConfirmed={motionEvent, node->
            //タップされた位置が空の場合
            if(node==null){
                val hitResults=frame?.hitTest(motionEvent.x,motionEvent.y)
                hitResults?.firstOrNull{
                    it.isValid(
                        depthPoint=false,
                        point=false
                    )
                }?.createAnchorOrNull()
                    ?.let{anchor->
                        planeRenderer=false
                        childNodes+= createAnchorNode(
                            engine=engine,
                            modelLoader=modelLoader,
                            modelInstances=modelInstances,
                            anchor=anchor
                        )
                    }
            }
        }
    )

    // モデルを非同期で読み込む
    LaunchedEffect(decodedUrl) {
        decodedUrl?.let { url ->
            // ModelInstanceを非同期にロード
            modelLoader.loadModelInstanceAsync(url) { modelInstance ->
                modelInstance?.let { instance ->
                    // ModelInstanceを使ってModelNodeを作成
                    val modelNode = ModelNode(modelInstance = instance).apply {
                        isEditable = true   // 編集可能に設定
                    }
                    // ModelNodeを子ノードに追加
                    childNodes.add(modelNode)
                    isModelLoaded = true // モデルが正常に読み込まれたことを示す
                }
            }
        }
    }
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            sessionConfiguration = sessionConfiguration,
            planeRenderer = planeRenderer,
            view = view,
            cameraNode = cameraNode,
            childNodes = childNodes,
            onSessionUpdated = onSessionUpdated,
            onGestureListener = onGestureListener
        )

    //popupの設定
    // ダイアログ表示用の状態を追加
    var showDialog by remember { mutableStateOf(true) }

    // ダイアログの表示
    if (showDialog) {
        MinimalDialog2(onDismissRequest = { showDialog = false })
    }
}

// 仮の関数：ARSceneからBitmapを取得する方法は使用しているライブラリによります
fun getBitmapFromARScene(): Bitmap {
    // 実装を追加
    return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}


suspend fun saveBitmapToFile(context: Context, bitmap: ImageBitmap): File {
    val filename = "photo_${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { out ->
        bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return file
}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    modelInstances:MutableList<ModelInstance>,
    anchor: Anchor
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelInstances.removeLastOrNull() ?: return anchorNode,
        scaleToUnits = 0.5f
    ).apply {
        isEditable = true
    }
    anchorNode.addChildNode(modelNode)
    return anchorNode
}

@Composable
fun MinimalDialog2(onDismissRequest: () -> Unit) {
    MinimalDialog(
        onDismissRequest = onDismissRequest,
        text = "あなたの夢の記憶が現れました!\n現実と重ね合わせてみましょう！"
    )
}

