package com.example.brewkery.data.model

data class OrderTicket(
    val ticketId: String = "#BK-51506",
    val status: String = "PREPARING",
    val statusMessage: String = "Barista accepted your order!",
    val estimatedWait: String = "20 – 30 minutes",
    val itemCount: Int,
    val itemsSummary: List<CartItem>,
    val totalAmount: Double
)
