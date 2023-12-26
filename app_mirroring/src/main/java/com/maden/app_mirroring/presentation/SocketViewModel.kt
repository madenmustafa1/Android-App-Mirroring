package com.maden.app_mirroring.presentation

import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maden.app_mirroring.domain.model.MirroringSocketUIState
import com.maden.app_mirroring.domain.model.SocketInitModel
import com.maden.app_mirroring.data.remote.MirroringSocket
import com.maden.app_mirroring.common.util.SocketListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URI

class SocketViewModel(
    private val model: SocketInitModel
) : ViewModel(), SocketListener, LifecycleEventObserver {

    private val _socketClient = MirroringSocket(this)
    private val _uiState = MutableLiveData<MirroringSocketUIState>(MirroringSocketUIState.Empty)
    val uiState: LiveData<MirroringSocketUIState> = _uiState

    private fun connectSocket() {
        val connectUri = URI(model.url + "?roomId=" + model.roomId + "&isReceiver=" + model.isReceived)
        CoroutineScope(Dispatchers.IO + _handler).launch {
            _socketClient.connect(connectUri)
        }
    }

    private var _shareScreenLiveData = MutableLiveData<Boolean>()
    var shareScreenLiveData: LiveData<Boolean> = _shareScreenLiveData

    fun shareScreen(view: Bitmap?) {
        CoroutineScope(Dispatchers.IO + _handler).launch {
            try {
                if (uiState.value !is MirroringSocketUIState.Start)
                    return@launch

                if (_socketClient.isConnected() && view != null)
                    _socketClient.sendScreen(view)

                delay(200)

                _shareScreenLiveData.postValue(true)
            } catch (e: Exception) {
                delay(500)
                if (uiState.value is MirroringSocketUIState.Start)
                    _shareScreenLiveData.postValue(true)
            }
        }
    }

    fun closeSocket() {
        CoroutineScope(Dispatchers.IO + _handler).launch {
            try {
                _socketClient.close()
            } catch (_: Exception) {
            }
        }
    }

    private val _handler = CoroutineExceptionHandler { _, exception ->
        println("@@@ _handler CoroutineExceptionHandler got $exception")
    }

    override fun uiState(uiState: MirroringSocketUIState) {
        CoroutineScope(Dispatchers.IO + _handler).launch {
            try {
                _uiState.postValue(uiState)
            } catch (_: Exception) {
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> closeSocket()
            Lifecycle.Event.ON_STOP -> closeSocket()
            Lifecycle.Event.ON_DESTROY -> closeSocket()
            Lifecycle.Event.ON_RESUME -> connectSocket()
            else -> {}
        }
    }
}