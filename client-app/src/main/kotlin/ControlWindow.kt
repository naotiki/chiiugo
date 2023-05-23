import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.window.Window
import data.DailyStatistic
import data.DailyStatistics
import data.DatabaseFactory
import data.dbQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.jetbrains.compose.animatedimage.Blank
import org.jetbrains.exposed.sql.and
import org.jetbrains.kotlinx.kandy.dsl.invoke
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layout
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.letsplot.y
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

val startTime = Clock.System.now()
const val areaScale = 300

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ControlWindow(visible: Boolean = true, serverState: ServerState, onCloseRequest: () -> Unit, selectedTab: Int) {
    val statisticsState = rememberStatisticsState(serverState)
    Window(onCloseRequest = onCloseRequest, visible = visible) {
        Surface {
            val screenSize = rememberScreenSize()


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
                Column(
                    Modifier.weight(0.8f).fillMaxWidth().padding(15.dp, 5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> {
                            val coroutineScope = rememberCoroutineScope()
                            Button({
                                coroutineScope.launch {
                                    statisticsState.syncDate()
                                }
                            }, Modifier.align(Alignment.End)) {
                                Icon(Icons.Default.Refresh, null)
                                Text("更新")
                            }
                            Text("累計プログラミング時間", Modifier.padding(10.dp), fontSize = 25.sp)

                            LaunchedEffect(Unit) {
                                withContext(Dispatchers.IO){
                                    launch {
                                        statisticsState.syncDate()
                                    }
                                }
                            }
                            Text(
                                statisticsState.totalDatePeriod.run { "${hours}h ${minutes}m ${seconds}s" },
                                Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 35.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text("一週間のプログラミング時間", Modifier.padding(10.dp), fontSize = 25.sp)
                            Image(statisticsState.imageBitmap, null, Modifier.fillMaxWidth())
                        }

                        1 -> {
                            val coroutineScope = rememberCoroutineScope()
                            val configState by ConfigManager.configState.collectAsState()
                            Text("領域設定", Modifier, fontSize = 25.sp)
                            val offset =
                                remember {
                                    Offset(
                                        screenSize.toPx(areaScale.dp),
                                        screenSize.toPx((areaScale * (1 / screenSize.aspectRatio).dp))
                                    )
                                }


                            var handleOffset by remember(offset, configState) {
                                mutableStateOf(
                                    Offset(
                                        offset.x * configState.areaSize.first,
                                        offset.y * configState.areaSize.second
                                    )
                                )
                            }
                            val areaSize =
                                remember(handleOffset) {
                                    DpSize(
                                        screenSize.toDp(handleOffset.x),
                                        screenSize.toDp(handleOffset.y)
                                    )
                                }

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
                                        .size(
                                            width = areaScale.dp,
                                            height = (areaScale * (1 / screenSize.aspectRatio)).dp
                                        )
                                        .background(Color.LightGray)
                                ) {
                                    Box(
                                        //小さくてもハンドルだけは表示
                                        Modifier.requiredSizeIn(minWidth = 1.dp, minHeight = 1.dp).size(areaSize)
                                            .offset { areaOffset.round() }.background(Color.Gray)
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
                                                    if (r.x in 0f..offset.x - areaOffset.x && r.y in 0f..offset.y - areaOffset.y) {
                                                        handleOffset = r
                                                    }
                                                })
                                    }
                                }
                                Column {

                                    Text("サイズ: w=${areaSize.width.value * 100 / areaScale}%, h=${areaSize.height.value * (100 / (areaScale * (1 / screenSize.aspectRatio)))}%")
                                    Text(
                                        "オフセット: x=${screenSize.toDp(areaOffset.x).value * 100 / areaScale}%, y=${
                                            screenSize.toDp(
                                                areaOffset.y
                                            ).value * (100 / (areaScale * (1 / screenSize.aspectRatio)))
                                        }%"
                                    )
                                    Button({
                                        handleOffset = offset * 0.8f
                                        areaOffset = Offset.Zero
                                    }) { Text("Reset") }
                                }
                            }
                            var alwaysTop by remember { mutableStateOf(configState.alwaysTop) }
                            Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                                Text("常に最前面で表示")
                                Checkbox(alwaysTop, { alwaysTop = it })
                            }
                            Button({
                                coroutineScope.launch {
                                    ConfigManager.apply {
                                        this.areaOffset =
                                            screenSize.toDp(areaOffset.x).value / areaScale to screenSize.toDp(
                                                areaOffset.y
                                            ).value / (areaScale * (1 / screenSize.aspectRatio))
                                        this.areaSize =
                                            areaSize.width.value / areaScale to areaSize.height.value / (areaScale * (1 / screenSize.aspectRatio))
                                        this.alwaysTop = alwaysTop
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




class StatisticsState(serverState: ServerState) {
    data class ProjectData(val name: String, val openEpoch: Long, val closeEpoch: Long = openEpoch)

    val projectList = mutableStateMapOf<Long, ProjectData>()
    var originEpoch: Long? = null
    val statisticsDAO = StatisticsDAO()

    init {
        serverState.server.onEventReceive { event, id ->
            when (event) {
                Event.CloseProject -> {
                    val delta = System.currentTimeMillis() - (originEpoch ?: return@onEventReceive)
                    projectList.remove(id)
                    originEpoch = if (projectList.isNotEmpty()) {
                        System.currentTimeMillis()
                    } else null
                    applyUpTime(delta)
                }

                is Event.OpenProject -> {
                    if (originEpoch == null) {
                        originEpoch = System.currentTimeMillis()
                    }
                    projectList[id] = ProjectData(event.projectName, System.currentTimeMillis())
                }

                else -> {}
            }
        }
    }
    var totalDatePeriod by mutableStateOf(DateTimePeriod())
    var imageBitmap by mutableStateOf(ImageBitmap.Blank)


    suspend fun applyUpTime(delta: Long) {
        println("Adding $delta ms")
        statisticsDAO.addUptime(delta, today)
    }
    suspend fun syncDate(){
        totalDatePeriod=getTotalDatePeriod()
        imageBitmap=generateBitmapImage()
    }
    suspend fun getTotalDatePeriod(): DateTimePeriod {
        return statisticsDAO.getTotalTime().milliseconds.toDateTimePeriod()
    }
    suspend fun generateBitmapImage(): ImageBitmap {
        return plot(totalTimeUntil()) {

            x("日付"<String>()) {
                this.axis
            }

            y("時間 (分)"<Double>()) {

                //scale = continuous(0.0..25.5)
            }

            bars {

                borderLine.width = 0.0
            }
            layout {

                this.size = 800 to 800
            }
        }.toBufferedImage().toComposeImageBitmap()
    }

    suspend fun totalTimeUntil(): Map<String, List<Any>> {
        val l = statisticsDAO.totalTimeUntil(today.minus(7, DateTimeUnit.DAY), today)
        return mapOf(
            "日付" to l.keys.map { "${it.year}/${it.monthNumber}/${it.dayOfMonth}" },
            "時間 (分)" to l.values.map {
                it.toDouble(DurationUnit.MINUTES)
            }
        )
    }
}

@Composable
fun rememberStatisticsState(serverState: ServerState) = remember { StatisticsState(serverState) }
val timezone = TimeZone.currentSystemDefault()
val today get() = Clock.System.now().toLocalDateTime(timezone).date

class StatisticsDAO {
    suspend fun addUptime(upTimeOfSec: Long, date: LocalDate) = dbQuery {
        DailyStatistic.find {
            DailyStatistics.date eq date
        }.singleOrNull()?.apply {
            totalTime += upTimeOfSec
        } ?: DailyStatistic.new {
            this.date = date
            totalTime = upTimeOfSec
        }
    }

    suspend fun getTotalTime() = dbQuery {
        DailyStatistic.all().sumOf {
            it.totalTime
        }
    }

    suspend fun totalTimeUntil(start: LocalDate, end: LocalDate) = dbQuery {

        DailyStatistic.find {
            DailyStatistics.date greaterEq start and (DailyStatistics.date lessEq end)
        }.reversed().associate { it.date to it.totalTime.milliseconds }
    }

}