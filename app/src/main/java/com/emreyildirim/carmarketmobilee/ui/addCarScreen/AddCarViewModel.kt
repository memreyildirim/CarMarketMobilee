package com.emreyildirim.carmarketmobilee.ui.addCarScreen

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.Brand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AddCarViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(AddCarUiState())
    val uiState: StateFlow<AddCarUiState> = _uiState.asStateFlow()


    
    fun updateForm(field: String, value: Any) {
        println("DEBUG: updateForm called with field=$field, value=$value")
        _uiState.value = when (field) {
            "brandId" -> {
                println("DEBUG: Updating brandId to $value")
                _uiState.value.copy(id = value as Long)
            }
            "model" -> _uiState.value.copy(model = value as String)
            "carSpecification" -> _uiState.value.copy(carSpecification = value as String)
            "engineVolume" -> _uiState.value.copy(engineVolume = value as String)
            "isNew" -> _uiState.value.copy(isNew = value as Boolean)
            "price" -> _uiState.value.copy(price = value as String)

            "selectedImageUri" -> _uiState.value.copy(selectedImageUri = value as Uri?)
            else -> {
                println("DEBUG: Unknown field $field")
                _uiState.value
            }
        }
        println("DEBUG: New brandId = ${_uiState.value.id}")
    }
    
    fun loadBrands(context: Context) {
        viewModelScope.launch {
            try {
                val brandService = RetrofitInstance.getBrandService(context)
                val brands = brandService.getBrands()
                _uiState.value = _uiState.value.copy(brands = brands)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load brands: ${e.message}"
                )
            }
        }
    }
    
    fun createCar(context: Context) {
        if (!validateForm()) return
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val carService = RetrofitInstance.getCarService(context)
                
                // Convert image URI to MultipartBody.Part
                val photoPart = _uiState.value.selectedImageUri?.let { uri ->
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val file = File(context.cacheDir, "temp_photo.jpg")
                    val outputStream = FileOutputStream(file)
                    
                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                }
                
                if (photoPart == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Please select a photo"
                    )
                    return@launch
                }
                
                val response = carService.createCarWithPhoto(
                    brandId = _uiState.value.id,
                    model = _uiState.value.model.toRequestBody("text/plain".toMediaTypeOrNull()),
                    carSpecification = _uiState.value.carSpecification.toRequestBody("text/plain".toMediaTypeOrNull()),
                    engineVolume = _uiState.value.engineVolume.toFloatOrNull() ?: 0f,
                    isNew = _uiState.value.isNew,
                    price = _uiState.value.price.toBigDecimalOrNull() ?: BigDecimal.ZERO,
//                    releaseDatetime = "".toRequestBody("text/plain".toMediaTypeOrNull()), // Geçici olarak boş gönderiyoruz
                    photo = photoPart
                )


                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to create car: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    private fun validateForm(): Boolean {
        val state = _uiState.value
        val errors = mutableListOf<String>()
        
        if (state.id <= 0) errors.add("Please select a brand")
        if (state.model.isBlank()) errors.add("Model is required")
        if (state.carSpecification.isBlank()) errors.add("Car specification is required")
        if (state.engineVolume.isBlank() || state.engineVolume.toFloatOrNull() == null) {
            errors.add("Valid engine volume is required")
        }
        if (state.price.isBlank() || state.price.toBigDecimalOrNull() == null || state.price.toBigDecimalOrNull()!! <= BigDecimal.ZERO) {
            errors.add("Valid price is required")
        }
        // ReleaseDateTime is always valid since it's LocalDateTime
        if (state.selectedImageUri == null) errors.add("Photo is required")
        
        if (errors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(error = errors.joinToString(", "))
            return false
        }
        
        return true
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}

data class AddCarUiState(
    val id: Long = 5,
    val model: String = "",
    val carSpecification: String = "",
    val engineVolume: String = "",
    val isNew: Boolean = true,
    val price: String = "",
    val releaseDateTime: LocalDateTime = LocalDateTime.now(),
    val selectedImageUri: Uri? = null,
    val brands: List<Brand> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)