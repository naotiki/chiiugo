import node.events.Event
import node.net.NetConnectOpts
import node.net.connect
import node.net.createServer

fun main() {
    println(greeting("chiiugo-js-core"))
}

fun greeting(name: String) =
    "Hello, $name"
