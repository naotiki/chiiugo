import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class AnimationType(val action: suspend WindowAnimatable.() -> Unit) {
    Test({

        kotlin.repeat(200){
            animateTo(
                this.value.copy(
                    x = (Random.nextFloat() * ScreenSize.widthDp).dp,
                    y = (Random.nextFloat() * ScreenSize.heightDp).dp
                ),
                tween(
                    5000, easing = androidx.compose.animation.core.EaseInOut
                )
            )
            delay(500)
        }
    })
}