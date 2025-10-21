package com.emreyildirim.carmarketmobilee.model

import java.math.BigDecimal

data class CarDto(
    val carId: Long,
    val brandName: String,
    val model: String,
    val price: BigDecimal,
    val isNew: Boolean,
    val filePath: String
)
