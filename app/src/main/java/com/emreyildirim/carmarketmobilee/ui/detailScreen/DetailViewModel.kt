package com.emreyildirim.carmarketmobilee.ui.detailScreen

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CarDetailDto
import com.emreyildirim.carmarketmobilee.model.CartItemRequest
import kotlinx.coroutines.launch

class DetailViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application){

    private val _carDetail = MutableLiveData<CarDetailDto?>()
    val carDetail: LiveData<CarDetailDto?> = _carDetail

    private val _addToCartResult = MutableLiveData<Result<Unit>>()
    val addToCartResult: LiveData<Result<Unit>> = _addToCartResult

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

}