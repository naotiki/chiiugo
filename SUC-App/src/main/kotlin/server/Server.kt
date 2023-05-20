

import kotlinx.coroutines.*
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
            println("Server Shutdown")
        }

    }

    private val callbacks= mutableListOf<suspend (ServerProtocol.SendEvent)->Unit>()
    fun onEventReceive( block:suspend (ServerProtocol.SendEvent)->Unit){
        callbacks.add(block)
    }
    val coroutineScope= CoroutineScope(Dispatchers.Default)
    inner class ServerThread(private val socket: Socket) : Thread() {
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
                        callbacks.forEach { coroutineScope.launch { it(data) } }
                    }
                    println(data)
                }

                if (socket.isClosed) {
                    break
                }
            }
            println("End Socket")
        }

        override fun interrupt() {
            socket.close()
            super.interrupt()
        }
    }
}




private fun main() {
    runBlocking {
        Server().runServer()
    }
}