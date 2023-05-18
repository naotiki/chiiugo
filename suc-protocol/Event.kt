import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor

@Serializable
sealed interface Event {
    @Serializable
    data class OpenProject(val projectName:String):Event
    @Serializable
    data class StartBuild(val buildId:String) : Event
    @Serializable
    data class SuccessBuild(val buildId:String) : Event
    @Serializable
    data class FailedBuild(val buildId:String):Event
    @Serializable
    data class Typed(val char: Char):Event
}

@Serializable
sealed interface ServerProtocol {
    @Serializable
    object Hello: ServerProtocol
    @Serializable
    data class SendEvent(val event:Event): ServerProtocol
    @Serializable
    object GetCodingTime: ServerProtocol
    @Serializable
    data class CodingTime(val time:Long):ServerProtocol
    @Serializable
    object End: ServerProtocol
    @Serializable
    object Error:ServerProtocol

    @OptIn(ExperimentalSerializationApi::class)
    fun convertByteArray(): ByteArray {
        return Cbor.encodeToByteArray(serializer(),this).addHeader()
    }

}
