package me.naotiki.chiiugo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import client_app.generated.resources.NotoSansJP
import client_app.generated.resources.Res
import kotlinx.coroutines.launch
import me.naotiki.chiiugo.utils.Blank
import me.naotiki.chiiugo.utils.animate
import me.naotiki.chiiugo.utils.loadOrNull
import me.naotiki.chiiugo.utils.loadResourceAnimatedImage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font


private val forceClose = arrayOf(Key.R, Key.E, Key.I, Key.S, Key.U, Key.B)

@OptIn(ExperimentalResourceApi::class)
val font @Composable get() = FontFamily(Font(Res.font.NotoSansJP))

@Composable
fun Mascot(mascotState: MascotState, configData: ConfigData, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) {
        launch {
            mascotState.loop()
        }
    }
    Box(modifier = modifier) {
        //SUCちゃん
        Image(
            loadOrNull(mascotState.gifName) {
                loadResourceAnimatedImage("files/" + mascotState.gifName)
            }?.animate() ?: ImageBitmap.Blank,
            null,
            Modifier.size(configData.imageSize.dp),
            colorFilter = ColorFilter.tint(mascotState.colorState, BlendMode.Modulate),
        )
        val serif by mascotState.serifFlow.collectAsState()

        if (serif != null) {
            Box(
                modifier = Modifier.padding(
                    start = configData.imageSize.dp - (configData.imageSize.dp * 0.05f),
                    top = (configData.imageSize.dp * 0.05f)
                ).width(150.dp)
            ) {


                Text(
                    serif ?: "",
                    modifier = Modifier
                        .background(
                            color = Color(0xff5ff4ac),
                            shape = RoundedCornerShape(30)
                        )
                        .padding(10.dp), fontFamily = font
                )
            }
        }

        mascotState.charMap.forEach { (c, a) ->
            val anim by a.second.asState()
            Text(c.toString(), Modifier.offset(x = a.first.dp, y = anim.dp), color = Color.Red, fontFamily = font)
        }
    }
}