package com.example.brewkery.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.brewkery.R
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.databinding.FragmentItemDetailBinding
import com.example.brewkery.ui.MainViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class ItemDetailFragment : Fragment() {

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private var currentMenuItem: MenuItem? = null
    private var selectedSize: SizeOption? = null
    private var selectedMilk: MilkOption? = null
    private var selectedSugar: String? = null
    private var quantity: Int = 1
    private var isFavorite: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                binding.ivFavorite.setImageResource(R.drawable.ic_heart)
                binding.ivFavorite.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_brand))
                Toast.makeText(requireContext(), "Saved to Favorites", Toast.LENGTH_SHORT).show()
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_heart)
                binding.ivFavorite.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updatePriceAndQuantity()
            }
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            updatePriceAndQuantity()
        }

        binding.btnAddToCart.setOnClickListener {
            val item = currentMenuItem ?: return@setOnClickListener
            viewModel.addToCart(
                item = item,
                selectedSize = selectedSize,
                selectedMilk = selectedMilk,
                selectedSugar = selectedSugar,
                quantity = quantity
            )
            Toast.makeText(requireContext(), "Added ${item.name} to Cart!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedMenuItem.collectLatest { item ->
                if (item != null) {
                    currentMenuItem = item
                    bindMenuItem(item)
                }
            }
        }
    }

    private fun bindMenuItem(item: MenuItem) {
        binding.tvDetailName.text = item.name
        binding.tvDetailDescription.text = item.description

        if (!item.badge.isNullOrEmpty()) {
            binding.tvDetailBadge.visibility = View.VISIBLE
            binding.tvDetailBadge.text = item.badge
        } else {
            binding.tvDetailBadge.visibility = View.GONE
        }

        if (item.imageUrl.isNotEmpty()) {
            binding.ivDetailImage.load(item.imageUrl) {
                crossfade(true)
            }
        }

        // Setup Key Ingredients
        binding.chipGroupIngredients.removeAllViews()
        val ingredients = item.ingredients
        if (!ingredients.isNullOrEmpty()) {
            ingredients.forEach { ingredient ->
                val chip = Chip(requireContext()).apply {
                    text = ingredient
                    setChipBackgroundColorResource(R.color.chip_bg)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    isCheckable = false
                    isClickable = false
                    textSize = 12f
                }
                binding.chipGroupIngredients.addView(chip)
            }
        }

        // Setup Customizations
        val cust = item.customizations

        // Sizes
        binding.containerSizes.removeAllViews()
        val sizes = cust?.sizes ?: emptyList()
        if (sizes.isNotEmpty()) {
            selectedSize = sizes.first()
            renderSizeOptions(sizes)
        } else {
            selectedSize = null
        }

        // Milk Options
        binding.containerMilk.removeAllViews()
        val milks = cust?.milkOptions ?: emptyList()
        if (milks.isNotEmpty()) {
            selectedMilk = milks.first()
            renderMilkOptions(milks)
        } else {
            selectedMilk = null
        }

        // Sugar Levels
        binding.containerSugar.removeAllViews()
        val sugars = cust?.sugarLevels ?: emptyList()
        if (sugars.isNotEmpty()) {
            selectedSugar = sugars.first()
            renderSugarOptions(sugars)
        } else {
            selectedSugar = null
        }

        updatePriceAndQuantity()
    }

    private fun renderSizeOptions(sizes: List<SizeOption>) {
        binding.containerSizes.removeAllViews()
        sizes.forEach { sizeOption ->
            val isSelected = (selectedSize?.id == sizeOption.id)

            val card = MaterialCardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(0, 0, 8, 0)
                }
                radius = 24f
                strokeWidth = if (isSelected) 3 else 2
                strokeColor = ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) R.color.primary_brand else R.color.stroke_color
                )
                setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isSelected) R.color.primary_brand_light else R.color.white
                    )
                )

                val layout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 16)

                    val tvLabel = TextView(requireContext()).apply {
                        text = sizeOption.label
                        textSize = 12f
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }

                    val extraText = if (sizeOption.extraPrice > 0) {
                        String.format(Locale.US, "+$%.2f", sizeOption.extraPrice)
                    } else {
                        "+$0.00"
                    }

                    val tvPrice = TextView(requireContext()).apply {
                        text = extraText
                        textSize = 11f
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                    }

                    addView(tvLabel)
                    addView(tvPrice)
                }

                addView(layout)

                setOnClickListener {
                    selectedSize = sizeOption
                    renderSizeOptions(sizes)
                    updatePriceAndQuantity()
                }
            }

            binding.containerSizes.addView(card)
        }
    }

    private fun renderMilkOptions(milks: List<MilkOption>) {
        binding.containerMilk.removeAllViews()
        milks.forEach { milkOption ->
            val isSelected = (selectedMilk?.id == milkOption.id)

            val card = MaterialCardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 8)
                }
                radius = 24f
                strokeWidth = if (isSelected) 3 else 2
                strokeColor = ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) R.color.primary_brand else R.color.stroke_color
                )
                setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isSelected) R.color.primary_brand_light else R.color.white
                    )
                )

                val layout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(16, 14, 16, 14)

                    val tvName = TextView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        text = milkOption.name
                        textSize = 13f
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (isSelected) R.color.primary_brand else R.color.text_primary
                            )
                        )
                        typeface = if (isSelected) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                    }

                    val extraText = if (milkOption.extraPrice > 0) {
                        String.format(Locale.US, "+$%.2f", milkOption.extraPrice)
                    } else {
                        "+$0.00"
                    }

                    val tvPrice = TextView(requireContext()).apply {
                        text = extraText
                        textSize = 12f
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (isSelected) R.color.primary_brand else R.color.text_secondary
                            )
                        )
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }

                    addView(tvName)
                    addView(tvPrice)
                }

                addView(layout)

                setOnClickListener {
                    selectedMilk = milkOption
                    renderMilkOptions(milks)
                    updatePriceAndQuantity()
                }
            }

            binding.containerMilk.addView(card)
        }
    }

    private fun renderSugarOptions(sugars: List<String>) {
        binding.containerSugar.removeAllViews()
        sugars.forEach { sugarLevel ->
            val isSelected = (selectedSugar == sugarLevel)

            val card = MaterialCardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(0, 0, 6, 0)
                }
                radius = 24f
                strokeWidth = 0
                setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isSelected) R.color.chip_selected_bg else R.color.chip_bg
                    )
                )

                val tv = TextView(requireContext()).apply {
                    setPadding(8, 12, 8, 12)
                    text = sugarLevel
                    textSize = 11f
                    gravity = android.view.Gravity.CENTER
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isSelected) R.color.chip_selected_text else R.color.text_primary
                        )
                    )
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                addView(tv)

                setOnClickListener {
                    selectedSugar = sugarLevel
                    renderSugarOptions(sugars)
                    updatePriceAndQuantity()
                }
            }

            binding.containerSugar.addView(card)
        }
    }

    private fun updatePriceAndQuantity() {
        val base = currentMenuItem?.basePrice ?: 0.0
        val sizeExtra = selectedSize?.extraPrice ?: 0.0
        val milkExtra = selectedMilk?.extraPrice ?: 0.0

        val unitPrice = base + sizeExtra + milkExtra
        val totalPrice = unitPrice * quantity

        binding.tvQuantity.text = quantity.toString()
        binding.tvDetailPrice.text = String.format(Locale.US, "$%.2f", unitPrice)
        binding.btnAddToCart.text = String.format(Locale.US, "Add to Cart • $%.2f", totalPrice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
