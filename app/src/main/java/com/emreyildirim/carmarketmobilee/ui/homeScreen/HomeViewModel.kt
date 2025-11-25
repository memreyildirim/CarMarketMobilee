package com.emreyildirim.carmarketmobilee.ui.homeScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.emreyildirim.carmarketmobilee.data.CarPagingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Refresh trigger - her değiştiğinde yeni Pager oluşturulur
    private val refreshTrigger = MutableStateFlow(0)

    val carPagingFlow = refreshTrigger.flatMapLatest {
        Pager(
            config = PagingConfig(pageSize = 10,
                initialLoadSize =  10,
                prefetchDistance = 2),
            pagingSourceFactory = {
                val context = getApplication<Application>().applicationContext
                val service = RetrofitInstance.getCarService(context)
                CarPagingSource(service)
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun refresh() {
        refreshTrigger.value += 1
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
