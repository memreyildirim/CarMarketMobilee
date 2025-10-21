package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.CarDetailDto
import com.emreyildirim.carmarketmobilee.model.CarDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CarService {
    @GET("api/v1/cars")
    suspend fun getCars(): List<CarDto>

    @GET("api/v1/cars/{id}")
    suspend fun getCarById(@Path("id") id: Long): CarDetailDto
}