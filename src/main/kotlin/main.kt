import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import org.jetbrains.compose.animatedimage.Blank
import org.jetbrains.compose.animatedimage.animate
import org.jetbrains.compose.animatedimage.loadResourceAnimatedImage
import org.jetbrains.compose.resources.loadOrNull
import java.awt.Color.getColor
import java.awt.Color.white
import java.awt.SystemColor.text
import kotlin.random.Random


fun main() = application {
    //1dp あたりのpx数を取得 remember いらない
    ScreenSize.density = LocalDensity.current.density
    //Windowサイズや位置の情報が入っている
    val windowState = rememberWindowState(size = DpSize.Unspecified, position = WindowPosition((Random.nextFloat() * ScreenSize.widthDp).dp, (Random.nextFloat() * ScreenSize.heightDp).dp))
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
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(mascotEventType){
        when(mascotEventType){
            MascotEventType.Explosion -> TODO()
            MascotEventType.Fall -> TODO()
            is MascotEventType.Feed -> TODO()
            MascotEventType.Gaming -> TODO()
            MascotEventType.None -> TODO()
            MascotEventType.Run -> {
                gifName="SUC.gif"
                //TODO アニメーション書いてちょ

                while(true){
                    val x = (Random.nextFloat() * ScreenSize.widthDp).toInt()
                    val y = (Random.nextFloat() * ScreenSize.heightDp).toInt()
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
        alwaysOnTop = true
    ) {

        Box(modifier = Modifier.size(600.dp)) {
            //SUCちゃん
            Image(loadOrNull { loadResourceAnimatedImage(gifName)
                .apply {
                println(this.codec.frameCount)
            } }
                ?.animate() ?: ImageBitmap.Blank,
                null,
                Modifier.size(175.dp)
                    .align(alignment = Alignment.Center)
                )
            if(show){
                Box(modifier = Modifier.width(400.dp).offset(x = 150.dp, y = 50.dp)) {
                    var ran by remember { mutableStateOf((0 .. 2).random()) }
                    LaunchedEffect(Unit){
                        while (true){
                            ran = (0..2).random()
                            delay(1000)
                        }
                    }
                    val text = listOf("カップルでディズニーに行くとすぐ別れるっていうよね。", "test", "test2")
                    Text(text[ran], modifier = Modifier
                        .height(40.dp)
                        .padding(horizontal = 50.dp)
                        .background(color = Color(0xff5ff4ac),
                            shape = RoundedCornerShape(30))
                    )
            }
            }
        }
    }
}


