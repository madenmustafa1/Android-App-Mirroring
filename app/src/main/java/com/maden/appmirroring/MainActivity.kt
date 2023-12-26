package com.maden.appmirroring

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maden.app_mirroring.presentation.SocketViewModel
import com.maden.app_mirroring.domain.model.MirroringSocketUIState
import com.maden.app_mirroring.domain.model.SocketInitModel
import com.maden.app_mirroring.common.util.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val linearLayout: LinearLayout by lazy { findViewById(R.id.bodyLayout) }
    private val imgTest: ImageView by lazy { findViewById(R.id.imgTest) }
    private val counter: TextView by lazy { findViewById(R.id.counter) }
    private val startButton: Button by lazy { findViewById(R.id.startButton) }
    private val stopButton: Button by lazy { findViewById(R.id.stopButton) }
    private val socketViewModel = SocketViewModel(
        SocketInitModel(
            url = "http://10.0.2.2:8080/screenMirroring",
            roomId = "courier"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()

        lifecycle.addObserver(socketViewModel)
        observeSocket()
    }

    private fun observeSocket() {
        socketViewModel.shareScreenLiveData.observe(this) {
            socketViewModel.shareScreen(linearLayout.toBitmap())
        }

        socketViewModel.uiState.observe(this) {
            when (it) {
                MirroringSocketUIState.Start -> socketViewModel.shareScreen(linearLayout.toBitmap())
                MirroringSocketUIState.Close -> socketViewModel.closeSocket()
                MirroringSocketUIState.Error -> socketViewModel.closeSocket()
                else -> {}
            }
        }
    }

    private fun initViews() {
        startButton.setOnClickListener {
            lifecycleScope.launch { timer() }
        }

        stopButton.setOnClickListener { socketViewModel.closeSocket() }
    }

    private var timer: Int = 50000000

    private suspend fun timer() {
        withContext(Dispatchers.IO) {
            while (true) {
                timer--

                withContext(Dispatchers.Main) {
                    counter.text = timer.toString()
                    if (timer % 5 == 0) {
                        imgTest.setImageDrawable(getDrawable(R.drawable.dog))
                    }

                    if (timer % 10 == 0) {
                        imgTest.setImageDrawable(getDrawable(R.drawable.cat))
                    }
                }
                delay(200)
            }
        }
    }
}
