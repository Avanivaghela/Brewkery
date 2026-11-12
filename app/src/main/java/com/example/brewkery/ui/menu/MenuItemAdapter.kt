package com.example.brewkery.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.databinding.ItemMenuCardBinding
import java.util.Locale

class MenuItemAdapter(
    private val onItemClick: (MenuItem) -> Unit
) : ListAdapter<MenuItem, MenuItemAdapter.MenuItemViewHolder>(MenuItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = ItemMenuCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MenuItemViewHolder(
        private val binding: ItemMenuCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
            binding.tvItemName.text = item.name

            if (!item.badge.isNullOrEmpty()) {
                binding.tvItemBadge.visibility = View.VISIBLE
                binding.tvItemBadge.text = item.badge
            } else {
                binding.tvItemBadge.visibility = View.GONE
            }

            binding.tvItemRating.text = String.format(
                Locale.US, "%.1f (%d)", item.rating, item.reviewCount
            )

            binding.tvItemPrice.text = String.format(
                Locale.US, "$%.2f", item.basePrice
            )

            if (item.imageUrl.isNotEmpty()) {
                binding.ivItemImage.load(item.imageUrl) {
                    crossfade(true)
                }
            }

            binding.cardMenuItem.setOnClickListener {
                onItemClick(item)
            }

            binding.btnCustomize.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class MenuItemDiffCallback : DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem) = oldItem == newItem
    }
}
