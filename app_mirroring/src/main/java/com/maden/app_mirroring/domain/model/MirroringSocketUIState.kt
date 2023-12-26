package com.maden.app_mirroring.domain.model

sealed class MirroringSocketUIState {
    object Start : MirroringSocketUIState()
    object Error : MirroringSocketUIState()
    object Empty : MirroringSocketUIState()
    object Close : MirroringSocketUIState()
    object Stop : MirroringSocketUIState()
}