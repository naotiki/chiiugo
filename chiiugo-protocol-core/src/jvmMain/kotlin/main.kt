import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.schema.ProtoBufSchemaGenerator

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    val descriptors = listOf(ServerProtocol.serializer().descriptor)
    val schemas = ProtoBufSchemaGenerator.generateSchemaText(descriptors)
    println(schemas)
}