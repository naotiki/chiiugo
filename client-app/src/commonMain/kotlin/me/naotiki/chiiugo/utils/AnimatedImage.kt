package me.naotiki.chiiugo.utils
/*
 * https://github.com/JetBrains/compose-multiplatform/tree/master/components/AnimatedImage/library
 *
 */

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import client_app.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.skia.AnimationFrameInfo
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data

class AnimatedImage(val codec: Codec)

@OptIn(ExperimentalResourceApi::class)
suspend fun loadResourceAnimatedImage(path: String): AnimatedImage {
    val byteArray = Res.readBytes(path)
    return withContext(Dispatchers.Default) {
        val data = Data.makeFromBytes(byteArray)
        val codec = Codec.makeFromData(data)

        return@withContext AnimatedImage(codec)
    }
}


@Composable
fun AnimatedImage.animate(): ImageBitmap {
    when (codec.frameCount) {
        0 -> return ImageBitmap.Blank // No frames at all
        1 -> {
            // Just one frame, no animation
            val bitmap = remember(codec) { Bitmap().apply { allocPixels(codec.imageInfo) } }
            remember(bitmap) {
                codec.readPixels(bitmap, 0)
            }
            return bitmap.asComposeImageBitmap()
        }

        else -> {
            val transition = rememberInfiniteTransition()
            val frameIndex by transition.animateValue(
                initialValue = 0,
                targetValue = codec.frameCount - 1,
                Int.VectorConverter,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 0
                        for ((index, frame) in codec.framesInfo.withIndex()) {
                            index at durationMillis
                            val frameDuration = calcFrameDuration(frame)

                            durationMillis += frameDuration
                        }
                    }
                )
            )

            val bitmap = remember(codec) { Bitmap().apply { allocPixels(codec.imageInfo) } }

            remember(bitmap, frameIndex) {
                codec.readPixels(bitmap, frameIndex)
            }

            return bitmap.asComposeImageBitmap()
        }
    }
}

private const val DEFAULT_FRAME_DURATION = 100

private fun calcFrameDuration(frame: AnimationFrameInfo): Int {
    // If the frame does not contain information about a duration, set a reasonable constant duration
    val frameDuration = frame.duration
    return if (frameDuration == 0) DEFAULT_FRAME_DURATION else frameDuration
}

private val BlankBitmap = ImageBitmap(1, 1)

/**
 * Object used to represent a blank ImageBitmap with the minimum possible size.
 */
val ImageBitmap.Companion.Blank get() = BlankBitmap