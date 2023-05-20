import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.skia.ColorFilter
import kotlin.random.Random
var x = 0
var y = 0
enum class AnimationType(val action: suspend WindowAnimatable.() -> Unit) {
    Test({

        while(true) {
            x = (Random.nextFloat() * ScreenSize.widthDp.value).toInt()
            y = (Random.nextFloat() * ScreenSize.heightDp.value).toInt()
            animateTo(
                this.value.copy(
                    x.dp,
                    y.dp
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
            delay(500)
        }
    }),

    Direction({

    })
}