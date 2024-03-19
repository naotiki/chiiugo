package me.naotiki.chiiugo

import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AreaPosition(
    val x: Dp, val y: Dp
) {

    companion object {
        val Zero = AreaPosition(0.dp, 0.dp)
        val converter: TwoWayConverter<AreaPosition, AnimationVector2D> = TwoWayConverter({
            //変換
            AnimationVector2D(it.x.value, it.y.value)
        }, {
            //復元
            AreaPosition(it.v1.dp, it.v2.dp)
        })
    }
}
