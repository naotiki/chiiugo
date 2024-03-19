package me.naotiki.chiiugo

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt
import kotlin.random.Random

val colorList =
    listOf(0xFFFFFF00, 0xFF00FF00, 0xFFFF0000, 0xFF0000FF, 0xFF7DEBEB, 0xFFFF9B00, 0xFF800080, 0xFFFF1493)

enum class Behaviours(val behaviourFunc: BehaviourFunc) {
    Fall({
        gifName = "fallSUC.gif"
        delay(950)
    }),
    Gaming({
        coroutineScope {
            val aho = launch {
                while (true) {
                    var random: Color
                    do {
                        random = Color(colorList.random())
                    } while (colorState == random)
                    color.animateTo(random, tween(160, easing = EaseInBack))
                }
            }
            randomWalk()
            aho.cancel()
            color.snapTo(Color.White)
        }
    }),
    Flying({
        gifName = "flyingSUC.gif"
        delay(3200)
    })
}

val defaultBehaviour: BehaviourFunc = {
    randomWalk()
    delay(Random.nextLong(0, 2000))
    say(texts.random(), 5000)
    when ((0 until 30).random()) {
        0 -> {
            changeBehaviour(Behaviours.Fall.behaviourFunc)
        }

        in 1..7 -> {
            changeBehaviour(Behaviours.Gaming.behaviourFunc)
        }

        in 8..15 -> {
            changeBehaviour(Behaviours.Flying.behaviourFunc)
        }
    }
}
typealias BehaviourFunc = suspend MascotState.() -> Unit

class MascotState(private val screenSize: AreaSize) {
    private var behaviourFunc: BehaviourFunc? = null
    private var behaviourJob: Job? = null

    //Composition対応Coroutineスコープ内で実行
    suspend fun loop() {
        coroutineScope {

            while (true) {
                gifName = "SUC.gif"
                color.snapTo(Color.White)
                val f = behaviourFunc
                behaviourFunc = null
                behaviourJob = launch {
                    (f ?: defaultBehaviour).invoke(this@MascotState)
                }
                behaviourJob?.join()
                yield()
            }
        }
    }

    private val stateCoroutineScope = CoroutineScope(Dispatchers.Default)

    //nullで吹き出し非表示
    private val serif = MutableStateFlow<String?>(null)
    val serifFlow = serif.asStateFlow()
    suspend fun say(string: String, delayMillis: Long, important: Boolean = false): Job? {
        //println("Say $important ${serif.value}→ $string")
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

    var lockMovement = false
    fun suspendMovement() {
        lockMovement = true
        movementJob?.cancel()
    }

    suspend fun restartMovement(position: AreaPosition?) {
        if (position != null) {
            animatedAreaPosition.snapTo(position)
        }
        lockMovement = false
    }


    fun changeBehaviour(behaviourFunc: BehaviourFunc?) {
        this.behaviourFunc = behaviourFunc
        behaviourJob?.cancel()
    }

    private fun randomAreaPos(): AreaPosition {
        val x =
            screenSize.widthDp.value * 1.5f * ConfigManager.conf.areaOffset.first + (Random.nextFloat() * screenSize.widthDp.value * ConfigManager.conf.areaSize.first)
        val y =
            screenSize.heightDp.value * 1.5f * ConfigManager.conf.areaOffset.second + (Random.nextFloat() * screenSize.heightDp.value * ConfigManager.conf.areaSize.second)
        return AreaPosition(x.dp, y.dp)
    }

    suspend fun randomWalk(millis: Int = 5000) = walk(randomAreaPos(), millis)

    var gifName by mutableStateOf("SUC.gif")
    val color = Animatable(Color.White/*初期の色*/)
    val colorState by color.asState()
    private var animatedAreaPosition =
        Animatable(randomAreaPos(), AreaPosition.converter)
    val areaPosState by animatedAreaPosition.asState()
    var movementJob: Job? = null
    suspend fun walk(areaPosition: AreaPosition, millis: Int) {

        gifName = if (areaPosition.x.value > areaPosState.x.value) {
            if (areaPosition.y.value >= areaPosState.y.value) {
                "downright.gif"
            } else {
                "upright.gif"
            }
        } else {
            if (areaPosition.y.value > areaPosState.y.value) {
                "downleft.gif"
            } else {
                "upleft.gif"
            }
        }
        if (lockMovement) return
        coroutineScope {
            movementJob = launch {
                animatedAreaPosition.animateTo(
                    areaPosition,
                    tween(
                        millis, easing = EaseInOut
                    )
                )
            }
            movementJob?.join()
            movementJob = null
        }
    }


    val charMap = mutableStateListOf<Pair<Char, Pair<Int, Animatable<Float, AnimationVector1D>>>>()

    suspend fun feed(char: Char) {
        if (!char.isLetterOrDigit() || charMap.size > 1000) return
        val anim = Animatable(0f)
        val e = char to (Random.nextInt(ConfigManager.conf.imageSize.roundToInt()) to anim)
        charMap.add(e)
        coroutineScope {
            launch {
                anim.animateTo(ConfigManager.conf.imageSize - 10, tween(2000, easing = EaseOutBounce))
                delay(Random.nextLong(500, 2000))
                charMap.remove(e)
            }
        }
    }
}

@Composable
fun rememberMascotState(screenSize: AreaSize): MascotState {
    return remember(screenSize) { MascotState(screenSize) }
}

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