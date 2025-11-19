package com.emreyildirim.carmarketmobilee.ui.detailScreen

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CarDetailDto
import com.emreyildirim.carmarketmobilee.model.CartItemRequest
import com.emreyildirim.carmarketmobilee.service.FavoriteService
import com.emreyildirim.carmarketmobilee.utils.isJwtExpired
import com.emreyildirim.carmarketmobilee.utils.UserSession
import kotlinx.coroutines.launch

class DetailViewModel(
    application: Application
    ) : AndroidViewModel(application){

    private val _carDetail = MutableLiveData<CarDetailDto?>()
    val carDetail: LiveData<CarDetailDto?> = _carDetail

    private val _addToCartResult = MutableLiveData<Result<Unit>>()
    val addToCartResult: LiveData<Result<Unit>> = _addToCartResult

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _favoriteActionResult = MutableLiveData<Result<Unit>>()
    val favoriteActionResult: LiveData<Result<Unit>> = _favoriteActionResult

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

     fun loadCarDetail(id: Long) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val service = RetrofitInstance.getCarService(context)
                val response = service.getCarById(id)
                Log.d("DetailViewModel", "Response: $response")
                _carDetail.postValue(response)
            } catch (e: Exception) {
                _carDetail.postValue(null)
            }
        }
    }

    fun addToCart(carId: Long, quantity: Int = 1){
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt", null)
                Log.d("DetailViewModel", "JWT Token: $token")
                Log.d("DetailViewModel", "CarId: $carId, Quantity: $quantity")
                
                val cartService = RetrofitInstance.getCartService(context)
                val request = CartItemRequest(carId = carId, quantity = quantity)
                Log.d("DetailViewModel", "Request: $request")
                
                cartService.addToCart(request)
                Log.d("DetailViewModel", "Add to cart successful")
                _addToCartResult.postValue(Result.success(Unit))
            }catch (e: Exception){
                Log.e("DetailViewModel", "Add to cart error: ${e.message}", e)
                _addToCartResult.postValue(Result.failure(e))
            }
        }
    }

    fun deleteCar(carId: Long){
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val carService = RetrofitInstance.getCarService(context)

                carService.deleteCar(carId)
                Log.d("DetailViewModel", "Delete car successful")
                _deleteResult.postValue(Result.success(Unit))

            }catch (e: Exception){
                Log.d("DetailViewModel", "Delete car error: ${e.message}")
                _deleteResult.postValue(Result.failure(e))
            }
        }
    }

    fun addFavorite(carId: Long) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt", null)
                val roles = prefs.getStringSet("role", emptySet()) ?: emptySet()
                if (token.isNullOrBlank() || isJwtExpired(token)) {
                    throw IllegalStateException("Session expired. Please login again.")
                }
                if (!roles.contains("USER")) {
                    throw IllegalStateException("Only USER can add favorites")
                }
                val favoriteService: FavoriteService = RetrofitInstance.getFavoriteService(context)
                Log.d("DetailViewModel", "addFavorite carId=$carId")
                favoriteService.addFavorite(carId)
                _favoriteActionResult.postValue(Result.success(Unit))
                _isFavorite.postValue(true)
            } catch (e: Exception) {
                _favoriteActionResult.postValue(Result.failure(e))
            }
        }
    }

    fun removeFavorite(carId: Long) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt", null)
                val roles = prefs.getStringSet("role", emptySet()) ?: emptySet()
                if (token.isNullOrBlank() || isJwtExpired(token)) {
                    throw IllegalStateException("Session expired. Please login again.")
                }
                if (!roles.contains("USER")) {
                    throw IllegalStateException("Only USER can remove favorites")
                }
                val favoriteService: FavoriteService = RetrofitInstance.getFavoriteService(context)
                Log.d("DetailViewModel", "removeFavorite carId=$carId")
                favoriteService.removeFavorite(carId)
                _favoriteActionResult.postValue(Result.success(Unit))
                _isFavorite.postValue(false)
            } catch (e: Exception) {
                _favoriteActionResult.postValue(Result.failure(e))
            }
        }
    }

    fun loadIsFavorite(carId: Long) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val favoriteService: FavoriteService = RetrofitInstance.getFavoriteService(context)
                val favorites = favoriteService.getFavorites()
                val isFav = favorites.any { it.carId == carId }
                _isFavorite.postValue(isFav)
            } catch (e: Exception) {
                // default to false on error
                _isFavorite.postValue(false)
            }
        }
    }

}