package me.naotiki.chiiugo

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.GraphicsEnvironment
import java.awt.Rectangle

class ScreenSize(density: Density){
    private val graphicEnv= GraphicsEnvironment.getLocalGraphicsEnvironment()
    var density:Float by mutableStateOf(density.density)

    private val screenSizePx: Rectangle = graphicEnv.defaultScreenDevice.defaultConfiguration.bounds
    val widthDp by derivedStateOf { screenSizePx.width.toDp() }
    val heightDp by derivedStateOf { screenSizePx.height.toDp() }
    val aspectRatio get() = widthDp/ heightDp
    fun Float.coerceInWidth()=coerceIn(0f, widthDp.value)
    fun Float.coerceInHeight()=coerceIn(0f, heightDp.value)
    fun Int.toDp() = div(density).dp
    fun toDp(float:Float) = float.div(density).dp
    fun toPx(dp:Dp) = dp.value.times(density)
}
@Composable
fun rememberScreenSize(): ScreenSize {
    val density = LocalDensity.current

    return remember(density) { ScreenSize(density) }
}
