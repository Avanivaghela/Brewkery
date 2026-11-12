package com.example.brewkery.ui.menu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.brewkery.data.model.Category
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.databinding.FragmentMenuBinding
import com.example.brewkery.ui.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var menuItemAdapter: MenuItemAdapter

    private var allItems: List<MenuItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { categoryId ->
            viewModel.selectCategory(categoryId)
        }
        binding.rvCategories.adapter = categoryAdapter

        menuItemAdapter = MenuItemAdapter { menuItem ->
            viewModel.selectMenuItem(menuItem)
            (activity as? MenuNavigationListener)?.navigateToDetail()
        }
        binding.rvMenuItems.adapter = menuItemAdapter
    }

    private fun setupListeners() {
        binding.cartIconContainer.setOnClickListener {
            (activity as? MenuNavigationListener)?.navigateToCart()
        }

        binding.btnTrackOrder.setOnClickListener {
            (activity as? MenuNavigationListener)?.navigateToOrderStatus()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.loadMenu()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is MenuUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.layoutError.visibility = View.GONE
                        binding.rvMenuItems.visibility = View.GONE
                    }
                    is MenuUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.layoutError.visibility = View.GONE
                        binding.rvMenuItems.visibility = View.VISIBLE

                        val menu = state.menu
                        allItems = menu.items

                        val fullCategories = mutableListOf(
                            Category(id = "ALL", name = "All Items", icon = "")
                        )
                        fullCategories.addAll(menu.categories)
                        categoryAdapter.submitList(fullCategories)

                        updateFilteredItems()
                    }
                    is MenuUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvMenuItems.visibility = View.GONE
                        binding.layoutError.visibility = View.VISIBLE
                        binding.tvErrorMessage.text = state.message
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCategoryId.collectLatest { categoryId ->
                categoryAdapter.setSelectedCategory(categoryId)
                updateFilteredItems()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchQuery.collectLatest {
                updateFilteredItems()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collectLatest { items ->
                val count = items.sumOf { it.quantity }
                if (count > 0) {
                    binding.tvCartBadge.visibility = View.VISIBLE
                    binding.tvCartBadge.text = count.toString()
                } else {
                    binding.tvCartBadge.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeOrder.collectLatest { order ->
                if (order != null) {
                    binding.cardActiveOrder.visibility = View.VISIBLE
                    binding.tvActiveOrderId.text = "Active Order ${order.ticketId}"
                    binding.tvActiveOrderStatus.text = "${order.status.lowercase().replaceFirstChar { it.uppercase() }} (Arriving in ${order.estimatedWait})"
                } else {
                    binding.cardActiveOrder.visibility = View.GONE
                }
            }
        }
    }

    private fun updateFilteredItems() {
        val catId = viewModel.selectedCategoryId.value
        val query = viewModel.searchQuery.value.trim().lowercase()

        val filtered = allItems.filter { item ->
            val matchesCategory = (catId == "ALL" || item.categoryId == catId)
            val matchesQuery = query.isEmpty() ||
                    item.name.lowercase().contains(query) ||
                    item.tagline.lowercase().contains(query) ||
                    item.description.lowercase().contains(query) ||
                    (item.ingredients?.any { it.lowercase().contains(query) } == true)
            matchesCategory && matchesQuery
        }
        menuItemAdapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface MenuNavigationListener {
        fun navigateToDetail()
        fun navigateToCart()
        fun navigateToOrderStatus()
    }
}
