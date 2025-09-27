package com.teleprompter.hub.ui

import androidx.lifecycle.LiveData

interface PairingController {
    val devices: LiveData<List<DeviceUiModel>>
    val isRefreshing: LiveData<Boolean>
    val statusMessage: LiveData<String?>

    fun pair(deviceId: String)
    fun disconnect(deviceId: String)
    fun refresh()
}

interface PairingControllerProvider {
    fun providePairingController(): PairingController
}
