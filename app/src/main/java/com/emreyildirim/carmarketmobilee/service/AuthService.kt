package com.emreyildirim.carmarketmobilee.service


import com.emreyildirim.carmarketmobilee.model.JwtResponse

import com.emreyildirim.carmarketmobilee.model.LoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): JwtResponse
}