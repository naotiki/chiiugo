package chiiugo.app

import SocketServer
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import chiiugo.app.data.DatabaseFactory
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.util.logging.LogManager
import kotlin.random.Random
import kotlin.system.exitProcess


val colorList =
    listOf(0xFFFFFF00, 0xFF00FF00, 0xFFFF0000, 0xFF0000FF, 0xFF7DEBEB, 0xFFFF9B00, 0xFF800080, 0xFFFF1493)
val server by lazy { SocketServer() }

@OptIn(ExperimentalResourceApi::class, ExperimentalComposeUiApi::class)
fun main() {
    val graphicsData= ConfigManager.conf.graphics
    System.setProperty("skiko.renderApi",graphicsData.renderApi)
    System.setProperty("skiko.fps.enabled",graphicsData.fps.toString())
    System.setProperty("skiko.vsync.enabled",graphicsData.vsync.toString())
    System.setProperty("skiko.gpu.priority",graphicsData.gpu.value)
    //setupGlobalListener()
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
        val screenSize= rememberScreenSize()
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

fun setupGlobalListener() {
    LogManager.getLogManager().reset();
    if (!GlobalScreen.isNativeHookRegistered()) {
        try {
            //フックを登録
            GlobalScreen.registerNativeHook()
        } catch (e: NativeHookException) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }
    GlobalScreen.addNativeKeyListener(object :NativeKeyListener{
        override fun nativeKeyTyped(p0: NativeKeyEvent) {
            println(p0.paramString())
        }

        override fun nativeKeyPressed(p0: NativeKeyEvent?) {
        }

        override fun nativeKeyReleased(p0: NativeKeyEvent?) {
        }
    })
}


@Suppress("unused")//DVD機能で使うかも
fun Random.nextSign() = if (nextBoolean()) 1 else -1
