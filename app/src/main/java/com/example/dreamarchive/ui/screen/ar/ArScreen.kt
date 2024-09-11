package com.example.dreamarchive.ui.screen.ar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
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
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

//3Dモデルファイルのパス
private const val kModelFile="models/model.glb"
//モデルインスタンスの最⼤数
private const val kMaxModelInstances = 10

@Composable
fun ARScreen(
    navController: NavController
){
    val engine = rememberEngine()
    val modelLoader=rememberModelLoader(engine =engine)
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
        if(childNodes.isEmpty()){
            //更新された平⾯情報から、⽔平平⾯を検索
            updatedFrame.getUpdatedPlanes()
                .firstOrNull{it.type== Plane.Type.HORIZONTAL_UPWARD_FACING}
                ?.let{it.createAnchorOrNull(it.centerPose)}?.let { anchor ->
                    //追加
                    childNodes += createAnchorNode(
                        engine = engine,
                        modelLoader = modelLoader,
                        materialLoader = materialLoader,
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
                            materialLoader=materialLoader,
                            modelInstances=modelInstances,
                            anchor=anchor
                        )
                    }
            }
        })

        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            sessionConfiguration=sessionConfiguration,
            planeRenderer=planeRenderer,
            view = view,
            cameraNode = cameraNode,
            childNodes = childNodes,
            onSessionUpdated=onSessionUpdated,
            onGestureListener= onGestureListener
    )
}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances:MutableList<ModelInstance>,
    anchor: Anchor
): AnchorNode {
    //anchorNodeの作成
    val anchorNode=AnchorNode(engine =engine,anchor=anchor)
    //ModelNodeの作成
    val modelNode= ModelNode(
        modelInstance=modelInstances.apply {
            if(isEmpty()){
                //kModelFile,kMaxModelInstancesはあとで定義します
                this+=modelLoader
                    .createInstancedModel(kModelFile,kMaxModelInstances)
            }
        }.removeLast(),
        //0.5メートルの⽴⽅体に収まる⼤きさに調整します
        scaleToUnits=0.5f
    ).apply{
        //ModelNodeがAnchorとは独⽴して回転できるように、編集可能(true)に設定する
        isEditable=true
    }
    //boundingBoxNodeの作成
    val boundingBoxNode= CubeNode(
        engine,
        size= modelNode.extents,
        center=modelNode.center,
        materialInstance=
        materialLoader.createColorInstance(Color.White.copy(alpha=0.5f))
    ).apply{
        //⾮表⽰に設定
        isVisible=false
    }
    //modelNodeにboundingBoxNodeを⼦ノードとして追加
    modelNode.addChildNode(boundingBoxNode)
    //anchorNodeにmodelNodeを⼦ノードとして追加
    anchorNode.addChildNode(modelNode)
    //modelNodeとanchorNodeの編集状態が変更されたら、
    //boundingBoxNodeの表⽰状態を編集中のみ表⽰するように設定
    listOf(modelNode,anchorNode).forEach {
        it.onEditingChanged={editingTransforms->
            boundingBoxNode.isVisible=editingTransforms.isNotEmpty()
        }
    }
        return anchorNode
}
