package com.emreyildirim.carmarketmobilee.ui.adminPanelScreen

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.AdminUserDto
import com.emreyildirim.carmarketmobilee.model.CarDto
import com.emreyildirim.carmarketmobilee.model.CartDto
import com.emreyildirim.carmarketmobilee.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminPanelViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AdminPanelUiState())
    val uiState: StateFlow<AdminPanelUiState> = _uiState.asStateFlow()

    private val _addAdminResult = MutableLiveData<Result<String>>()
    val addAdminResult: LiveData<Result<String>> = _addAdminResult


    fun refreshUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUsers = true, errorMessage = null) }
            try {
                val context = getApplication<Application>().applicationContext
                val adminService = RetrofitInstance.getAdminService(context)
                val users = adminService.getAllUsers()
                val previousSelectedId = _uiState.value.selectedUser?.id
                val selected = when {
                    users.isEmpty() -> null
                    previousSelectedId == null -> users.first()
                    else -> users.find { it.id == previousSelectedId } ?: users.first()
                }

                _uiState.update { state ->
                    state.copy(
                        users = users,
                        isLoadingUsers = false,
                        selectedUser = selected,
                        cart = if (selected?.id == previousSelectedId) state.cart else null,
                        favorites = if (selected?.id == previousSelectedId) state.favorites else emptyList()
                    )
                }

                selected?.let { fetchUserDetails(it.id, resetError = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingUsers = false,
                        errorMessage = e.message ?: "Kullanıcılar yüklenemedi"
                    )
                }
            }
        }
    }

    fun selectUser(userId: Long) {
        val user = _uiState.value.users.firstOrNull { it.id == userId } ?: return
        _uiState.update {
            it.copy(
                selectedUser = user,
                cart = null,
                favorites = emptyList(),
                errorMessage = null
            )
        }
        fetchUserDetails(userId, resetError = true)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun fetchUserDetails(userId: Long, resetError: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingDetails = true,
                    errorMessage = if (resetError) null else it.errorMessage
                )
            }

            try {
                val context = getApplication<Application>().applicationContext
                val adminService = RetrofitInstance.getAdminService(context)
                val response = adminService.getUserDetails(userId)

                val selected = _uiState.value.users.firstOrNull { it.id == userId }

                _uiState.update {
                    it.copy(
                        selectedUser = selected ?: it.selectedUser,
                        cart = response.cartDto,
                        favorites = response.favorites,
                        isLoadingDetails = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingDetails = false,
                        errorMessage = e.message ?: "Kullanıcı detayları yüklenemedi"
                    )
                }
            }
        }
    }

    fun deleteUser(userId: Long){
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val adminService = RetrofitInstance.getAdminService(context)

                adminService.deleteUser(userId)
                Log.d("AdminPanelViewModel", "Kullanıcı silindi")
                refreshUsers()
            }catch (e: Exception){
                Log.d("AdminPanelViewModel", "Hata: ${e.message}")
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Kullanıcı silinemedi") 
                }

            }
        }
    }

    fun addAdmin(username: String,password: String, email: String) {

        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val adminService = RetrofitInstance.getAdminService(context)
                val addAdminResponse = adminService.addAdmin(RegisterRequest(username,password,email))

                if (addAdminResponse.isSuccessful){
                    _addAdminResult.postValue(Result.success("Admin added succesfully"))
                    Log.d("AdminPanelViewModel", "Admin added succesfully")
                    refreshUsers()
                    Toast.makeText(context, "Admin added succesfully", Toast.LENGTH_SHORT).show()
                }else{
                    _addAdminResult.postValue(Result.failure(Exception("Failed to add admin")))
                    Log.e("AdminPanelViewModel", "Failed to add admin")
                }

            }catch (e: Exception){
                println("DEBUG: Error: ${e.message}")
            }
        }
    }
}

data class AdminPanelUiState(
    val users: List<AdminUserDto> = emptyList(),
    val selectedUser: AdminUserDto? = null,
    val cart: CartDto? = null,
    val favorites: List<CarDto> = emptyList(),
    val isLoadingUsers: Boolean = false,
    val isLoadingDetails: Boolean = false,
    val errorMessage: String? = null
)