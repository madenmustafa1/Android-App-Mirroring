package com.maden.app_mirroring.common.util

import com.maden.app_mirroring.domain.model.MirroringSocketUIState

interface SocketListener {
    fun uiState(uiState: MirroringSocketUIState)
}