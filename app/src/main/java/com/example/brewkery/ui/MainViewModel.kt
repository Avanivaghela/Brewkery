package com.example.brewkery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.model.CartItem
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MenuResponse
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.OrderTicket
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.data.model.StoreMeta
import com.example.brewkery.data.remote.RetrofitClient
import com.example.brewkery.data.repository.MenuRepository
import com.example.brewkery.ui.menu. MenuUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private val repository = MenuRepository(RetrofitClient.api)

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private val _storeMeta = MutableStateFlow<StoreMeta?>(null)
    val storeMeta: StateFlow<StoreMeta?> = _storeMeta.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow("ALL")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMenuItem = MutableStateFlow<MenuItem?>(null)
    val selectedMenuItem: StateFlow<MenuItem?> = _selectedMenuItem.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _activeOrder = MutableStateFlow<OrderTicket?>(null)
    val activeOrder: StateFlow<OrderTicket?> = _activeOrder.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            _uiState.value = MenuUiState.Loading
            try {
                val result = repository.getMenu()
                _storeMeta.value = result.meta
                _uiState.value = MenuUiState.Success(result)
            } catch (e: IOException) {
                _uiState.value = MenuUiState.Error("Please check your internet connection.")
            } catch (e: Exception) {
                _uiState.value = MenuUiState.Error(e.message ?: "Something went wrong.")
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectMenuItem(item: MenuItem?) {
        _selectedMenuItem.value = item
    }

    fun addToCart(
        item: MenuItem,
        selectedSize: SizeOption?,
        selectedMilk: MilkOption?,
        selectedSugar: String?,
        quantity: Int
    ) {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.menuItem.id == item.id &&
                    it.selectedSize?.id == selectedSize?.id &&
                    it.selectedMilk?.id == selectedMilk?.id &&
                    it.selectedSugar == selectedSugar
        }

        if (existingIndex != -1) {
            val existingItem = currentList[existingIndex]
            currentList[existingIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentList.add(
                CartItem(
                    menuItem = item,
                    selectedSize = selectedSize,
                    selectedMilk = selectedMilk,
                    selectedSugar = selectedSugar,
                    quantity = quantity
                )
            )
        }
        _cartItems.value = currentList
    }

    fun updateCartQuantity(cartItemId: String, delta: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == cartItemId }
        if (index != -1) {
            val item = currentList[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = item.copy(quantity = newQty)
            }
            _cartItems.value = currentList
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun placeOrder(): OrderTicket? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val subtotal = items.sumOf { it.totalPrice }
        val meta = _storeMeta.value
        val deliveryFee = meta?.deliveryFee ?: 2.50
        val taxRate = (meta?.taxRatePercent ?: 8.0) / 100.0
        val tax = subtotal * taxRate
        val total = subtotal + deliveryFee + tax

        val randomNum = Random.nextInt(51000, 59999)
        val ticketId = "#BK-$randomNum"

        val order = OrderTicket(
            ticketId = ticketId,
            status = "PREPARING",
            statusMessage = "Barista accepted your order!",
            estimatedWait = meta?.estimatedDeliveryTime ?: "20 – 30 minutes",
            itemCount = items.sumOf { it.quantity },
            itemsSummary = items,
            totalAmount = total
        )

        _activeOrder.value = order
        clearCart()
        return order
    }

    val cartCount: Int
        get() = _cartItems.value.sumOf { it.quantity }
}
