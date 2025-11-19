package com.emreyildirim.carmarketmobilee.model

data class UserCartAndFavsDto(
    val adminUserDto: AdminUserDto,
    val cartDto: CartDto,
    val favorites : List<CarDto>
)
