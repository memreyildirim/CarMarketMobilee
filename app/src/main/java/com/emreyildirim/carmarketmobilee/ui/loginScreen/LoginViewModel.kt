package com.emreyildirim.carmarketmobilee.ui.loginScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    fun login(email: String , password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.login(LoginRequest(email, password))
                _loginResult.postValue(Result.success(response.token))
            } catch (e : Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }

}