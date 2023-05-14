import Kanjyou.*
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

enum class Kanjyou {
    None,
    Happy,
    Boom,
    Angry,
}

enum class Direction(val gifName:String) {
    UpLeft(""),
    UpRight(""),
    Left(""),
    Right(""),
    DownLeft(""),
    DownRight(""),


}

class MascotState {
    private val stateFlow = MutableStateFlow(None)


    val flow get() = stateFlow.asStateFlow()


    var fileName by mutableStateOf("ktlnrner.png")


    suspend fun change(state: Kanjyou) {
        stateFlow.emit(state)
    }

}

@Composable
fun rememberMascotState() = remember { MascotState() }

@Composable
private fun Appp(mascotState: MascotState) {
    val kanjyou by mascotState.flow.collectAsState()
    val color = remember(kanjyou) { Animatable(Color.Black/*初期の色*/) }
    val colorState by color.asState()//Stateに変換 ok
    LaunchedEffect(kanjyou) {
        when (kanjyou) {
            Angry -> {
                color.animateTo(listOf(Color.Red,Color.Green,Color.Blue,Color.Yellow).random(), tween(250, easing = LinearEasing))
            }

            Happy -> {
                color.animateTo(Color.Red)
            }

            else -> {}
        }
    }
    MaterialTheme {
        Column(modifier = Modifier.border(2.dp, Color.Black).size(500.dp)) {
            Text(kanjyou.name, fontSize = 50.sp)
            Image(
                painterResource(mascotState.fileName), null, Modifier.fillMaxSize().clickable {
                    runBlocking {
                        mascotState.change(Angry)
                    }
                },
                colorFilter = ColorFilter.tint(
                    colorState,
                    BlendMode.Multiply
                )
            )
        }
    }
}

private fun main() = application {
    val mascotState = rememberMascotState()
    val mascot by mascotState.flow.collectAsState()

    //Windowサイズや位置の情報が入っている
    val windowState = rememberWindowState(
        size = DpSize.Unspecified, position = WindowPosition.Absolute(0.dp, 0.dp)
    )

    //アニメーション用の位置
    val animatedWindowPosition =
        remember() { Animatable(windowState.position as WindowPosition.Absolute, WindowPositionToVector) }
    val windowPos by animatedWindowPosition.asState()
    LaunchedEffect(Unit) {
        while (true) {
            mascotState.change(Kanjyou.values().random())
            delay(1000)
        }
    }
    LaunchedEffect(windowPos) {
        windowState.position = windowPos
    }
    LaunchedEffect(mascot) {
        when (mascot) {
            None -> {

            }

            Happy -> {
                mascotState.fileName=Direction.DownLeft.gifName
                repeat(200){
                    animatedWindowPosition.animateTo(

                        animatedWindowPosition.value.copy(
                            x = (Random.nextFloat() * ScreenSize.widthDp).dp,
                            y = (Random.nextFloat() * ScreenSize.heightDp).dp
                        ),
                        tween(
                            10000, easing = androidx.compose.animation.core.EaseInOutBounce
                        )
                    )
                    delay(500)
                }
            }

            Boom -> {

            }

            Angry -> {

            }

        }

    }
    Window(onCloseRequest = this::exitApplication, state = windowState) {
        Appp(mascotState)
    }
}
