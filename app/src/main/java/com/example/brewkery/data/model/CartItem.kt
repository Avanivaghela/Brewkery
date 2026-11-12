package com.example.brewkery.data.model

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val menuItem: MenuItem,
    val selectedSize: SizeOption?,
    val selectedMilk: MilkOption?,
    val selectedSugar: String?,
    var quantity: Int = 1
) {
    val unitPrice: Double
        get() {
            val sizePrice = selectedSize?.extraPrice ?: 0.0
            val milkPrice = selectedMilk?.extraPrice ?: 0.0
            return menuItem.basePrice + sizePrice + milkPrice
        }

    val totalPrice: Double
        get() = unitPrice * quantity
}
