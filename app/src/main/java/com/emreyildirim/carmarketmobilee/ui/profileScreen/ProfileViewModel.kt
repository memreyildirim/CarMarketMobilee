package com.emreyildirim.carmarketmobilee.ui.profileScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.UserProfile
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val _profile = MutableLiveData<UserProfile>()
    val profile : LiveData<UserProfile> = _profile

    fun loadProfile(){
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val profileService = RetrofitInstance.getUserService(context)
                val response = profileService.getProfile()
                _profile.postValue(response)
                Log.d("ProfileViewModel", "Profile loaded: ${response.username}")
            }catch (e: Exception){
                Log.e( "ProfileViewModel", "Error loading profile", e);
            }
        }
    }
}