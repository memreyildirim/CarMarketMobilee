package com.emreyildirim.carmarketmobilee.ui.favoritesScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CarDto
import com.emreyildirim.carmarketmobilee.utils.UserSession
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val _favorites = MutableLiveData<List<CarDto>>(emptyList())
    val favorites: LiveData<List<CarDto>> = _favorites

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadFavorites() {
        viewModelScope.launch {
            try {

                _isLoading.value = true
                val context = getApplication<Application>().applicationContext
                val service = RetrofitInstance.getFavoriteService(context)
                val list = service.getFavorites()
                _favorites.postValue(list)
                _error.postValue(null)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}


