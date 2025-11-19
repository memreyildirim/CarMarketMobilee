package com.emreyildirim.carmarketmobilee.model

import java.math.BigDecimal

data class CarDetailDto(
    val carId: Long,
    val brand: Brand?,
    val brandName: String,
    val model: String,
    val price: BigDecimal,
    val isNew: Boolean,
    val carSpecification: String,
    val engineVolume: Float,
    val releaseDatetime: String?,
    val filePath: String
)

