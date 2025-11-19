package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.CarDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Headers
import retrofit2.http.Query

interface FavoriteService {
    @GET("api/v1/favorites")
    suspend fun getFavorites(): List<CarDto>

    @Headers("Content-Length: 0")
    @POST("api/v1/favorites")
    suspend fun addFavorite(@Query("carId") carId: Long)

    @DELETE("api/v1/favorites/{carId}")
    suspend fun removeFavorite(@Path("carId") carId: Long)
}


