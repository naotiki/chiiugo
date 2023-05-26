import Event.OpenProject
import ServerProtocol.SendEvent
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
    private var socket: Socket?=null
    private val notificationGroup = NotificationGroupManager.getInstance()
        .getNotificationGroup("SUC-Notification")
    val connecting get() = socket?.isConnected==true&&socket?.isClosed==false
    @OptIn(ExperimentalSerializationApi::class)
    fun startServer(): Boolean {
        return runCatching {
            closeServer()
            socket = Socket("127.0.0.1", PORT)
            val dataInputStream = DataInputStream(socket!!.getInputStream())
            notificationGroup.createNotification("[Debug] Connected !", NotificationType.INFORMATION)
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
                            ServerProtocol.Error -> TODO()
                            ServerProtocol.Hello -> TODO()
                            else -> {

                            }
                        }
                    }
                    if (!connecting){
                        closeServer()
                    }
                }
            }
            sendData(ServerProtocol.Hello, null)
        }.fold({
            true
        }, {
            if (it is UnknownHostException || it is IOException) {
                it.printStackTrace()
            } else throw it
            false
        })
    }

    private val outputStream get() = socket!!.getOutputStream()
    fun sendData(serverProtocol: ServerProtocol, project: Project? ): Job {
        if (socket==null){
            startServer()
        }
        notificationGroup.createNotification("[Debug] Send: $serverProtocol", NotificationType.INFORMATION)
            .notify(project)
        return coroutineScope.launch {
            withContext(Dispatchers.IO) {
                outputStream.write(convertByteArray(serverProtocol))
                outputStream.flush()
            }
        }
    }


    fun closeServer(){
        println("Close Server")
        coroutineScope.cancel()
        coroutineScope= CoroutineScope(Dispatchers.IO)
        if (!connecting)return
        runBlocking {
            sendData(ServerProtocol.End,null).join()
        }
        socket?.close()
        socket=null
    }

    override fun dispose() {
        closeServer()
        coroutineScope.cancel()
    }
}