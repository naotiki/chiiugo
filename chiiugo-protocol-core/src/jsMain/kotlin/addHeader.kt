import js.buffer.ArrayBuffer
import js.buffer.DataView
import js.core.toArray
import js.typedarrays.Uint8Array
import node.buffer.Buffer

actual fun addHeader(byteArray: ByteArray): ByteArray {
    val buffer= Buffer(HeaderSize + byteArray.size)
    buffer.writeIntBE(byteArray.size,0,HeaderSize)
    byteArray.forEachIndexed { index, byte ->
        buffer.writeInt8(byte,HeaderSize+index)
    }
    return ByteArray(buffer.length){
        buffer.readInt8(it).toByte()
    }
}

