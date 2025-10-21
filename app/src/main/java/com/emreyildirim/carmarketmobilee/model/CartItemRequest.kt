package com.emreyildirim.carmarketmobilee.model

data class CartItemRequest(
    val carId: Long,
    val quantity: Int = 1
)
