package com.maden.app_mirroring.domain.model

data class SocketInitModel(
    val url: String,
    val isReceived: Boolean = true,
    val roomId: String
)