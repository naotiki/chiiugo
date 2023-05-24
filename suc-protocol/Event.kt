import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.protobuf.schema.ProtoBufSchemaGenerator
import java.nio.ByteBuffer


@Serializable
sealed class Event {
    @Serializable
    data class OpenProject(val projectName:String):Event
    @Serializable
    object CloseProject: Event
    @Serializable
    data class StartBuild(val buildId:String) : Event
    @Serializable
    data class SuccessBuild(val buildId:String) : Event
    @Serializable
    data class FailedBuild(val buildId:String):Event
    @Serializable
    data class Typed(val char: Char):Event
    @Serializable
    data class OpenFile(val fileName:String,val fileTypeData: FileTypeData):Event
    @Serializable
    data class CloseFile(val fileName:String,val fileTypeData: FileTypeData):Event
}
@Serializable
data class FileTypeData(val idName:String, val displayName:String, val fileTypeExtension: String)

@Serializable
sealed interface ServerProtocol {
    @Serializable
    object Hello: ServerProtocol
    @Serializable
    data class SendEvent(val event:Event): ServerProtocol
    @Serializable
    object End: ServerProtocol
    @Serializable
    object Error:ServerProtocol

    @OptIn(ExperimentalSerializationApi::class)
    fun convertByteArray(): ByteArray {
        return Cbor.encodeToByteArray(serializer(),this).addHeader()
    }

}
const val PORT=0xCAD
const val HeaderSize = Int.SIZE_BYTES
fun ByteArray.addHeader(): ByteArray {

    return ByteBuffer.allocate(HeaderSize + size).putInt(size).put(this).array()
}
private fun main(){
    generateProtoBufScheme()
}

@OptIn(ExperimentalSerializationApi::class)
fun generateProtoBufScheme() {
    val descriptors = listOf(ServerProtocol.serializer().descriptor)
    val schemas = //ProtoBufSchemaGenerator.generateSchemaText(descriptors)
    ProtoBufSchemaGenerator.generateSchemaText(ServerProtocol.serializer().descriptor)
    println(schemas)
}