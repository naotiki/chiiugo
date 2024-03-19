package me.naotiki.chiiugo

import androidx.compose.ui.window.WindowPosition

import me.naotiki.chiiugo.AreaPosition

fun WindowPosition.toAreaPositionOrNull(): AreaPosition? {
    if (this !is WindowPosition.Absolute) return null
    return AreaPosition(x, y)
}

fun AreaPosition.toWindowPosition(): WindowPosition {
    return WindowPosition(x, y)
}

