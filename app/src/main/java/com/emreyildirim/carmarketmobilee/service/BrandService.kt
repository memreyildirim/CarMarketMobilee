package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.Brand
import retrofit2.http.GET

interface BrandService {
    @GET("api/v1/brands")
    suspend fun getBrands(): List<Brand>
}

