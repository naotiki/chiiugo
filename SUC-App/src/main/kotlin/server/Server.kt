

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer

const val PORT = 0xCAD
class Server(val port: Int=PORT) {
    val serverSocket = ServerSocket(port)
    suspend fun runServer()= withContext(Dispatchers.IO){
        suspendCancellableCoroutine<Unit> {
            it.invokeOnCancellation { serverSocket.close() }
            while (it.isActive){
                val socket=serverSocket.accept()
                ServerThread(socket).start()
            }
        }

    }
}
class ServerThread(val socket: Socket) : Thread() {
    override fun run() {
        println("Ready")
        var sin = DataInputStream(socket.getInputStream())
        var sout = socket.getOutputStream()
        println("Connected")
        while (this.isAlive) {
            if (sin.available() >= HeaderSize) {
                val size =sin.readInt()
                print("Size=$size:")
                val data=Cbor.decodeFromByteArray<ServerProtocol>(sin.readNBytes(size))
                println(data)


            }

            if (socket.isClosed) {
                break
            }
        }
    }
}


const val HeaderSize = Int.SIZE_BYTES
fun ByteArray.addHeader(): ByteArray {

    return ByteBuffer.allocate(HeaderSize + size).putInt(size).put(this).array()
}
//   Header(n)      Body
//   [4 Byte]     [n Byte]

private fun main() {
    runBlocking {
        Server().runServer()
    }
}