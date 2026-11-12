package com.example.brewkery.data.model

import com.google.gson.annotations.SerializedName

data class MenuResponse(
    val meta: StoreMeta,
    val categories: List<Category>,
    val items: List<MenuItem>
)

data class StoreMeta(
    val app: String = "Brewkery",
    val version: String = "1.0.0",
    val tagline: String = "Artisanal Coffee & Fresh Oven Bakes",
    val currency: String = "USD",
    @SerializedName("currency_symbol")
    val currencySymbol: String = "$",
    @SerializedName("delivery_fee")
    val deliveryFee: Double = 2.50,
    @SerializedName("tax_rate_percent")
    val taxRatePercent: Double = 8.0,
    @SerializedName("estimated_delivery_time")
    val estimatedDeliveryTime: String = "20 - 30 mins"
)

data class Category(
    val id: String,
    val name: String,
    val icon: String = "",
    @SerializedName("item_count")
    val itemCount: Int = 0
)

data class MenuItem(
    val id: Int,
    @SerializedName("category_id")
    val categoryId: String = "",
    val name: String,
    val tagline: String = "",
    val description: String = "",
    @SerializedName("base_price")
    val basePrice: Double,
    val rating: Double = 0.0,
    @SerializedName("review_count")
    val reviewCount: Int = 0,
    @SerializedName("prep_time")
    val prepTime: String = "",
    val calories: Int = 0,
    @SerializedName("image_url")
    val imageUrl: String = "",
    val badge: String? = null,
    val ingredients: List<String>? = emptyList(),
    val customizations: Customizations? = null
)

data class Customizations(
    val sizes: List<SizeOption>? = emptyList(),
    @SerializedName("sugar_levels")
    val sugarLevels: List<String>? = emptyList(),
    @SerializedName("milk_options")
    val milkOptions: List<MilkOption>? = emptyList()
)

data class SizeOption(
    val id: String,
    val label: String,
    @SerializedName("extra_price")
    val extraPrice: Double = 0.0
)

data class MilkOption(
    val id: String,
    val name: String,
    @SerializedName("extra_price")
    val extraPrice: Double = 0.0
)
