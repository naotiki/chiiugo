package me.naotiki.chiiugo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    /*configureWebResources {
        resourcePathMapping { path -> "./$path" }
    }*/
    CanvasBasedWindow("ちぃうご", canvasElementId = "ComposeTarget") {
        var areaSizePx by remember { mutableStateOf(IntSize.Zero) }
        val areaSizeDp = with(LocalDensity.current) {
            areaSizePx.run { width.toDp() to height.toDp() }
        }
        val areaSize = rememberAreaSize(0.dp to 0.dp)
        LaunchedEffect(areaSizeDp) {
            areaSize.screenSizeDp = areaSizeDp
        }
        val mascotState = rememberMascotState(areaSize)
        Box(Modifier.fillMaxSize()
            .onSizeChanged {
                areaSizePx = it
            }) {
            var mascotPos by remember { mutableStateOf(AreaPosition.Zero) }
            LaunchedEffect(mascotState.areaPosState) {
                mascotPos=mascotState.areaPosState
            }
            Mascot(mascotState,ConfigManager.conf,Modifier.offset(mascotPos.x, mascotPos.y))
        }
    }
}