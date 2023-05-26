@file:JsExport

import js.typedarrays.Uint8Array
import node.buffer.Buffer
import node.events.Event
import node.net.Socket
import node.net.connect


class Client{
    val socket:Socket = connect(PORT, "localhost") {
        console.log("Connected")
    }
    init {
        socket.on(Event.DATA){
            console.log("Received DATA")
            console.dir(it)
        }
    }
    fun send(v: ServerProtocol) {
        socket.write(Uint8Array(convertByteArray(
            v
        ).toTypedArray()))
    }
}
