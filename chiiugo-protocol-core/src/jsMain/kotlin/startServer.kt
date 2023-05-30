@file:JsExport

import js.typedarrays.Uint8Array
import node.buffer.Buffer
import node.events.Event
import node.net.Socket
import node.net.connect


class Client(val clientData: ClientData){
    val socket:Socket = connect(PORT, "localhost") {
        console.log("Connected")
        send(ServerProtocol.Hello(clientData))
    }
    init {
        socket.on(Event.DATA){data:Buffer->
            console.log("Received DATA")
            console.log(data)
        }
    }
    fun send(v: ServerProtocol) {
        socket.write(Uint8Array(convertByteArray(
            v
        ).toTypedArray()))
    }
}
