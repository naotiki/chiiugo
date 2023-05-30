@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

import js.typedarrays.Uint8Array
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import node.buffer.Buffer
import node.events.Event
import node.net.Socket
import kotlin.js.Promise


@OptIn(ExperimentalSerializationApi::class)
class Client(val clientData: ClientData):Disposable {
    @JsExport.Ignore
    var socket: Socket? = null

    fun startServer(): Promise<Unit> {
        return Promise { resolve, reject ->
            val s = Socket()
            s.once(Event.ERROR) {
                console.log("Error")
                resolve(null)
            }
            s.on(Event.DATA) { data: Buffer ->
                console.log("Size:${data.byteLength}")
                console.log(data)
                if (data.byteLength >= HeaderSize) {
                    val a = data.readIntBE(0, HeaderSize).toInt()
                    val d = ProtoBuf.decodeFromByteArray(SocketProtocol.serializer(), ByteArray(a) {
                        data.readInt8(HeaderSize + it).toByte()
                    })
                    console.log(d)
                    when(d){
                        SocketProtocol.End -> {
                            socket?.end()
                            socket=null
                        }
                        SocketProtocol.Error -> TODO()
                        is SocketProtocol.Hello -> TODO()
                        SocketProtocol.Ping -> TODO()
                        is SocketProtocol.SendEvent -> TODO()
                    }
                }
            }
            s.connect(PORT) {
                console.log("Connected")
                s.on(Event.ERROR){socket=null}
                resolve(s)
            }
        }.then {
            socket = it
            send(SocketProtocol.Hello(clientData))
        }
    }

    fun send(v: SocketProtocol): Boolean {
        return socket?.write(
            Uint8Array(
                convertByteArray(
                    v
                ).toTypedArray()
            )
        ) != null
    }

    fun close() {
        send(SocketProtocol.End)
        socket?.resetAndDestroy()
        socket = null
    }

    override fun dispose() {
        println("Close Socket Client")
        close()
    }
}

interface Disposable {
    fun dispose():Any
}


