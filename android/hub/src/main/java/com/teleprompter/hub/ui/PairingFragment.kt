package com.teleprompter.hub.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.teleprompter.hub.R
import com.teleprompter.hub.databinding.FragmentPairingBinding

class PairingFragment : Fragment() {

    private var _binding: FragmentPairingBinding? = null
    private val binding get() = _binding!!

    private var controller: PairingController? = null

    private val viewModel: PairingViewModel by viewModels {
        val pairingController = controller
            ?: throw IllegalStateException("Parent context must provide PairingController")
        PairingViewModelFactory(pairingController)
    }

    private val deviceAdapter by lazy {
        DeviceListAdapter(
            onPair = viewModel::onPairClicked,
            onDisconnect = viewModel::onDisconnectClicked
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = when {
            context is PairingControllerProvider -> context.providePairingController()
            parentFragment is PairingControllerProvider ->
                (parentFragment as PairingControllerProvider).providePairingController()
            else -> null
        }
        if (controller == null) {
            throw IllegalStateException("Host must implement PairingControllerProvider")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPairingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupEmptyState()
        observeState()
    }

    private fun setupToolbar() {
        binding.pairingToolbar.setNavigationIcon(R.drawable.ic_pair)
    }

    private fun setupRecyclerView() {
        binding.deviceRecyclerView.adapter = deviceAdapter
        binding.deviceRecyclerView.itemAnimator = null
    }

    private fun setupSwipeRefresh() {
        binding.deviceListRefreshLayout.setOnRefreshListener {
            viewModel.onRefreshRequested()
        }
    }

    private fun setupEmptyState() {
        binding.emptyStateScanButton.setOnClickListener {
            viewModel.onRefreshRequested()
        }
    }

    private fun observeState() {
        viewModel.devices.observe(viewLifecycleOwner) { devices ->
            deviceAdapter.submitList(devices)
            val isEmpty = devices.isEmpty()
            binding.emptyStateGroup.isVisible = isEmpty
            binding.deviceListRefreshLayout.isVisible = !isEmpty
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            binding.deviceListRefreshLayout.isRefreshing = refreshing
            binding.pairingProgress.isVisible = refreshing
            binding.pairingStatusChip.isVisible = refreshing
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        controller = null
    }
}
