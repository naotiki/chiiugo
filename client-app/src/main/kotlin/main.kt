import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.DatabaseFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.GpuPriority
import org.jetbrains.skiko.GraphicsApi
import kotlin.random.Random

val colorList =
    listOf(0xFFFFFF00, 0xFF00FF00, 0xFFFF0000, 0xFF0000FF, 0xFF7DEBEB, 0xFFFF9B00, 0xFF800080, 0xFFFF1493)
val server by lazy { SocketServer() }

@OptIn(ExperimentalResourceApi::class, ExperimentalComposeUiApi::class)
fun main() {
    val graphicsData=ConfigManager.conf.graphics
    System.setProperty("skiko.renderApi",graphicsData.renderApi)
    System.setProperty("skiko.fps.enabled",graphicsData.fps.toString())
    System.setProperty("skiko.vsync.enabled",graphicsData.vsync.toString())
    System.setProperty("skiko.gpu.priority",graphicsData.gpu.value)
    application {
        val coroutineScope = rememberCoroutineScope()
        val configState by ConfigManager.configStateFlow.collectAsState()
        DisposableEffect(Unit) {
            DatabaseFactory.init()
            coroutineScope.launch { server.runServer() }
            onDispose {
                server.stop()
            }
        }

        var exitCount by remember { mutableStateOf(0) }
        val screenSize=rememberScreenSize()
        //Windowを表示
        repeat(configState.spawnCount){
            Mascot(screenSize,configState)
        }
        var controlWindowTab by remember { mutableStateOf<Int?>(null) }
        /*Bitmap Only*/
        Tray(painterResource("SUCIcon.png")) {
            Item("設定") {
                controlWindowTab = 1
            }
            Item("統計") {
                controlWindowTab = 0
            }
            Item("閉じる") {
                exitCount = 5
            }
        }
        val dialogState = rememberDialogState()
        Dialog({ exitCount = 0 }, dialogState, visible = exitCount != 0, title = "確認", onKeyEvent = {
            if (it.isCtrlPressed && it.isAltPressed && it.isShiftPressed && it.key == Key.Q) {
                exitApplication()
            }
            false
        }) {
            Box(Modifier.fillMaxSize().padding(15.dp, 5.dp)) {
                Column {
                    Text("本当に閉じますか？")
                    Text("(あと $exitCount 回)")
                }
                ProvideLayoutDirection(layoutDirection = if (exitCount == 1) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    Row(Modifier.align(Alignment.BottomEnd), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Button({ exitCount = 0 }) {
                            Text("No")
                        }
                        Button({
                            if (--exitCount <= 0) {
                                exitApplication()
                            } else {
                                dialogState.position = WindowPosition(
                                    BiasAlignment(
                                        Random.nextInt(-1, 1).toFloat(),
                                        Random.nextInt(-1, 1).toFloat()
                                    )
                                )
                            }
                        }) {
                            Text("Yes")
                        }

                    }
                }
            }
        }

        ControlWindow(controlWindowTab != null, { controlWindowTab = null }, controlWindowTab ?: 0)
    }
}



@Suppress("unused")//DVD機能で使うかも
fun Random.nextSign() = if (nextBoolean()) 1 else -1
