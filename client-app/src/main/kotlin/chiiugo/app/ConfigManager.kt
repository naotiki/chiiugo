package chiiugo.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.skiko.GpuPriority
import java.io.File

val ConfigJson= Json {
    prettyPrint=true
    encodeDefaults=true
}
object ConfigManager {
    const val CONFIG_PATH = "ChiiugoConf.json"
    val configFile  = File(CONFIG_PATH)
    val configAbusolutePath get() =  configFile.absolutePath
    val configStateFlow = MutableStateFlow(loadDataOrDefault())
    val conf get() = configStateFlow.value
    fun loadDataOrDefault(): ConfigData {
        return configFile.takeIf { it.exists() }?.readText()?.let {
            ConfigJson.decodeFromString<ConfigData>(it)
        } ?: ConfigData().also {
            println(configFile.absolutePath)
            configFile.writeText(ConfigJson.encodeToString(ConfigData.serializer(), it))
        }
    }
    suspend fun apply(configData: ConfigData){
        configStateFlow.emit(configData)
        configFile.writeText(ConfigJson.encodeToString(ConfigData.serializer(), configData))
    }
    suspend inline fun apply(configData: ConfigData.() -> Unit) {
        val c = configStateFlow.value.copy()
        c.configData()
        apply(c)
    }
}

@Serializable
data class ConfigData(
    var areaSize: Pair<Float, Float> = 0.8f to 0.8f,
    var areaOffset: Pair<Float, Float> = 0f to 0f,
    var alwaysTop: Boolean = true,
    var imageSize: Float = 175f,
    var spawnCount: Int = 1,
    var graphics: GraphicsData = GraphicsData(),
    var debug: DebugData = DebugData()
)

@Serializable
data class DebugData(val enable:Boolean=false)

@Serializable
data class GraphicsData(
    val vsync:Boolean=true,
    val fps:Boolean=false,
    val renderApi:String="",
    val gpu:GpuPriority=GpuPriority.Auto
)