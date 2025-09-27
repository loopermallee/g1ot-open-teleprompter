package com.teleprompter.hub.ui

data class DeviceUiModel(
    val id: String,
    val name: String,
    val status: ConnectionStatus,
    val isBusy: Boolean = false,
    val errorMessage: String? = null
)

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}
