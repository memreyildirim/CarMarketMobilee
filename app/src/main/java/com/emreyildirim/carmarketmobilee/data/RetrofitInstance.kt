package com.emreyildirim.carmarketmobilee.data

import android.content.Context
import com.emreyildirim.carmarketmobilee.service.AuthService
import com.emreyildirim.carmarketmobilee.service.CarService
import com.emreyildirim.carmarketmobilee.service.CartService
import com.emreyildirim.carmarketmobilee.service.UserService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://sortably-nonaffiliating-my.ngrok-free.dev/"

    fun buildAbsoluteUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        val trimmedBase = BASE_URL.trimEnd('/')
        return if (path.startsWith("http://") || path.startsWith("https://")) {
            path
        } else {
            val normalizedPath = if (path.startsWith('/')) path else "/$path"
            trimmedBase + normalizedPath
        }
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    private fun createAuthorizedClient(context: Context): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt", null)

            if (token.isNullOrBlank()) {
                return@Interceptor chain.proceed(originalRequest)
            }

            val authorized = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(authorized)
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    fun getUserService(context: Context): UserService {
        val client = createAuthorizedClient(context)
        val authedRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return authedRetrofit.create(UserService::class.java)
    }

    fun getCarService(context: Context): CarService{
        val client = createAuthorizedClient(context)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarService::class.java)
    }

    fun getCartService(context: Context): CartService {
        val client = createAuthorizedClient(context)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CartService::class.java)
    }
}