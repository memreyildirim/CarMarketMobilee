package com.emreyildirim.carmarketmobilee.ui.cartScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CartDto
import com.emreyildirim.carmarketmobilee.model.CartItemDto
import com.emreyildirim.carmarketmobilee.model.CartItemRequest
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val _cart = MutableLiveData<CartDto?>()
    val cart: LiveData<CartDto?> = _cart

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadCart() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val context = getApplication<Application>().applicationContext
                val cartService = RetrofitInstance.getCartService(context)
                val response = cartService.getCart()
                
                Log.d("CartViewModel", "Cart loaded: $response")
                Log.d("CartViewModel", "Cart items count: ${response.items.size}")
                response.items.forEachIndexed { index, item ->
                    Log.d("CartViewModel", "Item $index: carId=${item.carId}, car=${item.car}")
                    if (item.car == null) {
                        Log.w("CartViewModel", "Item $index has null car field!")
                    }
                }
                _cart.value = response
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error loading cart: ${e.message}", e)
                _error.value = e.message ?: "Unknown error"
                _cart.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFromCart(carId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val context = getApplication<Application>().applicationContext
                val cartService = RetrofitInstance.getCartService(context)
                cartService.deleteFromCart(carId)

                // Silme sonrası sepeti yenile
                val response = cartService.getCart()
                _cart.value = response
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error deleting item: ${e.message}", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val context =getApplication<Application>().applicationContext
                val cartService = RetrofitInstance.getCartService(context)
                cartService.clearCart()

                // Temizleme sonrası sepeti yenile
                val response = cartService.getCart()
                _cart.value = response
            }catch (e: Exception){
                Log.e("CartViewModel", "Error clearing cart: ${e.message}", e)
                _error.value = e.message ?: "Unknown error"
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun decreaseQuantity(item: CartItemDto){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val context = getApplication<Application>().applicationContext
                val cartService = RetrofitInstance.getCartService(context)

                val newQuantity = (item.quantity - 1).coerceAtLeast(0)
                if (newQuantity == 0){
                    cartService.deleteFromCart(item.carId)
                }else{
                    cartService.updateQuantity(item.carId, newQuantity)
                }

                val updated = cartService.updateQuantity(item.carId, newQuantity)
                _cart.value = updated
            }catch (e: Exception){
                _error.value = e.message ?: "Unknown error"
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun increaseQuantity(item : CartItemDto){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val context = getApplication<Application>().applicationContext
                val cartService = RetrofitInstance.getCartService(context)



                val updated = cartService.updateQuantity(item.carId, item.quantity + 1)
                _cart.value = updated
            }catch (e: Exception){
                _error.value = e.message ?: "Unknown error"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshCart() {
        loadCart()
    }
}
