package com.emreyildirim.carmarketmobilee.model

data class CartDto(
    val id: Long,
    val userId: Long,
    val items: List<CartItemDto>,
    val totalPrice: Double
)

