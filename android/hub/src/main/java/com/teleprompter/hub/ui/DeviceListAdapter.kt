package com.teleprompter.hub.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teleprompter.hub.R
import com.teleprompter.hub.databinding.ItemDiscoveredDeviceBinding

class DeviceListAdapter(
    private val onPair: (String) -> Unit,
    private val onDisconnect: (String) -> Unit
) : ListAdapter<DeviceUiModel, DeviceListAdapter.DeviceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDiscoveredDeviceBinding.inflate(inflater, parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeviceViewHolder(
        private val binding: ItemDiscoveredDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceUiModel) {
            binding.deviceName.text = device.name
            binding.deviceAddress.text = device.id

            val context = binding.root.context
            val (statusText, chipColor, textColor) = when (device.status) {
                ConnectionStatus.CONNECTED -> Triple(
                    context.getString(R.string.device_status_connected),
                    ContextCompat.getColor(context, R.color.status_chip_connected),
                    ContextCompat.getColor(context, R.color.status_chip_connected_text)
                )

                ConnectionStatus.CONNECTING -> Triple(
                    context.getString(R.string.device_status_connecting),
                    ContextCompat.getColor(context, R.color.status_chip_connecting),
                    ContextCompat.getColor(context, R.color.status_chip_connecting_text)
                )

                ConnectionStatus.ERROR -> Triple(
                    context.getString(R.string.device_status_error),
                    ContextCompat.getColor(context, R.color.status_chip_error),
                    ContextCompat.getColor(context, R.color.status_chip_error_text)
                )

                ConnectionStatus.DISCONNECTED -> Triple(
                    context.getString(R.string.device_status_disconnected),
                    ContextCompat.getColor(context, R.color.status_chip_disconnected),
                    ContextCompat.getColor(context, R.color.status_chip_disconnected_text)
                )
            }

            binding.statusChip.text = statusText
            binding.statusChip.chipBackgroundColor = ColorStateList.valueOf(chipColor)
            binding.statusChip.setTextColor(textColor)

            val isConnected = device.status == ConnectionStatus.CONNECTED
            binding.actionButton.apply {
                text = context.getString(if (isConnected) R.string.device_action_disconnect else R.string.device_action_pair)
                icon = ContextCompat.getDrawable(
                    context,
                    if (isConnected) R.drawable.ic_disconnect else R.drawable.ic_pair
                )
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        if (isConnected) R.color.disconnect_button_background else R.color.pair_button_background
                    )
                )
                setTextColor(ContextCompat.getColor(context, R.color.action_button_text))
                isEnabled = !device.isBusy
                setOnClickListener {
                    if (isConnected) onDisconnect(device.id) else onPair(device.id)
                }
            }

            binding.progressIndicator.isVisible = device.isBusy
            binding.statusChip.isCloseIconVisible = false

            binding.errorMessage.apply {
                isVisible = device.errorMessage?.isNotEmpty() == true
                text = device.errorMessage
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<DeviceUiModel>() {
        override fun areItemsTheSame(oldItem: DeviceUiModel, newItem: DeviceUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DeviceUiModel, newItem: DeviceUiModel): Boolean =
            oldItem == newItem
    }
}
