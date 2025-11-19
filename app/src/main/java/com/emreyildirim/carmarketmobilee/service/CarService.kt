package com.emreyildirim.carmarketmobilee.service

import com.emreyildirim.carmarketmobilee.model.CarDetailDto
import com.emreyildirim.carmarketmobilee.model.CarDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal
import java.time.LocalDateTime

interface CarService {
    @GET("api/v1/cars")
    suspend fun getCars(): List<CarDto>

    @GET("api/v1/cars/{id}")
    suspend fun getCarById(@Path("id") id: Long): CarDetailDto

    @PUT("api/v1/cars/{id}")
    suspend fun updateCarById(@Path("id") id: Long, @Body carDetailDto: CarDetailDto): CarDto

    @Multipart
    @POST("api/v1/cars")
    suspend fun createCarWithPhoto(
        @Part("brandId") brandId: Long,
        @Part("model") model: RequestBody,
        @Part("carSpecification") carSpecification: RequestBody,
        @Part("engineVolume") engineVolume: Float,
        @Part("isNew") isNew: Boolean,
        @Part("price") price: BigDecimal,
        @Part("releaseDatetime") releaseDatetime: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @POST("api/v1/cars/timeless")
    suspend fun createCarWithPhoto(
        @Part("brandId") brandId: Long,
        @Part("model") model: RequestBody,
        @Part("carSpecification") carSpecification: RequestBody,
        @Part("engineVolume") engineVolume: Float,
        @Part("isNew") isNew: Boolean,
        @Part("price") price: BigDecimal,
        @Part photo: MultipartBody.Part
    ): Response<Unit>

    @DELETE("api/v1/cars/{carId}")
    suspend fun deleteCar(@Path("carId")  carId:Long)
}