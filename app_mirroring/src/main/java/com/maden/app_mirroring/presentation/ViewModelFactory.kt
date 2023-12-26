package com.maden.app_mirroring.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maden.app_mirroring.domain.model.SocketInitModel

class SocketViewModelFactory(
    private val model: SocketInitModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SocketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SocketViewModel(model) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}