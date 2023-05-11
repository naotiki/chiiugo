import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class AnimationType(val action:suspend WindowAnimatable.()->Unit){
    Test({

        animateTo(
            this.value.copy((Random.nextFloat() * ScreenSize.widthDp).dp,), tween(500)
        )
        delay(500)
    })
}