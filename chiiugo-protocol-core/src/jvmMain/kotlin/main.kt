import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.schema.ProtoBufSchemaGenerator

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    val descriptors = listOf(SocketProtocol.serializer().descriptor)
    val schemas = ProtoBufSchemaGenerator.generateSchemaText(descriptors)
    println(schemas)
}