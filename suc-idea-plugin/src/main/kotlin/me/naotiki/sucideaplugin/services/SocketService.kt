import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

@Service
class SocketService : Disposable {
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var socket: Socket? = null
    private val notificationGroup = NotificationGroupManager.getInstance()
        .getNotificationGroup("SUC-Notification")
    val connecting get() = socket?.isConnected == true && socket?.isClosed == false

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun startServer(): Boolean {
        return runCatching {
            println("Try Close Server")
            closeServer()
            println("Closed")
            socket = Socket("127.0.0.1", PORT)
            println("Connected")
            val dataInputStream = DataInputStream(socket!!.getInputStream())
            notificationGroup.createNotification("Hello, Chiiugo!", NotificationType.INFORMATION)
                .notify(null)
            coroutineScope.launch {
                while (isActive) {
                    if (dataInputStream.available() >= HeaderSize) {
                        val dataLength = dataInputStream.readInt()
                        val byteData = dataInputStream.readNBytes(dataLength)
                        when (val e=ProtoBuf.decodeFromByteArray<ServerProtocol>(byteData)) {
                            ServerProtocol.End -> {
                                closeServer()
                            }

                            else -> {
                                println(e)
                            }
                        }
                    }
                    if (!connecting) {
                        println("Close In While")
                        closeServer()
                    }
                }
            }
            sendData(ServerProtocol.Hello, null)
        }.fold({
            it != null
        }, {
            if (it is UnknownHostException || it is IOException) {
                it.printStackTrace()
            } else throw it
            false
        })
    }

    private val outputStream get() = socket!!.getOutputStream()
    fun sendData(serverProtocol: ServerProtocol, project: Project?): Job? {
        if (socket == null) {
            return null
        }
        println("[Debug] Send: $serverProtocol")
        return coroutineScope.launch {
            withContext(Dispatchers.IO) {
                outputStream.write(convertByteArray(serverProtocol))
                outputStream.flush()
            }
        }
    }


    suspend fun closeServer() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.IO)
        if (connecting) {
            withTimeout(1000) {
                coroutineScope.launch {
                    sendData(ServerProtocol.End, null)?.join()
                }.join()
            }
        }
        socket?.close()
        socket = null
    }

    override fun dispose() {
        runBlocking {
            closeServer()
        }
        coroutineScope.cancel()
    }
}