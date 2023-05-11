import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import kotlinx.coroutines.delay
import java.awt.Toolkit
import kotlin.random.Random

typealias WindowAnimatable=Animatable<WindowPosition.Absolute, AnimationVector2D>

object AnimationManager {
    suspend fun animate(animatedWindowPosition: WindowAnimatable,type: AnimationType) {
        animatedWindowPosition.value.x
        type.action(animatedWindowPosition)
    }
}

object ScreenSize{
    var density:Float=1f

    val screenSizePx = Toolkit.getDefaultToolkit().screenSize
    val widthDp get() = screenSizePx.width.toDp()
    val heightDp get() = screenSizePx.height.toDp()
    fun Float.coerceInWidth()=coerceIn(0f, widthDp)
    fun Float.coerceInHeight()=coerceIn(0f, heightDp)
    fun Int.toDp() = div(density)
}
