import ServerProtocol.SendEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class SocketServer(val port: Int=PORT) {
    val serverSocket = ServerSocket(port)

    suspend fun runServer()= withContext(Dispatchers.IO){
        suspendCancellableCoroutine<Unit> {
            it.invokeOnCancellation { serverSocket.close() }
            runCatching {
                while (it.isActive){
                    val socket=serverSocket.accept()
                    serverThreads+=ServerThread(socket).apply {
                        start()
                    }
                }
            }.onFailure { throwable ->
                if (throwable is SocketException){
                    throwable.printStackTrace()
                }else throw throwable
            }
            println("Server Shutdown")
        }

    }

    private val callbacks= mutableListOf<suspend (event:Event,id:Long)->Unit>()
    fun onEventReceive( block:suspend (event:Event,id:Long)->Unit){
        callbacks.add(block)
    }
    val serverThreads= mutableStateListOf<ServerThread>()
    fun stop() {
        serverThreads.forEach { it.close() }
        serverSocket.close()
    }

    val coroutineScope= CoroutineScope(Dispatchers.Default)
    inner class ServerThread(private val socket: Socket) : Thread() {
        var clientData by mutableStateOf(ClientData("Unknown","Unknown"))
        var timeoutJob:Job=timeout(TIMEOUT){
            close()
        }
        @OptIn(ExperimentalSerializationApi::class)
        override fun run() {
            println("Ready")
            val sin = DataInputStream(socket.getInputStream())
            println("Connected")
            while (this.isAlive) {
                if (sin.available() >= HeaderSize) {
                    val size =sin.readInt()
                    print("Size=$size:")
                    val data=ProtoBuf.decodeFromByteArray<ServerProtocol>(sin.readNBytes(size))
                    when (data) {
                        is SendEvent -> {
                            callbacks.forEach { coroutineScope.launch { it(data.event,id) } }
                        }

                        is ServerProtocol.End -> {
                            close()
                        }

                        is ServerProtocol.Hello -> {
                            clientData=data.clientData
                        }
                        else -> {}
                    }
                    timeoutJob.cancel()
                    timeoutJob=timeout(TIMEOUT){
                        close()
                    }
                    println(data)
                }
                if (socket.isClosed){
                    break
                }
            }
            println("End Socket")
        }
        inline fun timeout(timeoutMillis:Long, crossinline block:()->Unit): Job {
            return coroutineScope.launch {
                delay(timeoutMillis)
                block()
            }
        }
        fun send(serverProtocol: ServerProtocol){
            socket.getOutputStream().apply {
                write(convertByteArray(serverProtocol))
                flush()
            }
        }
        fun close(){
            timeoutJob.cancel()
            socket.getOutputStream().apply {
                write(convertByteArray(ServerProtocol.End))
                flush()
            }
            socket.close()
            callbacks.forEach { coroutineScope.launch { it(Event.CloseProject,id) } }
            serverThreads.remove(this)
        }
        override fun interrupt() {
            close()
            super.interrupt()
        }
    }
}

const val TIMEOUT:Long=5000*60


private fun main() {
    runBlocking {
        SocketServer().runServer()
    }
}