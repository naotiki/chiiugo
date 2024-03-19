package me.naotiki.chiiugo

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class AreaSize(density: Density, initScreenSize: Pair<Dp, Dp>?) {
    var density: Float by mutableStateOf(density.density)
    var screenSizeDp: Pair<Dp, Dp> by mutableStateOf(initScreenSize ?: (Dp.Unspecified to Dp.Unspecified))
    val widthDp by derivedStateOf { screenSizeDp.first }
    val heightDp by derivedStateOf { screenSizeDp.second }
    val aspectRatio get() = widthDp / heightDp
    fun Float.coerceInWidth() = coerceIn(0f, widthDp.value)
    fun Float.coerceInHeight() = coerceIn(0f, heightDp.value)
    fun Int.toDp() = div(density).dp
    fun toDp(float: Float) = float.div(density).dp
    fun toPx(dp: Dp) = dp.value.times(density)
}

@Composable
fun rememberAreaSize(screenSizeDp: Pair<Dp, Dp>?): AreaSize {
    val density = LocalDensity.current
    return remember(density) { AreaSize(density, screenSizeDp) }
}