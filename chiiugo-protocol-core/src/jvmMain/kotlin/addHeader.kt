import java.nio.ByteBuffer

actual fun addHeader(byteArray: ByteArray): ByteArray {
    return ByteBuffer.allocate(HeaderSize + byteArray.size).putInt(byteArray.size).put(byteArray).array()
}