import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@Composable
@Preview
fun App() {

    MaterialTheme {
        Box(modifier = Modifier.border(2.dp, Color.Black).size(200.dp)) {
            Image(painterResource("ktlnrner.png"),null,Modifier.fillMaxSize())
        }
    }
}

fun main() = application {
    //Windowサイズを自動に
    val window = rememberWindowState(size = DpSize.Unspecified)
    //Windowを表示
    Window(onCloseRequest = ::exitApplication, state = window, resizable = false, transparent = true, undecorated = true) {
        WindowDraggableArea {
            //App関数を表示
            App()
        }
    }
}
