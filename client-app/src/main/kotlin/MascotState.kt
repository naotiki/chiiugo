import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface MascotEventType {
    //なにもないよ
    object None : MascotEventType
    object Run : MascotEventType
    object Gaming : MascotEventType
    //転ぶ
    object Fall : MascotEventType

    //爆発
    object Explosion : MascotEventType

    object DVD : MascotEventType

    object flyingSUC : MascotEventType

}

class ServerState() {
    val server = Server()
    suspend fun initServer() {
        server.runServer()
    }
}

@Composable
fun rememberServerState() = remember { ServerState() }

class MascotState(mascotEventType: MascotEventType, serverState: ServerState) {
    private val stateFlow = MutableStateFlow(mascotEventType)
    val flow get() = stateFlow.asStateFlow()

    init {
        serverState.server.onEventReceive {e,_->
            when (e) {
                is Event.FailedBuild -> {
                    change(MascotEventType.Explosion)
                }
                is Event.OpenProject -> {
                    speak(e.projectName + "を開きました！", 5000, true)
                    //change(Speak(e.projectName))
                }
                is Event.StartBuild -> {
                    speak(e.buildId + "を実行中", 5000, true)
                }
                is Event.SuccessBuild -> {
                    speak( "ビルド成功！", 5000, true)
                }
                is Event.Typed -> {
                    feed(e.char)
                }
                else->{}
            }
        }
    }
    val stateCoroutineScope= CoroutineScope(Dispatchers.Default)
    //nullで吹き出し非表示
    private val serif = MutableStateFlow<String?>(null)
    val serifFlow = serif.asStateFlow()
    suspend fun speak(string: String, delayMillis: Long, important: Boolean = false): Job? {

        println("Say $important ${serif.value}→ $string")
        if (important) {
            serif.emit(string)
        } else if (serif.value == null) {
            serif.emit(string)
        } else {
            yield()//return のみだとUI Threadがブロックされる
            return null
        }
        return stateCoroutineScope.launch {
            delay(delayMillis)
            serif.compareAndSet(string, null)
        }
    }



    private var previousEventType by mutableStateOf<MascotEventType>(MascotEventType.None)
    suspend fun change(state: MascotEventType) {
        println("Changed:"+state)
        previousEventType = stateFlow.value
        stateFlow.emit(state)
    }

    suspend fun recoverEvent() {
        stateFlow.emit(previousEventType)
    }


    val charFlow = MutableSharedFlow<Char>()
    suspend fun feed(char: Char) {
        charFlow.emit(char)
    }
}
@Composable
fun rememberMascotState(
    initialMascotEventType: MascotEventType = MascotEventType.None, serverState: ServerState
) =
    remember { MascotState(initialMascotEventType, serverState) }

val texts = arrayOf(
    "優雅に雑音を聞く生活もいいかもしれないよ",
    "もぅﾏﾁﾞ無理...コンパイルしょ...",
    "チャンチャカチャンチャンチャチャンカチャンチャン床に抜け毛が落ちていると思っていたら～～～wwwwww～～…超弦理論でした～～～(11次元宇宙を近くする小梅太夫)あああああﾁｸｼｮｵｵｵｵｵｵｵｵｵｵ超弦理論→～～～",
    "技術的には可能です(ｷﾘｯ)",
    "ﾄﾞﾋｭｩｩｩｩﾝシンフォギアァァァァ!!!ｷｭｷｭｷｭｷｭｲﾝ!ｷｭｷｭｷｭｷｭｲﾝ!ｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｷｭｲﾝ!\n" + "ﾎﾟｫﾛﾎﾟﾎﾟﾎﾟﾎﾟﾍﾟﾍﾟﾍﾟﾍﾟﾋﾟﾋﾟﾋﾟﾋﾟﾋﾟｰﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟﾍﾟｰ♪",
    "も　う　ダ　メ　ぽ",
    "♪～",
    "ｾｰﾝｷｮ❗\uFE0Fｾﾝｷｮ❗\uFE0Fｱｶﾙｲｾﾝｷｮｰ‼\uFE0Fｾｰﾝｷｮ❗\uFE0Fｾﾝｷｮ❗\uFE0Fｱｶﾙｲｾﾝｷｮｰ‼\uFE0F⤴\uFE0F",
    "すごい楽しい素晴らしいソフトがあるんですよBlenderっていうんですけどね，",
    "あっ産地が直送されてきた",
    "消しゴムマジックで消してやるのさ☆",
    "俺モテすぎるから誰からもチョコ受け取らないんだよね",
    "お前守るよ(ｲｹｳﾞｫ)",
    "Twitterは脳を粉々に破壊するよ",
    "レターパックで現金送れ"
)