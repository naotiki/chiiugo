

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

    private val callbacks= mutableListOf<(ServerProtocol.SendEvent)->Unit>()
    fun onEventReceive( block:(ServerProtocol.SendEvent)->Unit){
        callbacks.add(block)
    }
    inner class ServerThread(val socket: Socket) : Thread() {
        @OptIn(ExperimentalSerializationApi::class)
        override fun run() {
            println("Ready")
            val sin = DataInputStream(socket.getInputStream())
            println("Connected")
            while (this.isAlive) {
                if (sin.available() >= HeaderSize) {
                    val size =sin.readInt()
                    print("Size=$size:")
                    val data=Cbor.decodeFromByteArray<ServerProtocol>(sin.readNBytes(size))
                    if (data is ServerProtocol.SendEvent){
                        callbacks.forEach { it(data) }
                    }
                    println(data)
                }

                if (socket.isClosed) {
                    break
                }
            }
        }
    }
}




private fun main() {
    runBlocking {
        Server().runServer()
    }
}