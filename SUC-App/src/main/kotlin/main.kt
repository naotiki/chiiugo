import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.animatedimage.Blank
import org.jetbrains.compose.animatedimage.animate
import org.jetbrains.compose.animatedimage.loadResourceAnimatedImage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.loadOrNull
import org.jetbrains.compose.resources.painterResource
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.roundToInt
import kotlin.random.Random


val colorList =
    listOf<Long>(0xFFFFFF00, 0xFF00FF00, 0xFFFF0000, 0xFF0000FF, 0xFF7DEBEB, 0xFFFF9B00, 0xFF800080, 0xFFFF1493)

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    //1dp あたりのpx数を取得 remember いらない
    ScreenSize.density = LocalDensity.current.density
    //Windowサイズや位置の情報が入っている

    val configState by ConfigManager.configState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val winX = remember { Random.nextFloat() * ScreenSize.widthDp.value }
    val winY = remember { Random.nextFloat() * ScreenSize.heightDp.value }
    val windowState = rememberWindowState(size = DpSize.Unspecified, position = WindowPosition(winX.dp, winY.dp))
    //アニメーション用の位置
    val animatedWindowPosition =
        remember { Animatable(windowState.position as WindowPosition.Absolute, WindowPositionToVector) }
    val statePos by animatedWindowPosition.asState()
    //Window位置を更新
    LaunchedEffect(statePos) {
        windowState.position = statePos
    }
    val serverState = rememberServerState()
    //SUCの状態
    val mascotState = rememberMascotState(MascotEventType.Run, serverState)
    val mascotEventType by mascotState.flow.collectAsState()
    //       ↓MascotEventTypeが変更されたら初期値 SUC.gifに
    var gifName by remember(mascotEventType) { mutableStateOf("SUC.gif") }
    val color = remember(mascotEventType) { androidx.compose.animation.Animatable(Color.White/*初期の色*/) }
    val colorState by color.asState()
    val charMap = remember { mutableStateListOf<Pair<Char, Pair<Int, Animatable<Float, AnimationVector1D>>>>() }
    LaunchedEffect(Unit) {
        launch { serverState.initServer() }
        launch {//タイピング連動機能
            mascotState.charFlow.collectLatest {
                val anim = Animatable(0f)
                val e = it to (Random.nextInt(imageSizeDp.value.roundToInt()) to anim)
                charMap.add(e)
                coroutineScope.launch {
                    anim.animateTo(imageSizeDp.value - 10, tween(2000, easing = EaseOutBounce))
                    delay(Random.nextLong(500, 2000))
                    charMap.remove(e)
                }
            }
        }
        while (true) {
            delay(Random.nextLong(5000, 7000))
            val stt = (0 .. 2).random()
            if (stt == 0){
                mascotState.change(MascotEventType.flyingSUC)
            }else if(stt == 1){
                mascotState.change(MascotEventType.Gaming)
            }else{
                mascotState.change(MascotEventType.Run)//苔の侵食
            }
            mascotState.speak(texts.random(), 5000)
        }
    }
    //val charList= remember() { mutableStateListOf<Char>() }
    LaunchedEffect(mascotEventType) {
        when (val eventType = mascotEventType) {
            MascotEventType.Explosion -> {
                gifName = "boom.gif"
                mascotState.speak("ビルド失敗！！！", 5000, true)
            }//コンパイルエラー
            MascotEventType.Fall -> TODO()//ランダム
            MascotEventType.Gaming -> {//ゲーミング〇〇〇華道部
                gifName = "upleft.gif"
                val aho = launch { while(true) {
                    var random = Color(colorList.random())
                    while (colorState == random) {
                        random = Color(colorList.random())
                    }
                    color.animateTo(random, tween(160, easing = EaseInBack))
                } }
                val x =
                    ScreenSize.widthDp.value * configState.areaOffset.first + (Random.nextFloat() * ScreenSize.widthDp.value * configState.areaSize.first)
                val y =
                    ScreenSize.heightDp.value * configState.areaOffset.second + (Random.nextFloat() * ScreenSize.heightDp.value * configState.areaSize.second)

                gifName = if (x > windowState.position.x.value) {
                    if (y >= windowState.position.y.value) {
                        "downright.gif"
                    } else {
                        "upright.gif"
                    }
                } else {
                    if (y > windowState.position.y.value) {
                        "downleft.gif"
                    } else {
                        "upleft.gif"
                    }
                }
                animatedWindowPosition.animateTo(
                    animatedWindowPosition.value.copy(
                        x.dp,
                        y.dp
                    ),
                    tween(
                        5000, easing = EaseInOut
                    )
                )
                aho.cancel()
                color.snapTo(Color.White)
                mascotState.change(MascotEventType.Run)
            }

            MascotEventType.None -> {
                gifName = "SUC.gif"
            }

            MascotEventType.Run -> {
                //TODO アニメーション書いてちょ

                while (true) {
                    val x =
                        ScreenSize.widthDp.value * configState.areaOffset.first + (Random.nextFloat() * ScreenSize.widthDp.value * configState.areaSize.first)
                    val y =
                        ScreenSize.heightDp.value * configState.areaOffset.second + (Random.nextFloat() * ScreenSize.heightDp.value * configState.areaSize.second)

                    gifName = if (x > windowState.position.x.value) {
                        if (y >= windowState.position.y.value) {
                            "downright.gif"
                        } else {
                            "upright.gif"
                        }
                    } else {
                        if (y > windowState.position.y.value) {
                            "downleft.gif"
                        } else {
                            "upleft.gif"
                        }
                    }
                    animatedWindowPosition.animateTo(
                        animatedWindowPosition.value.copy(
                            x.dp,
                            y.dp
                        ),
                        tween(
                            5000, easing = EaseInOut
                        )
                    )
                }
            }
            /*MascotEventType.Chat->{
                while (true){
                    serif=texts.random()
                    delay(5000)
                }
            }
            is MascotEventType.Speak -> {
                serif=eventType.text
                delay(5000)
                mascotState.recoverEvent()
            }*/

            MascotEventType.flyingSUC -> {
                gifName = "flyingSUC.gif"
                delay(3200)
                mascotState.change(MascotEventType.Run)
            }

            MascotEventType.DVD -> {
                /* val x = windowState.position.x.value
                 val y = windowState.position.y.value

                 //傾き
                 val bias = Random.nextFloat() * 3 * Random.nextSign()

                 val y1= bias*((ScreenSize.widthDp-imageSizeDp.value)-x)
                 if (y1-y in 0.0f..(ScreenSize.heightDp-imageSizeDp.value)) {
                     //水平軸へのCollision
                     val deltaX=(y-y1)/bias

                 }else{
                     //垂直軸への
                 }


                 val deltaX = newX - x
                 val deltaY = newY - y
                 val distance = hypot(deltaX, deltaY)
                 val cos = deltaX / distance
                 val sin = deltaY / distance
                 if (vertical) {

                 } else {

                 }*/
            }
            MascotEventType.Chat -> TODO()
            is MascotEventType.Speak -> TODO()
        }
        //Noneに戻す
        //mascotState.change(MascotEventType.None)
    }
    //Windowを表示
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        resizable = false,
        transparent = true,
        undecorated = true,
        alwaysOnTop = configState.alwaysTop//TODO デバッグ用に切った 本番時 true
    ) {

        Box(modifier = Modifier) {
            //SUCちゃん
            Image(
                loadOrNull(gifName) {
                    loadResourceAnimatedImage(gifName)
                }?.animate() ?: ImageBitmap.Blank,
                null,
                Modifier.size(imageSizeDp), colorFilter = ColorFilter.tint(colorState, BlendMode.Modulate),
            )
            val serif by mascotState.serifFlow.collectAsState()
            if (serif != null) {
                Box(
                    modifier = Modifier.padding(start = 150.dp, top = 50.dp).width(150.dp)
                ) {
                    Text(
                        serif ?: "", modifier = Modifier
                            .background(
                                color = Color(0xff5ff4ac),
                                shape = RoundedCornerShape(30)
                            )
                            .padding(10.dp)
                    )
                }
            } else Spacer(Modifier.width(300.dp))
            charMap.forEach { (c, a) ->
                val anim by a.second.asState()
                Text(c.toString(), Modifier.offset(x = a.first.dp, y = anim.dp), color = Color.Red)
            }
        }
    }

    var controlWindowTab by remember { mutableStateOf<Int?>(0) }
    var exitCount by remember { mutableStateOf(0) }
    Tray(painterResource("SUC.png")) {
        Item("設定") {
            controlWindowTab = 1
        }
        Item("統計") {
            controlWindowTab = 0
        }
        Item("閉じる") {
            exitCount = 5
        }
    }
        val dialogState= rememberDialogState()
        Dialog({ exitCount=0 },dialogState, visible = exitCount!=0, title = "確認"){
            Box(Modifier.fillMaxSize().padding(15.dp,5.dp)) {
                Column {
                    Text("本当に閉じますか？")
                    Text("(あと $exitCount 回)")
                }
                Row(Modifier.align(Alignment.BottomEnd), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Button({exitCount=0}){
                        Text("No")
                    }
                    Button({
                        if (exitCount--<=0){
                            exitApplication()
                        }else{
                            dialogState.position= WindowPosition(BiasAlignment(Random.nextInt(-1,1).toFloat(),Random.nextInt(-1,1).toFloat()))
                        }
                    }){
                        Text("Yes")
                    }
                }
            }
        }

    ControlWindow(controlWindowTab != null, serverState, { controlWindowTab = null }, controlWindowTab ?: 0)
}

val imageSizeDp: Dp = 175.dp


fun Random.Default.nextSign() = if (nextBoolean()) 1 else -1
