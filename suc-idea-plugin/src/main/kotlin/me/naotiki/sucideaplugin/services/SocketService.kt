import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
val clientData=ClientData("IntelliJ IDEA","v0")
@Service
class SocketService : Disposable {
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var socket: Socket? = null
    private val notificationGroup by lazy { NotificationGroupManager.getInstance()
        .getNotificationGroup("SUC-Notification") }
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
                        when (val e=ProtoBuf.decodeFromByteArray<SocketProtocol>(byteData)) {
                            SocketProtocol.End -> {
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
            sendData(SocketProtocol.Hello(clientData))
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
    fun sendData(socketProtocol: SocketProtocol): Job? {
        if (socket == null) {
            return null
        }
        println("[Debug] Send: $socketProtocol")
        return coroutineScope.launch {
            withContext(Dispatchers.IO) {
                outputStream.write(convertByteArray(socketProtocol))
                outputStream.flush()
            }
        }
    }


    suspend fun closeServer() {
        if (connecting) {
            withTimeoutOrNull(1000) {
                launch {
                    sendData(SocketProtocol.End)?.join()
                }.join()
            }
        }
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.IO)
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