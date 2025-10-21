package com.emreyildirim.carmarketmobilee.ui.homeScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CarDto
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _cars = MutableLiveData<List<CarDto>>()
    val cars: LiveData<List<CarDto>> = _cars
    fun loadCars() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val carService = RetrofitInstance.getCarService(context)
                val response = carService.getCars()
                _cars.postValue(response)
            }catch (e: Exception){
                Log.e("HomeViewModel", "Arabalar yüklenmedi", e)
            }
        }
    }

    /*
    private val _profile = MutableLiveData<UserProfile?>()
    val profile: LiveData<UserProfile?> = _profile

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val response = RetrofitInstance.getUserService(context).getProfile()
                _profile.postValue(response)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Profil yüklenemedi", e)
            }
        }
    }
     */
}