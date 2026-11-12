package com.example.brewkery.ui.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.brewkery.databinding.FragmentOrderStatusBinding
import com.example.brewkery.ui.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderStatusFragment : Fragment() {

    private var _binding: FragmentOrderStatusBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToMenu.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeOrder.collectLatest { order ->
                if (order != null) {
                    binding.tvOrderTicketId.text = order.ticketId
                    binding.tvOrderStatusBadge.text = order.status
                    binding.tvEstimatedWait.text = order.estimatedWait
                    binding.tvItemsOrderedCount.text = "${order.itemCount} Item(s)"
                    binding.tvOrderStatusMessage.text = order.statusMessage
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
