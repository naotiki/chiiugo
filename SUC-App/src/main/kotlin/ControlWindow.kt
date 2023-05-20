import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import org.jetbrains.compose.animatedimage.Blank
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.invoke
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layout
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.letsplot.y

val startTime= Clock.System.now()
@Composable
fun ControlWindow(visible: Boolean = true, serverState: ServerState, onCloseRequest: () -> Unit){
    Window(onCloseRequest = onCloseRequest, visible = visible) {
        Surface {
            var weekGraphImage by remember { mutableStateOf(ImageBitmap.Blank) }
            var langGraphImage by remember { mutableStateOf(ImageBitmap.Blank) }
            LaunchedEffect(Unit) {
                serverState.server.onEventReceive {
                    when(it.event){

                        else->{}
                    }
                }
                /*
                weekGraphImage=
                    plot(simpleDataset) {

                    x("日"<String>()) {

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
                    }
                }.toBufferedImage().toComposeImageBitmap()*/
                langGraphImage = plot(simpleDataset) {

                    x("言語"<String>()) {
                    }

                    y("時間"<Double>()) {

                        scale = continuous(0.0..25.5)
                    }

                    bars {

                        borderLine.width = 0.0
                    }
                    layout {

                        this.size = 800 to 800
                    }
                }.toBufferedImage().toComposeImageBitmap()
            }
            Column(Modifier.fillMaxSize()) {
                var selectedTabIndex by remember { mutableStateOf(0) }
                TabRow(selectedTabIndex, Modifier.weight(0.1f)) {
                    Tab(selectedTabIndex == 0, {
                        selectedTabIndex = 0
                    }) {
                        Text("統計")
                    }
                    Tab(selectedTabIndex == 1, {
                        selectedTabIndex = 1
                    }) {
                        Text("設定")
                    }
                }
                Column(Modifier.weight(0.8f)) {
                    when (selectedTabIndex) {
                        0 -> {
                            Text("累計プログラミング時間", Modifier.padding(10.dp), fontSize = 25.sp)
                            var totalTimeText by remember { mutableStateOf("00:00:00") }
                            LaunchedEffect(Unit){
                                while (true){

                                    val c= startTime.periodUntil(Clock.System.now(), TimeZone.currentSystemDefault())

                                    totalTimeText= "${c.hours}h ${c.minutes}m ${c.seconds}s"
                                    delay(1000)

                                }
                            }
                            Text(totalTimeText,Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 35.sp, fontFamily = FontFamily.Monospace)
                            Text("一週間のプログラミング時間", Modifier.padding(10.dp), fontSize = 25.sp)
                            Image(langGraphImage, null, Modifier.fillMaxWidth())
                        }

                        1 -> {
                            Row {
                                Text("")
                            }
                        }
                    }
                }
            }
        }
    }
}
val simpleDataset = mapOf(
    "言語" to listOf(0, 1, 2, 4, 5, 7, 8, 9).map { "Kt$it" },
    "時間" to listOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
)