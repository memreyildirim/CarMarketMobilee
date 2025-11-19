package com.emreyildirim.carmarketmobilee.ui.registerScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.LoginRequest
import com.emreyildirim.carmarketmobilee.model.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {



    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val registerResponse = RetrofitInstance.authService.register(RegisterRequest(username, email, password))

                if (registerResponse.isSuccessful) {
                    try {
                        val loginResponse = RetrofitInstance.authService.login(LoginRequest(email,password))
                        _registerResult.postValue(Result.success(loginResponse.token))
                    }catch (e: Exception){
                        _registerResult.postValue(Result.failure(e))
                        Log.e("RegisterViewModel", "Error logging in after registration", e)
                    }
                } else {
                    val errorBody = registerResponse.errorBody()?.string() ?: "Registration failed"
                    _registerResult.postValue(Result.failure(Exception(errorBody)))
                    Log.e("RegisterViewModel", "Error registering user1: $errorBody ")
                }
            }catch (e: Exception){
                _registerResult.postValue(Result.failure(e))
                Log.e("RegisterViewModel", "Error registering user", e)

            }
        }
    }



}
