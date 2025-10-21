package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.CartDto
import com.emreyildirim.carmarketmobilee.model.CartItemRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartService {
    @GET("api/v1/cart")
    suspend fun getCart(): CartDto
    
    @POST("api/v1/cart/items")
    suspend fun addToCart(@Body request: CartItemRequest)

    @DELETE("api/v1/cart/items/{carId}")
    suspend fun deleteFromCart(@Path("carId") carId: Long)

    @DELETE("api/v1/cart")
    suspend fun clearCart()
}