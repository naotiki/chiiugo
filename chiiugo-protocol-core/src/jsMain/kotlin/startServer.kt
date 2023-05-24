@file:JsExport

import js.typedarrays.Uint8Array
import node.buffer.Buffer
import node.net.Socket
import node.net.connect


class Client{
    private val socket:Socket = connect(PORT, "localhost") {
        console.log("Connected")
    }

    fun send(v: ServerProtocol) {
        socket.write(Uint8Array(convertByteArray(
            v
        ).toTypedArray()))
    }
}
