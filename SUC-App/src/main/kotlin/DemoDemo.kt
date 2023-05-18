import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

private fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize.Unspecified,
    )
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        resizable = false,
        transparent = true,
        undecorated = true,
        alwaysOnTop = true
    ) {
        Box(Modifier.wrapContentWidth()) {
            Box(modifier = Modifier.size(175.dp).background(Color.Red))
            var ran by remember { mutableStateOf("0") }
            LaunchedEffect(Unit) {
                ran = "999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999"

            }
            Box(
                Modifier
                    .padding(start = 150.dp)
                    .width(100.dp)
            ) {

                Text(
                    ran,
                    Modifier
                        .background(
                            color = Color(0xff5ff4ac),
                            shape = RoundedCornerShape(30)
                        ).padding(10.dp)
                       ,
                    maxLines = 5, overflow = TextOverflow.Visible,
                )
            }

        }
    }

}