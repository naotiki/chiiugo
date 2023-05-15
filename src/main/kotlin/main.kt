import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val mascotState=rememberMascotState()
    val mascotEventType by mascotState.flow.collectAsState()
                    //       ↓MascotEventTypeが変更されたら初期値 SUC.gifに
    var gifName by remember(mascotEventType) { mutableStateOf("SUC.png") }

    LaunchedEffect(mascotEventType){
        when(mascotEventType){
            MascotEventType.Explosion -> TODO()
            MascotEventType.Fall -> TODO()
            is MascotEventType.Feed -> TODO()
            MascotEventType.Gaming -> TODO()
            MascotEventType.None -> TODO()
            MascotEventType.Run -> {
                gifName="SUC.gif"
                //アニメーション書いてちょ
                /*
                repeat(200){
                    animatedWindowPosition.animateTo()
                }
                */
            }
            MascotEventType.Speak -> TODO()
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

        Box(modifier = Modifier.size(200.dp)) {
            //SUCちゃん
            Image(loadOrNull { loadResourceAnimatedImage(gifName).apply {
                println(this.codec.frameCount)
            } }?.animate() ?: ImageBitmap.Blank, null, Modifier.fillMaxSize())

        }
    }
}


