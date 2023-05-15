import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface MascotEventType {
    //なにもないよ
    object None : MascotEventType
    object Run : MascotEventType
    object Speak : MascotEventType
    object Gaming : MascotEventType

    //転ぶ
    object Fall : MascotEventType
    //爆発
    object Explosion : MascotEventType
    //餌食うよ
    data class Feed(val char: Char) : MascotEventType
}

class MascotState(mascotEventType: MascotEventType) {
    private val stateFlow = MutableStateFlow(mascotEventType)
    val flow get() = stateFlow.asStateFlow()

    suspend fun change(state: MascotEventType) {
        stateFlow.emit(state)
    }
}

@Composable
fun rememberMascotState(initialMascotEventType: MascotEventType=MascotEventType.None) = remember { MascotState(initialMascotEventType) }