package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.UserProfile
import retrofit2.http.GET

interface UserService {
    @GET("api/v1/profile")
    suspend fun getProfile(): UserProfile
}


