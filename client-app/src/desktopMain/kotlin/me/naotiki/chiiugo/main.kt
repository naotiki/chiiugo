package me.naotiki.chiiugo

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import client_app.generated.resources.Res
import client_app.generated.resources.SUCIcon
import kotlinx.coroutines.launch
import me.naotiki.chiiugo.data.DatabaseFactory
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.awt.GraphicsEnvironment
import java.util.logging.LogManager
import kotlin.random.Random
import kotlin.system.exitProcess


val server by lazy { SocketServer() }
private val screenPx =
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration.bounds.run {
        width to height
    }

fun main() {
    val graphicsData = ConfigManager.conf.graphics
    System.setProperty("skiko.renderApi", graphicsData.renderApi)
    System.setProperty("skiko.fps.enabled", graphicsData.fps.toString())
    System.setProperty("skiko.vsync.enabled", graphicsData.vsync.toString())
    System.setProperty("skiko.gpu.priority", graphicsData.gpu.value)
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
        val areaSize = rememberAreaSize(with(LocalDensity.current) {
            screenPx.run {
                first.toDp() to second.toDp()
            }
        })
        //Windowを表示
        repeat(configState.spawnCount) {
            MascotWrapper(areaSize, configState)
        }
        var controlWindowTab by remember { mutableStateOf<Int?>(null) }
        /*Bitmap Only*/
        Tray(
            painterResource(
                Res.drawable.SUCIcon
            )
        ) {
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
        DialogWindow({ exitCount = 0 }, dialogState, visible = exitCount != 0, title = "確認", onKeyEvent = {
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
                                        1-Random.nextFloat()*2,
                                        1-Random.nextFloat()*2
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
    GlobalScreen.addNativeKeyListener(object : NativeKeyListener {
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
