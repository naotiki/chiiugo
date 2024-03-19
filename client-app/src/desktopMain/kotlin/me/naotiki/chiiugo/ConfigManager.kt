package me.naotiki.chiiugo

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
actual object ConfigManager {
    const val CONFIG_PATH = "ChiiugoConf.json"
    val configFile  = File(CONFIG_PATH)
    val configAbusolutePath get() =  configFile.absolutePath
    val configStateFlow = MutableStateFlow(loadDataOrDefault())
    actual val conf get() = configStateFlow.value
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

