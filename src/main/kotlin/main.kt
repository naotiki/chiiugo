import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay
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
import kotlin.random.Random
import org.jetbrains.kotlinx.kandy.util.color.Color.Companion as KandyColor


@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    //1dp あたりのpx数を取得 remember いらない
    ScreenSize.density = LocalDensity.current.density
    //Windowサイズや位置の情報が入っている
    var winX = remember { Random.nextFloat() * ScreenSize.widthDp }
    var winY = remember { Random.nextFloat() * ScreenSize.heightDp }
    val windowState = rememberWindowState(size = DpSize.Unspecified, position = WindowPosition(winX.dp, winY.dp))
    //アニメーション用の位置
    val animatedWindowPosition = remember { Animatable(windowState.position as WindowPosition.Absolute,WindowPositionToVector) }
    val statePos by animatedWindowPosition.asState()
    //Window位置を更新
    LaunchedEffect(statePos) {
        windowState.position = statePos
    }

    //SUCの状態
    val mascotState=rememberMascotState(MascotEventType.Run)
    val mascotEventType by mascotState.flow.collectAsState()
                    //       ↓MascotEventTypeが変更されたら初期値 SUC.gifに
    var gifName by remember(mascotEventType) { mutableStateOf("SUC.png") }
    var show by remember { mutableStateOf(true) }

    LaunchedEffect(mascotEventType){
        when(mascotEventType){
            MascotEventType.Explosion -> TODO()//コンパイルエラー
            MascotEventType.Fall -> TODO()//ランダム
            is MascotEventType.Feed -> TODO()//タイプ
            MascotEventType.Gaming -> TODO()//ランダム
            MascotEventType.None -> {
                gifName="stay.gif"
            }
            MascotEventType.Run -> {
                //TODO アニメーション書いてちょ

                while (true) {
                    val x = (Random.nextFloat() * ScreenSize.widthDp * 0.8)
                    val y = (Random.nextFloat() * ScreenSize.heightDp * 0.8)

                    gifName = if (x > windowState.position.x.value){
                        if(y >= windowState.position.y.value){
                            "downright.png"
                        }else{
                            "upright.png"
                        }
                    }else{
                        if(y > windowState.position.y.value){
                            "downleft.png"
                        }else{
                            "upleft.png"
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
            MascotEventType.Speak -> {
                show = true
            }
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
                Modifier.size(175.dp)
            )
            if (show) {
                Box(
                    modifier = Modifier.padding(start = 150.dp, top = 50.dp).width(150.dp)
                ) {
                    var ran by remember { mutableStateOf((0..8).random()) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            ran = (0..8).random()
                            delay(5000)
                        }
                    }
                    val text = listOf(
                        "カップルでディズニーに行くとすぐ別れるっていうよね。",
                        "もぅﾏﾁﾞ無理...コンパイルしょ...",
                        "小梅太夫「チャンチャカチャンチャンチャチャンカチャンチャン床に抜け毛が落ちていると思っていたら～～～wwwwww～～…超弦理論でした～～～(11次元宇宙を近くする小梅太夫)あああああﾁｸｼｮｵｵｵｵｵｵｵｵｵｵ超弦理論→～～～",
                        "技術的には可能です(ｷﾘｯ)",
                        "ﾄﾞﾋｭｩｩｩｩﾝシンフォギアァァァァ!!!ｷｭｷｭｷｭｷｭｲﾝ!ｷｭｷｭｷｭｷｭｲﾝ!ｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｲﾝ!\n" +
                                "ﾎﾟｫﾛﾎﾟﾎﾟﾎﾟﾎﾟﾍﾟﾍﾟﾍﾟﾍﾟﾋﾟﾋﾟﾋﾟﾋﾟﾋﾟｰﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟｰ♪",
                        "も　う　ダ　メ　ぽ",
                        "♪～",
                        "ｾｰﾝｷｮ❗\uFE0Fｾﾝｷｮ❗\uFE0Fｱｶﾙｲｾﾝｷｮｰ‼\uFE0Fｾｰﾝｷｮ❗\uFE0Fｾﾝｷｮ❗\uFE0Fｱｶﾙｲｾﾝｷｮｰ‼\uFE0F⤴\uFE0F",
                        "すごい楽しい素晴らしいソフトがあるんですよBlenderっていうんですけどね，",

                        )
                    Text(
                        text[ran], modifier = Modifier
                            .background(
                                color = Color(0xff5ff4ac),
                                shape = RoundedCornerShape(30)
                            )
                            .padding(10.dp)
                    )
            }
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
    if (statisticsWindow) {
        Window(onCloseRequest = { statisticsWindow = false }) {
            Surface {
                var graphImage by remember { mutableStateOf(ImageBitmap.Blank) }
                LaunchedEffect(Unit) {
                    graphImage = plot(simpleDataset) {

                        x("言語"<String>()) {

                        }

                        y("時間"<Double>()) {

                            scale = continuous(0.0..25.5)
                        }

                        bars {
                            fillColor("humidity"<Double>()) {
                                scale = continuous(range = KandyColor.YELLOW..KandyColor.RED)
                            }

                            borderLine.width = 0.0
                        }



                        layout {
                            this.size = 1000 to 1000
                            title = "Simple plot with lets-plot"
                            caption = "See `examples` section for more\n complicated and interesting examples!"
                        }
                    }.toBufferedImage().toComposeImageBitmap()
                }
                Column {
                    Image(graphImage, null, Modifier.fillMaxWidth())


                }
            }
        }
    }
}

val simpleDataset = mapOf(
    "言語" to listOf(0, 1, 2, 4, 5, 7, 8, 9).map { "Kt$it" },
    "時間" to listOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
    "humidity" to listOf(0.5, 0.32, 0.11, 0.89, 0.68, 0.57, 0.56, 0.5)
)
