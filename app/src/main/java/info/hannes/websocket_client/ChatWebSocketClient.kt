package info.hannes.websocket_client

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI

class ChatWebSocketClient(serverUri: URI, private val messageListener: (String) -> Unit) : WebSocketClient(serverUri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        Timber.w("$handshakedata")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Timber.w("$code $reason")
    }

    override fun onMessage(message: String?) {
        Timber.d(message)
        messageListener.invoke(message ?: "")
    }

    override fun onError(ex: Exception?) {
        Timber.e(ex)
    }

    fun sendMessage(message: String) {
        Timber.d(message)
        send(message)
    }
}
