package com.emreyildirim.carmarketmobilee.ui.carUpdateScreen

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CarDetailDto
import com.emreyildirim.carmarketmobilee.ui.addCarScreen.AddCarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CarUpdateViewModel(application: Application) : AndroidViewModel(application) {

    // Backing data
    private val _loadedDetail = MutableLiveData<CarDetailDto?>(null)
    val loadedDetail: LiveData<CarDetailDto?> = _loadedDetail

    private val _uiState = MutableStateFlow(AddCarUiState())
    val uiState: StateFlow<AddCarUiState> = _uiState.asStateFlow()



    // Form state
    val brandName = mutableStateOf("")
    val model = mutableStateOf("")
    val price = mutableStateOf(BigDecimal.ZERO)
    val isNew = mutableStateOf(false)
    val carSpecification = mutableStateOf("")
    val engineVolume = mutableStateOf(0f)
    val releaseDatetime = mutableStateOf("")

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    val brandTouched = mutableStateOf(false)

    fun loadCar(carId: Long) {
        viewModelScope.launch {
            _loading.postValue(true)
            _error.postValue(null)
            try {
                val context = getApplication<Application>().applicationContext
                val service = RetrofitInstance.getCarService(context)
                val detail = service.getCarById(carId)
                _loadedDetail.postValue(detail)
                // Fill form
                brandName.value = detail.brandName
                model.value = detail.model
                price.value = detail.price
                isNew.value = detail.isNew
                carSpecification.value = detail.carSpecification
                engineVolume.value = detail.engineVolume
                releaseDatetime.value = detail.releaseDatetime?: ""

                // PRESELECT brand id from loaded detail (if user hasn't touched)
                if (!brandTouched.value) {
                    val preselectId = detail.brand?.id
                    if (preselectId != null) {
                        _uiState.value = _uiState.value.copy(id = preselectId)
                    }
                }

                _loading.postValue(false)
            } catch (e: Exception) {
                _loading.postValue(false)
                _error.postValue(e)
            }
        }
    }

    fun loadBrands(context: Context) {
        viewModelScope.launch {
            try {
                val brandService = RetrofitInstance.getBrandService(context)
                val brands = brandService.getBrands()
                _uiState.value = _uiState.value.copy(brands = brands)

                // If user hasn't changed brand, try to align id with loaded detail
                if (!brandTouched.value) {
                    val preId = _loadedDetail.value?.brand?.id
                    val name = _loadedDetail.value?.brandName
                    val matchId = preId ?: brands.find { it.brandName == name }?.id
                    if (matchId != null) {
                        _uiState.value = _uiState.value.copy(id = matchId)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load brands: ${e.message}"
                )
            }
        }
    }

    fun updateForm(field: String, value: Any) {
        println("DEBUG: updateForm called with field=$field, value=$value")
        _uiState.value = when (field) {
            "brandId" -> {
                brandTouched.value = true
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

    fun submit(carId: Long) {
        viewModelScope.launch {
            _loading.postValue(true)
            _error.postValue(null)
            try {
                val context = getApplication<Application>().applicationContext
                val service = RetrofitInstance.getCarService(context)

                val current = _loadedDetail.value
                val filePathToKeep = current?.filePath ?: ""

                val selectedBrandId = uiState.value.id
                val selectedBrand = uiState.value.brands.find { it.id == selectedBrandId }

                val body = CarDetailDto(
                    carId = carId,
                    brand = selectedBrand,
                    brandName = selectedBrand?.brandName ?: "brand not found",
                    model = model.value,
                    price = price.value,
                    isNew = isNew.value,
                    carSpecification = carSpecification.value,
                    engineVolume = engineVolume.value,
                    releaseDatetime = releaseDatetime.value,
                    filePath = filePathToKeep
                )

                service.updateCarById(carId, body)
                _loading.postValue(false)
                _updateResult.postValue(Result.success(Unit))
            } catch (e: Exception) {
                _loading.postValue(false)
                _updateResult.postValue(Result.failure(e))
                Log.d("CarUpdateViewModel", "Error: ${e.message}")
            }
        }
    }
}