import androidx.compose.animation.core.Animatable
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {

    MaterialTheme {
        Box(modifier = Modifier.border(2.dp, Color.Black).size(200.dp)) {
            Image(painterResource("ktlnrner.png"), null, Modifier.fillMaxSize())
        }
    }
}

fun main() = application {
    //スクリーンの情報を更新
    ScreenSize.density = LocalDensity.current.density

    //Windowサイズや位置の情報が入っている
    val windowState = rememberWindowState(size = DpSize.Unspecified, position = WindowPosition(0.dp, 0.dp))

    //アニメーション用の位置
    val animatedWindowPosition = remember() { Animatable(windowState.position as WindowPosition.Absolute,WindowPositionToVector) }

    LaunchedEffect(Unit) {
        delay(1000)
        //1sec後にAnimationType.Testを実行
        AnimationManager.animate(animatedWindowPosition, AnimationType.Test)

    }
    val statePos by animatedWindowPosition.asState()
    LaunchedEffect(statePos) {
        windowState.position = statePos
    }
    //Windowを表示
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        resizable = false,
        transparent = true,
        undecorated = true
    ) {

        WindowDraggableArea {
            //App関数を表示
            App()
        }
    }
}


