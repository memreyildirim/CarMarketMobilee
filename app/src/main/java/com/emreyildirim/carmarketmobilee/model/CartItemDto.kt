package com.emreyildirim.carmarketmobilee.model

data class CartItemDto(
    val id: Long,
    val carId: Long,
    val quantity: Int,
    val car: CarDto?
)

