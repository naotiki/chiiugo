import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import org.jetbrains.compose.animatedimage.Blank
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.invoke
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layout
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.letsplot.y
import org.jetbrains.kotlinx.kandy.util.color.Color.Companion as KandyColor

@Composable
fun ControlWindow(visible:Boolean=true,onCloseRequest: () -> Unit){
    Window(onCloseRequest = onCloseRequest, visible = visible) {
        Surface {
            var weekGraphImage by remember { mutableStateOf(ImageBitmap.Blank) }
            var langGraphImage by remember { mutableStateOf(ImageBitmap.Blank) }
            LaunchedEffect(Unit) {
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
                        fillColor("humidity"<Double>()) {
                            scale = continuous(range = KandyColor.YELLOW..KandyColor.RED)

                        }

                        borderLine.width = 0.0
                    }
                    layout {
                        this.size = 1000 to 1000
                    }
                }.toBufferedImage().toComposeImageBitmap()
            }
            Column(Modifier.fillMaxSize()) {
                var selectedTabIndex by remember { mutableStateOf(0) }
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
                Column(Modifier.weight(0.8f)) {
                    when (selectedTabIndex) {
                        0 -> {
                            LaunchedEffect(Unit){

                            }
                            Text("一週間のプログラミング時間", Modifier.padding(10.dp), fontSize = 25.sp)
                            Image(langGraphImage, null, Modifier.fillMaxWidth())
                        }

                        1 -> {

                        }
                    }
                }
            }
        }
    }
}