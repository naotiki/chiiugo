package me.naotiki.chiiugo

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.onClick
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MascotWrapper(areaSize: AreaSize, configData: ConfigData) {
    val density = LocalDensity.current.density
    val mascotState = rememberMascotState(areaSize)
    val windowState =
        rememberWindowState(size = DpSize.Unspecified, position = mascotState.areaPosState.toWindowPosition())
    LaunchedEffect(Unit) {
        launch {
            server.onEventReceive { e, _ ->
                println("Event Receive:$e")
                when (e) {
                    is Event.FailedBuild -> {
                        mascotState.changeBehaviour {
                            gifName = "boom.gif"
                            say("ビルド ${e.buildId} 失敗！", 5000, true)?.join()
                        }
                    }

                    is Event.OpenProject -> {
                        mascotState.say("プロジェクト ${e.projectName} を開きました！", 5000, true)
                    }

                    is Event.StartBuild -> {
                        mascotState.say("ビルド ${e.buildId} を実行中", 5000, true)
                    }

                    is Event.SuccessBuild -> {
                        mascotState.say("ビルド ${e.buildId} 成功！", 5000, true)
                    }

                    is Event.Typed -> {
                        launch {
                            mascotState.feed(e.char)
                        }
                    }

                    else -> {}
                }
            }
        }
    }
    LaunchedEffect(configData.imageSize) {
        windowState.size = DpSize.Unspecified
    }
    //画面スケール変更時
    val size = remember(windowState.size.isSpecified) { windowState.size }
    LaunchedEffect(windowState.size) {
        if (windowState.size != size)
            windowState.size = DpSize.Unspecified
    }

    //Window位置を更新
    LaunchedEffect(mascotState.areaPosState) {
        windowState.position = mascotState.areaPosState.toWindowPosition()
    }

    var cacheSize: IntSize? by remember { mutableStateOf(null) }
    Window(
        onCloseRequest = {},
        state = windowState,
        focusable = false,
        resizable = false,
        transparent = true,
        undecorated = true,
        alwaysOnTop = configData.alwaysTop,
    ) {
        val coroutine = rememberCoroutineScope()
        WindowDraggableArea(Modifier.onDrag(onDragStart = {
            mascotState.suspendMovement()
        }, onDragEnd = {
            coroutine.launch {
                mascotState.restartMovement(windowState.position.toAreaPositionOrNull())
            }
        }) {}.onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary), onDoubleClick = {
            if (mascotState.lockMovement) {
                coroutine.launch {
                    mascotState.restartMovement(windowState.position.toAreaPositionOrNull())
                }
            } else {
                mascotState.suspendMovement()
            }
        }) {}) {
            Mascot(mascotState, configData, modifier = Modifier.wrapContentSize(unbounded = true).onSizeChanged {
                if (cacheSize == it) {
                    return@onSizeChanged
                }
                windowState.size = DpSize.Unspecified
                cacheSize = it
            })
        }
    }
}

