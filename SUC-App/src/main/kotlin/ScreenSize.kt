import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Toolkit

object ScreenSize{
    var density:Float=1f

    val screenSizePx = Toolkit.getDefaultToolkit().screenSize
    val widthDp get() = screenSizePx.width.toDp()
    val heightDp get() = screenSizePx.height.toDp()
    val aspectRatio get() = widthDp/ heightDp
    fun Float.coerceInWidth()=coerceIn(0f, widthDp.value)
    fun Float.coerceInHeight()=coerceIn(0f, heightDp.value)
    fun Int.toDp() = div(density).dp
    fun Float.toDp() = div(density).dp
    fun Dp.toPx() = value.times(density)
}
