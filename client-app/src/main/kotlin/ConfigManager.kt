import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

val ConfigJson= Json {
    prettyPrint=true
    encodeDefaults=true
}
object ConfigManager {
    const val CONFIG_PATH = "ChiiugoConf.json"
    val configFile  = File(CONFIG_PATH)
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

    suspend inline fun apply(configData: ConfigData.() -> Unit) {
        val c = configStateFlow.value.copy()
        c.configData()
        configStateFlow.emit(c)
        configFile.writeText(ConfigJson.encodeToString(ConfigData.serializer(), c))
    }
}

@Serializable
data class ConfigData(
    var areaSize: Pair<Float, Float> = 0.8f to 0.8f,
    var areaOffset: Pair<Float, Float> = 0f to 0f,
    var alwaysTop:Boolean=true,
    var imageSize:Float=imageSizeDp.value,
)