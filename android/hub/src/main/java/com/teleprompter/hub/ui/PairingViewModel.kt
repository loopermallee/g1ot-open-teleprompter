package com.teleprompter.hub.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PairingViewModel(private val controller: PairingController) : ViewModel() {
    val devices: LiveData<List<DeviceUiModel>> = controller.devices
    val isRefreshing: LiveData<Boolean> = controller.isRefreshing
    val statusMessage: LiveData<String?> = controller.statusMessage

    fun onPairClicked(deviceId: String) {
        controller.pair(deviceId)
    }

    fun onDisconnectClicked(deviceId: String) {
        controller.disconnect(deviceId)
    }

    fun onRefreshRequested() {
        controller.refresh()
    }
}

class PairingViewModelFactory(
    private val controller: PairingController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PairingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PairingViewModel(controller) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
