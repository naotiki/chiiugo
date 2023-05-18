import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

@Service
class SocketService : Disposable {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var socket: Socket
    private val notificationGroup = NotificationGroupManager.getInstance()
        .getNotificationGroup("SUC-Notification")

    @OptIn(ExperimentalSerializationApi::class)
    fun startServer(): Boolean {
        return runCatching {
            socket = Socket("127.0.0.1", PORT)
            val dataInputStream = DataInputStream(socket.getInputStream())

            coroutineScope.launch {
                while (isActive) {
                    if (dataInputStream.available() >= HeaderSize) {
                        val dataLength = dataInputStream.readInt()
                        val byteData = dataInputStream.readNBytes(dataLength)
                        when (Cbor.decodeFromByteArray<ServerProtocol>(byteData)) {
                            ServerProtocol.End -> TODO()
                            ServerProtocol.Error -> TODO()
                            ServerProtocol.Hello -> TODO()
                            else -> {

                            }
                        }
                    }
                }
            }
        }.fold({
            true
        }, {
            if (it is UnknownHostException || it is IOException) {
                it.printStackTrace()
            } else throw it
            false
        })
    }

    private val outputStream get() = socket.getOutputStream()
    fun sendData(serverProtocol: ServerProtocol, project: Project? = null) {
        notificationGroup.createNotification("[Debug] Send: $serverProtocol", NotificationType.INFORMATION)
            .notify(project)
        coroutineScope.launch {
            outputStream.write(serverProtocol.convertByteArray())
            outputStream.flush()
        }
    }

    override fun dispose() {
        socket.close()
        coroutineScope.cancel()
    }
}