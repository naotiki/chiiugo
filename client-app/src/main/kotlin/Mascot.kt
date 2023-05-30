
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
import org.jetbrains.compose.animatedimage.Blank
import org.jetbrains.compose.animatedimage.animate
import org.jetbrains.compose.animatedimage.loadResourceAnimatedImage
import org.jetbrains.compose.resources.loadOrNull
import org.jetbrains.kotlinx.kandy.dsl.invoke

@Composable
fun Mascot(screenSize: ScreenSize,configData: ConfigData){
    val mascotState = rememberMascotState(screenSize)
    val windowState = rememberWindowState(size = DpSize.Unspecified, position =mascotState.windowsPosState)
    LaunchedEffect(Unit){
        launch {
            mascotState.loop()
        }
    }
    LaunchedEffect(configData.imageSize){
        windowState.size=DpSize.Unspecified
    }
    //画面スケール変更時
    val size= remember(windowState.size.isSpecified) { windowState.size }
    LaunchedEffect(windowState.size){
        if (windowState.size!=size)
            windowState.size= DpSize.Unspecified
    }


    //Window位置を更新
    LaunchedEffect(mascotState.windowsPosState) {
        windowState.position = mascotState.windowsPosState
    }


    Window(
        onCloseRequest = {},
        state = windowState,
        focusable = false,
        resizable = false,
        transparent = true,
        undecorated = true,
        alwaysOnTop = configData.alwaysTop
    ) {

        Box(modifier = Modifier) {
            //SUCちゃん
            Image(
                loadOrNull(mascotState.gifName) {
                    loadResourceAnimatedImage(mascotState.gifName)
                }?.animate() ?: ImageBitmap.Blank,
                null,
                Modifier.size(configData.imageSize.dp), colorFilter = ColorFilter.tint(mascotState.colorState, BlendMode.Modulate),
            )
            val serif by mascotState.serifFlow.collectAsState()

            Box(
                modifier = Modifier.padding(start = configData.imageSize.dp-(configData.imageSize.dp*0.05f), top = (configData.imageSize.dp*0.05f)).width(150.dp)
            ) {
                if (serif != null) {
                    Text(
                        serif ?: "",
                        modifier = Modifier
                            .background(
                                color = Color(0xff5ff4ac),
                                shape = RoundedCornerShape(30)
                            )
                            .padding(10.dp)
                    )
                }
            }

            mascotState.charMap.forEach { (c, a) ->
                val anim by a.second.asState()
                Text(c.toString(), Modifier.offset(x = a.first.dp, y = anim.dp), color = Color.Red)
            }
        }
    }
}