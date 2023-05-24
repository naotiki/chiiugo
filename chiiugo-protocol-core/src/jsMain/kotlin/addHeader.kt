import js.buffer.ArrayBuffer
import js.buffer.DataView
import js.typedarrays.Uint8Array
import node.buffer.Buffer

actual fun addHeader(byteArray: ByteArray): ByteArray {
    val arrayBuffer= ArrayBuffer(HeaderSize + byteArray.size)
    return DataView(arrayBuffer).run {
        setInt32(0,byteArray.size)
        byteArray.forEachIndexed { index, byte ->
            setInt8(HeaderSize+index,byte)
        }

        ByteArray(byteLength){
            getInt8(it)
        }
    }
}

