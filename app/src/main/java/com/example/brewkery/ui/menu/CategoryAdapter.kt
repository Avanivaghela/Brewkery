package com.example.brewkery.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.brewkery.R
import com.example.brewkery.data.model.Category
import com.example.brewkery.databinding.ItemCategoryChipBinding

class CategoryAdapter(
    private val onCategorySelected: (String) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedCategoryId: String = "ALL"

    fun setSelectedCategory(categoryId: String) {
        if (selectedCategoryId != categoryId) {
            selectedCategoryId = categoryId
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryChipBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            val context = binding.root.context
            val isSelected = category.id == selectedCategoryId

            val displayText = if (category.icon.isNotEmpty()) {
                "${category.icon} ${category.name}"
            } else {
                category.name
            }
            binding.tvCategoryName.text = displayText

            if (isSelected) {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.chip_selected_bg)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(context, R.color.chip_selected_text)
                )
            } else {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.chip_bg)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(context, R.color.text_primary)
                )
            }

            binding.cardCategory.setOnClickListener {
                onCategorySelected(category.id)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}
