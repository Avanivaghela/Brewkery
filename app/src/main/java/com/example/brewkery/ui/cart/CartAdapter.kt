package com.example.brewkery.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.brewkery.data.model.CartItem
import com.example.brewkery.databinding.ItemCartBinding
import java.util.Locale

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            val item = cartItem.menuItem
            binding.tvCartItemName.text = item.name

            val customParts = mutableListOf<String>()
            cartItem.selectedSize?.let { customParts.add(it.label) }
            cartItem.selectedMilk?.let { customParts.add(it.name) }
            cartItem.selectedSugar?.let { customParts.add(it) }

            binding.tvCartItemCustomizations.text = if (customParts.isNotEmpty()) {
                customParts.joinToString(" • ")
            } else {
                "Standard"
            }

            binding.tvCartItemPrice.text = String.format(
                Locale.US, "$%.2f", cartItem.totalPrice
            )

            binding.tvCartItemQuantity.text = cartItem.quantity.toString()

            if (item.imageUrl.isNotEmpty()) {
                binding.ivCartItemImage.load(item.imageUrl) {
                    crossfade(true)
                }
            }

            binding.btnCartMinus.setOnClickListener {
                onQuantityChanged(cartItem, -1)
            }

            binding.btnCartPlus.setOnClickListener {
                onQuantityChanged(cartItem, 1)
            }
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem == newItem
    }
}
