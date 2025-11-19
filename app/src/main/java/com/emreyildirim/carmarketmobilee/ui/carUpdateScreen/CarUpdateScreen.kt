package com.emreyildirim.carmarketmobilee.ui.carUpdateScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarUpdateScreen(
    carId: Long,
    navController: NavHostController,
    viewModel: CarUpdateViewModel = viewModel()
){
    val context = LocalContext.current

    val loading by viewModel.loading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val updateResult by viewModel.updateResult.observeAsState()

    val uiState by viewModel.uiState.collectAsState()

    val loadedDetail by viewModel.loadedDetail.observeAsState()
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(carId) {
        viewModel.loadCar(carId)
    }

    LaunchedEffect(Unit) {
        viewModel.loadBrands(context)
    }

    LaunchedEffect(updateResult) {
        updateResult?.onSuccess {
            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }?.onFailure {
            Toast.makeText(context, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val brandState = remember { mutableStateOf(TextFieldValue()) }
    val modelState = remember { mutableStateOf(TextFieldValue()) }
    val priceState = remember { mutableStateOf(TextFieldValue()) }
    val specState = remember { mutableStateOf(TextFieldValue()) }
    val engineState = remember { mutableStateOf(TextFieldValue()) }
    val isNewState = remember { mutableStateOf(false) }


    // Brand Selection Dropdown
    var expanded by remember { mutableStateOf(false) }
    val selectedBrand = uiState.brands.find { it.id == uiState.id }



    LaunchedEffect(loadedDetail?.carId) {
        if (!initialized && loadedDetail != null) {
            val brand = viewModel.brandName.value ?: ""
            val model = viewModel.model.value ?: ""
            val price = viewModel.price.value?.toPlainString() ?: ""
            val spec = viewModel.carSpecification.value ?: ""
            val engine = viewModel.engineVolume.value?.toString() ?: ""
            val isNew = viewModel.isNew.value

            brandState.value = TextFieldValue(brand)
            modelState.value = TextFieldValue(model)
            priceState.value = TextFieldValue(price)
            specState.value = TextFieldValue(spec)
            engineState.value = TextFieldValue(engine)
            isNewState.value = isNew

            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Update Car") })
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            error?.let { Text("Load error: ${it.message}") }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {

                val displayBrand = selectedBrand?.brandName
                    ?: viewModel.brandName.value.ifBlank { "Select Brand" }

                OutlinedTextField(
                    value = displayBrand,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Brand") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.brands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand.brandName) },
                            onClick = {
                                println("DEBUG: Brand clicked: ${brand.brandName} (ID: ${brand.id})")
                                viewModel.updateForm("brandId", brand.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = modelState.value,
                onValueChange = {
                    modelState.value = it
                    viewModel.model.value = it.text
                },
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = priceState.value,
                onValueChange = {
                    priceState.value = it
                    viewModel.price.value = it.text.toBigDecimalOrNull() ?: BigDecimal.ZERO
                },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = specState.value,
                onValueChange = {
                    specState.value = it
                    viewModel.carSpecification.value = it.text
                },
                label = { Text("Specification") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = engineState.value,
                onValueChange = {
                    engineState.value = it
                    viewModel.engineVolume.value = it.text.toFloatOrNull() ?: 0f
                },
                label = { Text("Engine Volume (L)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.submit(carId) },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            Row {
                Checkbox(
                    checked = isNewState.value,
                    onCheckedChange = {
                        isNewState.value = it
                        viewModel.isNew.value = it
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("New")
            }

            Spacer(modifier = Modifier.weight(1f))


        }
    }
}