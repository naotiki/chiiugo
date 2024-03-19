package me.naotiki.chiiugo

import kotlinx.serialization.Serializable
import org.jetbrains.skiko.GpuPriority

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
    val gpu: GpuPriority = GpuPriority.Auto
)