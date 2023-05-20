import ScreenSize.aspectRatio
import ScreenSize.toDp
import ScreenSize.toPx
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import org.jetbrains.compose.animatedimage.Blank
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.invoke
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layout
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.letsplot.y

val startTime = Clock.System.now()
const val areaScale = 300

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ControlWindow(visible: Boolean = true, serverState: ServerState, onCloseRequest: () -> Unit,selectedTab:Int) {
    Window(onCloseRequest = onCloseRequest, visible = visible) {
        Surface {
            var weekGraphImage by remember { mutableStateOf(ImageBitmap.Blank) }
            var langGraphImage by remember { mutableStateOf(ImageBitmap.Blank) }
            LaunchedEffect(Unit) {
                serverState.server.onEventReceive {
                    when (it.event) {

                        else -> {}
                    }
                }
                /*
                weekGraphImage=
                    plot(simpleDataset) {

                    x("日"<String>()) {

                    }

                    y("時間"<Double>()) {
                        scale = continuous(0.0..25.5)
                    }

                    bars {
                        fillColor("humidity"<Double>()) {
                            scale = continuous(range = KandyColor.YELLOW..KandyColor.RED)

                        }

                        borderLine.width = 0.0
                    }
                    layout {
                        this.size = 1000 to 1000
                    }
                }.toBufferedImage().toComposeImageBitmap()*/
                langGraphImage = plot(simpleDataset) {

                    x("言語"<String>()) {
                    }

                    y("時間"<Double>()) {

                        scale = continuous(0.0..25.5)
                    }

                    bars {

                        borderLine.width = 0.0
                    }
                    layout {

                        this.size = 800 to 800
                    }
                }.toBufferedImage().toComposeImageBitmap()
            }
            Column(Modifier.fillMaxSize()) {
                var selectedTabIndex by remember(selectedTab) { mutableStateOf(selectedTab) }
                TabRow(selectedTabIndex, Modifier.weight(0.1f)) {
                    Tab(selectedTabIndex == 0, {
                        selectedTabIndex = 0
                    }) {
                        Text("統計")
                    }
                    Tab(selectedTabIndex == 1, {
                        selectedTabIndex = 1
                    }) {
                        Text("設定")
                    }
                }
                Column(Modifier.weight(0.8f).fillMaxWidth().padding(15.dp,5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    when (selectedTabIndex) {
                        0 -> {
                            Text("累計プログラミング時間", Modifier.padding(10.dp), fontSize = 25.sp)
                            var totalTimeText by remember { mutableStateOf("00:00:00") }
                            LaunchedEffect(Unit) {
                                while (true) {

                                    val c = startTime.periodUntil(Clock.System.now(), TimeZone.currentSystemDefault())

                                    totalTimeText = "${c.hours}h ${c.minutes}m ${c.seconds}s"
                                    delay(1000)

                                }
                            }
                            Text(
                                totalTimeText,
                                Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 35.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text("一週間のプログラミング時間", Modifier.padding(10.dp), fontSize = 25.sp)
                            Image(langGraphImage, null, Modifier.fillMaxWidth())
                        }

                        1 -> {
                            val coroutineScope = rememberCoroutineScope()
                            val configState by ConfigManager.configState.collectAsState()
                            Text("領域設定", Modifier, fontSize = 25.sp)
                            val offset =
                                remember { Offset((areaScale.dp).toPx(), (areaScale * (1 / aspectRatio)).dp.toPx()) }


                            var handleOffset by remember(offset, configState) {
                                mutableStateOf(
                                    Offset(
                                        offset.x * configState.areaSize.first,
                                        offset.y * configState.areaSize.second
                                    )
                                )
                            }
                            val areaSize =
                                remember(handleOffset) { DpSize(handleOffset.x.toDp(), handleOffset.y.toDp()) }

                            var areaOffset by remember(offset, configState) {
                                mutableStateOf(
                                    Offset(
                                        offset.x * configState.areaOffset.first,
                                        offset.y * configState.areaOffset.second
                                    )
                                )
                            }
                            Row {
                                Box(
                                    Modifier.padding(10.dp)
                                        .size(width = areaScale.dp, height = (areaScale * (1 / aspectRatio)).dp)
                                        .background(Color.LightGray)
                                ) {
                                    Box(
                                        Modifier.size(areaSize).offset { areaOffset.round() }.background(Color.Gray)
                                            .onDrag {
                                                val r = areaOffset + it
                                                if (r.x in 0f..offset.x - handleOffset.x && r.y in 0f..offset.y - handleOffset.y) {
                                                    areaOffset = r
                                                }
                                            }) {

                                        Box(
                                            Modifier.requiredSize(10.dp)
                                                .offset { handleOffset.round().minus(IntOffset(5, 5)) }
                                                .background(Color.Red).onDrag {
                                                    val r = handleOffset + it
                                                    if (areaOffset.x + r.x in 0f..offset.x && areaOffset.y + r.y in 0f..offset.y) {
                                                        handleOffset = r
                                                    }
                                                })
                                    }
                                }
                                Column {

                                    Text("サイズ: w=${areaSize.width.value * 100 / areaScale}%, h=${areaSize.height.value * (100 / (areaScale * (1 / aspectRatio)))}%")
                                    Text("オフセット: x=${areaOffset.x.toDp().value * 100 / areaScale}%, y=${areaOffset.y.toDp().value * (100 / (areaScale * (1 / aspectRatio)))}%")
                                    Button({
                                        handleOffset = offset * 0.8f
                                        areaOffset = Offset.Zero
                                    }) { Text("Reset") }
                                }
                            }
                            var alwaysTop by remember { mutableStateOf(configState.alwaysTop) }
                            Row(Modifier,verticalAlignment = Alignment.CenterVertically) {
                                Text("常に最前面で表示")
                                Checkbox(alwaysTop,{alwaysTop=it})
                            }
                            Button({
                                coroutineScope.launch {
                                    ConfigManager.apply {
                                        this.areaOffset =
                                            areaOffset.x.toDp().value / areaScale to areaOffset.y.toDp().value / (areaScale * (1 / aspectRatio))
                                        this.areaSize =
                                            areaSize.width.value / areaScale to areaSize.height.value / (areaScale * (1 / aspectRatio))
                                        this.alwaysTop=alwaysTop
                                    }
                                }
                            }, Modifier.align(Alignment.End)) {
                                Text("適用")
                            }

                        }
                    }
                }
            }
        }
    }
}

val simpleDataset = mapOf(
    "言語" to listOf(0, 1, 2, 4, 5, 7, 8, 9).map { "Kt$it" },
    "時間" to listOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
)