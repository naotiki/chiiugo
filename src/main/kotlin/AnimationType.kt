import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.skia.ColorFilter
import kotlin.random.Random

enum class AnimationType(val action: suspend WindowAnimatable.() -> Unit) {
    Test({

        kotlin.repeat(200) {
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
            val range = (0..2).random()
            if (range == 0){
                kotlin.io.println("test")
            }else if(range == 1){
                kotlin.io.println("test2")
            }else{
                kotlin.io.println("test3")
            }//静止した後ランダムで(行動)or(何もしない)をする
        }
    })
}