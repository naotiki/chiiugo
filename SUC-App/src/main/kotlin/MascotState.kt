import MascotEventType.Feed
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

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

    object DVD : MascotEventType

    //餌食うよ
    data class Feed(val char: Char) : MascotEventType
}

class MascotState(mascotEventType: MascotEventType) {
    private val stateFlow = MutableStateFlow(mascotEventType)
    val flow get() = stateFlow.asStateFlow()
    private val server = Server()
    suspend fun initServer() {
        server.onEventReceive {
            runBlocking {
                when (val e = it.event) {
                    is Event.FailedBuild -> TODO()
                    is Event.OpenProject -> {}
                    is Event.StartBuild -> TODO()
                    is Event.SuccessBuild -> TODO()
                    is Event.Typed -> {
                        change(Feed(e.char))
                    }
                }
            }

        }
        server.runServer()

    }
    private var previousEventType by mutableStateOf<MascotEventType>(MascotEventType.None)
    suspend fun change(state: MascotEventType) {
        previousEventType=stateFlow.value
        stateFlow.emit(state)
    }
    suspend fun recoverEvent(){
        stateFlow.emit(previousEventType)
    }
}

@Composable
fun rememberMascotState(initialMascotEventType: MascotEventType = MascotEventType.None) =
    remember { MascotState(initialMascotEventType) }