package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.AdminUserDto
import com.emreyildirim.carmarketmobilee.model.UserCartAndFavsDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface AdminService {

    @GET("api/v1/admin/users")
    suspend fun getAllUsers(): List<AdminUserDto>

    @GET("api/v1/admin/users/{userId}/details")
    suspend fun  getUserDetails(@Path("userId") userId: Long): UserCartAndFavsDto

    @DELETE("api/v1/admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

}