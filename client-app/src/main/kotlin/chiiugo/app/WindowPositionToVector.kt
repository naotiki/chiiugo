package chiiugo.app

import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition

//ウィンドウの位置をアニメーション用の値に変換する
val WindowPositionToVector: TwoWayConverter<WindowPosition.Absolute, AnimationVector2D> = TwoWayConverter({
    //変換
    AnimationVector2D(it.x.value, it.y.value)
}, {
    //復元
    WindowPosition(it.v1.dp, it.v2.dp)
})