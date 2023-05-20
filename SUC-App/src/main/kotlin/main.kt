import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.invoke
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layout
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.letsplot.y
import kotlin.math.roundToInt
import kotlin.random.Random
import org.jetbrains.kotlinx.kandy.util.color.Color.Companion as KandyColor


@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    //1dp あたりのpx数を取得 remember いらない
    ScreenSize.density = LocalDensity.current.density
    //Windowサイズや位置の情報が入っている
    val coroutineScope = rememberCoroutineScope()
    val winX = remember { Random.nextFloat() * ScreenSize.widthDp }
    val winY = remember { Random.nextFloat() * ScreenSize.heightDp }
    val windowState = rememberWindowState(size = DpSize.Unspecified, position = WindowPosition(winX.dp, winY.dp))
    //アニメーション用の位置
    val animatedWindowPosition =
        remember { Animatable(windowState.position as WindowPosition.Absolute, WindowPositionToVector) }
    val statePos by animatedWindowPosition.asState()
    //Window位置を更新
    LaunchedEffect(statePos) {
        windowState.position = statePos
    }

    //SUCの状態
    val mascotState = rememberMascotState(MascotEventType.Run)
    val mascotEventType by mascotState.flow.collectAsState()
    //       ↓MascotEventTypeが変更されたら初期値 SUC.gifに
    var gifName by remember(mascotEventType) { mutableStateOf("SUC.png") }
    var show by remember { mutableStateOf(true) }
    val colorList = listOf<Long>(0xFFFFFF00, 0xFF00FF00, 0xFFFF0000, 0xFF0000FF, 0xFF7DEBEB, 0xFFFF9B00,0xFF800080,0xFFFF1493)
    val color = remember { androidx.compose.animation.Animatable(Color.White/*初期の色*/) }
    val colorState by color.asState()
    val charMap = remember { mutableStateMapOf<Char, Pair<Int, Animatable<Float, AnimationVector1D>>>() }
    LaunchedEffect(Unit){
        launch{ mascotState.initServer() }
        launch {//タイピング連動機能
            mascotState.charFlow.collectLatest {
                val anim = Animatable(0f)
                charMap[it] = Random.nextInt(imageSizeDp.value.roundToInt()) to anim
                launch {
                    anim.animateTo(imageSizeDp.value, tween(2000, easing = EaseOutBounce))
                    charMap.remove(it)
                }
            }
        }
        while (true){
            mascotState.speak(texts.random(),5000)
        }
    }

    //val charList= remember() { mutableStateListOf<Char>() }
    LaunchedEffect(mascotEventType) {
        when (val eventType = mascotEventType) {
            MascotEventType.Explosion -> TODO()//コンパイルエラー
            MascotEventType.Fall -> TODO()//ランダム
            MascotEventType.Gaming -> {
                gifName="upleft.gif"
                while(true){
                    var random = Color(colorList.random())
                    while (colorState == random) {
                        random = Color(colorList.random())
                    }
                    color.animateTo(random, tween(160, easing = EaseInBack))
                }
            }
            MascotEventType.None -> {
                gifName = "SUC.webp"
            }

            MascotEventType.Run -> {
                //TODO アニメーション書いてちょ

                while (true) {
                    val x = (Random.nextFloat() * ScreenSize.widthDp * 0.8)
                    val y = (Random.nextFloat() * ScreenSize.heightDp * 0.8)

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
        mascotState.change(MascotEventType.None)
    }
    //Windowを表示
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        resizable = false,
        transparent = true,
        undecorated = true,
        alwaysOnTop = false//TODO デバッグ用に切った 本番時 true
    ) {

        Box(modifier = Modifier) {
            //SUCちゃん
            Image(loadOrNull(gifName) {
                loadResourceAnimatedImage(gifName)
            }
                ?.animate() ?: ImageBitmap.Blank,
                null,
                Modifier.size(imageSizeDp),colorFilter = ColorFilter.tint(colorState, BlendMode.Modulate),
            )
            val serif by mascotState.serifFlow.collectAsState()
            if (serif!=null) {
                Box(
                    modifier = Modifier.padding(start = 150.dp, top = 50.dp).width(150.dp)
                ) {
                    Text(
                        serif?:"", modifier = Modifier
                            .background(
                                color = Color(0xff5ff4ac),
                                shape = RoundedCornerShape(30)
                            )
                            .padding(10.dp)
                    )
                }
            }else Spacer(Modifier.width(300.dp))
            charMap.forEach { (c, a) ->
                val anim by a.second.asState()
                Text(c.toString(), Modifier.offset(x = a.first.dp, y = anim.dp))
            }
        }
    }

    var statisticsWindow by remember { mutableStateOf(true) }
    Tray(painterResource("SUC.png")) {
        Item("統計") {
            statisticsWindow = true
        }
        Item("閉じる") {
            exitApplication()
        }
    }
    ControlWindow(statisticsWindow) { statisticsWindow = false }
}

val imageSizeDp: Dp = 175.dp
val simpleDataset = mapOf(
    "言語" to listOf(0, 1, 2, 4, 5, 7, 8, 9).map { "Kt$it" },
    "時間" to listOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
    "humidity" to listOf(0.5, 0.32, 0.11, 0.89, 0.68, 0.57, 0.56, 0.5)
)

fun Random.Default.nextSign() = if (nextBoolean()) 1 else -1
