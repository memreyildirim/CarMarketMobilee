package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.Brand
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BrandService {
    @GET("api/v1/brands")
    suspend fun getBrands(): List<Brand>

    @POST("api/v1/brands/add")
    suspend fun addBrand(@Body brand: Brand) : retrofit2.Response<Brand>
}

