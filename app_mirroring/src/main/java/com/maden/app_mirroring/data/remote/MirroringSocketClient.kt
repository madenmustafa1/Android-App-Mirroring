package com.maden.app_mirroring.data.remote

import com.maden.app_mirroring.common.util.SocketListener
import com.maden.app_mirroring.domain.model.MirroringSocketUIState
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

internal class MirroringSocketClient(
    serverUri: URI,
    private val socketListener: SocketListener
) :
    WebSocketClient(serverUri) {
    var isSocketConnected = false


    override fun onOpen(handshakedata: ServerHandshake?) {
        isSocketConnected = true
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        isSocketConnected = false
        socketListener.uiState(MirroringSocketUIState.Close)
    }

    override fun onMessage(message: String?) {
        message?.let {
            when (it.trim().lowercase()) {
                "close" -> {
                    isSocketConnected = false
                    socketListener.uiState(MirroringSocketUIState.Close)
                }

                "stop" -> {
                    socketListener.uiState(MirroringSocketUIState.Stop)
                }

                "start" -> {
                    socketListener.uiState(MirroringSocketUIState.Start)
                }
            }
        }
    }

    override fun onError(ex: Exception?) {
        isSocketConnected = false
        socketListener.uiState(MirroringSocketUIState.Error)
    }

    fun sendByteArray(bitmapByte: ByteArray) {
        send(bitmapByte)
    }
}