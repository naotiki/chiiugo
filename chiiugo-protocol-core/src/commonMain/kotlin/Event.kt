@file:Suppress("RemoveRedundantQualifierName")
@file:OptIn(kotlin.js.ExperimentalJsExport::class)
@file:kotlin.js.JsExport

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
sealed class Event{
    @Serializable
    data class OpenProject(val projectName:String): Event()
    @Serializable
    object CloseProject: Event()
    @Serializable
    data class StartBuild(val buildId:String) : Event()
    @Serializable
    data class SuccessBuild(val buildId:String) : Event()
    @Serializable
    data class FailedBuild(val buildId:String): Event()
    @Serializable
    data class Typed(val char: Char): Event()
    @Serializable
    data class OpenFile(val fileName:String,val fileTypeData: FileTypeData): Event()
    @Serializable
    data class CloseFile(val fileName:String,val fileTypeData: FileTypeData): Event()
}


@Serializable
data class FileTypeData(val idName:String, val displayName:String, val fileTypeExtension: String)


@Serializable

sealed class ServerProtocol{
    @Serializable
    data class Hello(val clientData: ClientData) : ServerProtocol()
    @Serializable
    data class SendEvent(val event:Event): ServerProtocol()
    @Serializable
    object End: ServerProtocol()
    @Serializable
    object Error: ServerProtocol()

    @Serializable
    object Ping:ServerProtocol()
}//d.ts generatorでバグるのでclass
@Serializable
data class ClientData(
    val clientName:String,
    val version:String,
)

@OptIn(ExperimentalSerializationApi::class)
fun <T:ServerProtocol> convertByteArray(serverProtocol: T): ByteArray {
    return addHeader(ProtoBuf.encodeToByteArray(ServerProtocol.serializer(),serverProtocol))
}

const val PORT=0xCAD
const val HeaderSize = Int.SIZE_BYTES

