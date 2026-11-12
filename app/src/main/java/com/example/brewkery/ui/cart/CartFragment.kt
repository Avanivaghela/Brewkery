package com.example.brewkery.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.brewkery.databinding.FragmentCartBinding
import com.example.brewkery.ui.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupListeners()
        observeViewModel()
    }

    private fun setupAdapter() {
        cartAdapter = CartAdapter { cartItem, delta ->
            viewModel.updateCartQuantity(cartItem.id, delta)
        }
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun setupListeners() {
        binding.btnBackCart.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnClearCart.setOnClickListener {
            viewModel.clearCart()
            Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show()
        }

        binding.btnPlaceOrder.setOnClickListener {
            val order = viewModel.placeOrder()
            if (order != null) {
                (activity as? CartNavigationListener)?.navigateToOrderStatus()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collectLatest { items ->
                cartAdapter.submitList(items)

                if (items.isEmpty()) {
                    binding.cardEmptyCart.visibility = View.VISIBLE
                    binding.rvCartItems.visibility = View.GONE
                    binding.btnPlaceOrder.isEnabled = false
                    binding.btnPlaceOrder.alpha = 0.5f

                    binding.tvSubtotal.text = "$0.00"
                    binding.tvDeliveryFee.text = "$2.50"
                    binding.tvTax.text = "$0.00"
                    binding.tvTotalPayable.text = "$0.00"
                    binding.btnPlaceOrder.text = "🔒 Place Order Now • $0.00"
                } else {
                    binding.cardEmptyCart.visibility = View.GONE
                    binding.rvCartItems.visibility = View.VISIBLE
                    binding.btnPlaceOrder.isEnabled = true
                    binding.btnPlaceOrder.alpha = 1.0f

                    val subtotal = items.sumOf { it.totalPrice }
                    val meta = viewModel.storeMeta.value
                    val deliveryFee = meta?.deliveryFee ?: 2.50
                    val taxRate = (meta?.taxRatePercent ?: 8.0) / 100.0
                    val tax = subtotal * taxRate
                    val total = subtotal + deliveryFee + tax

                    val symbol = meta?.currencySymbol ?: "$"
                    val taxPercent = meta?.taxRatePercent ?: 8.0

                    binding.tvSubtotal.text = String.format(Locale.US, "%s%.2f", symbol, subtotal)
                    binding.tvDeliveryFee.text = String.format(Locale.US, "%s%.2f", symbol, deliveryFee)
                    binding.tvTaxLabel.text = String.format(Locale.US, "Est. Tax (%.1f%%)", taxPercent)
                    binding.tvTax.text = String.format(Locale.US, "%s%.2f", symbol, tax)
                    binding.tvTotalPayable.text = String.format(Locale.US, "%s%.2f", symbol, total)

                    binding.btnPlaceOrder.text = String.format(
                        Locale.US, "🔒 Place Order Now • %s%.2f", symbol, total
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface CartNavigationListener {
        fun navigateToOrderStatus()
    }
}
