package me.naotiki.chiiugo.utils

import androidx.compose.runtime.*

@Composable
fun <T> loadOrNull(key:Any?,action: suspend () -> T?): T? {
    val scope = rememberCoroutineScope()
    var result: T? by remember(key) { mutableStateOf(null) }
    LaunchedEffect(key) {
        result = action()
    }
    return result
}