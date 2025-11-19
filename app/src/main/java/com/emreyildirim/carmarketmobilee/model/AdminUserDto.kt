package com.emreyildirim.carmarketmobilee.model

data class AdminUserDto(
    val id : Long,
    val username: String,
    val email : String?,
    val role : String

)
