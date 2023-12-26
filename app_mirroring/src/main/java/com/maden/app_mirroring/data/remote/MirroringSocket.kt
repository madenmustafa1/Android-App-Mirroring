package com.maden.app_mirroring.data.remote

import android.graphics.Bitmap
import android.os.Build
import com.maden.app_mirroring.common.util.SocketListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URI

class MirroringSocket(
    private val socketListener: SocketListener
) {

    private var _socketClient: MirroringSocketClient? = null

    suspend fun connect(url: URI): Boolean {
        return try {
            if (_socketClient == null) _socketClient = MirroringSocketClient(url, socketListener)
            if (_socketClient!!.isSocketConnected) return true

            if (_socketClient!!.isClosed) _socketClient!!.reconnect()
            else _socketClient!!.connectBlocking()

            return _socketClient!!.isSocketConnected
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isConnected(): Boolean {
        return try {
            _socketClient?.isSocketConnected ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sendScreen(bitmap: Bitmap) = withContext(Dispatchers.IO + _handler) {
        try {
            val stream = ByteArrayOutputStream()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 10, stream)
            } else {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 10, stream)
            }
            _socketClient?.sendByteArray(stream.toByteArray())
        } catch (_: Exception) {
        }
    }

    suspend fun close() {
        try {
            _socketClient?.close()
        } catch (_: Exception) {
        }
    }

    private val _handler = CoroutineExceptionHandler { _, exception ->
        println("@@@ CoroutineExceptionHandler got $exception")
    }
}


