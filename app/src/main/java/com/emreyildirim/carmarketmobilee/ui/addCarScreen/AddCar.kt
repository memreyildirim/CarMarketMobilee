package com.emreyildirim.carmarketmobilee.ui.addCarScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.emreyildirim.carmarketmobilee.model.Brand
import com.emreyildirim.carmarketmobilee.ui.addCarScreen.AddCarViewModel
import com.emreyildirim.carmarketmobilee.ui.addCarScreen.AddCarUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCar(
    navController: NavHostController,
    viewModel: AddCarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Load brands on startup
    LaunchedEffect(Unit) {
        viewModel.loadBrands(context)
    }
    
    // Handle success navigation
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.popBackStack()
        }
    }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateForm("selectedImageUri", uri as Any)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp,0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text(text = "Add Car",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium) },

        )


        
        // Photo Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.selectedImageUri != null) {
                    AsyncImage(
                        model = uiState.selectedImageUri,
                        contentDescription = "Selected car photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Add photo",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to add car photo")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Brand Selection Dropdown
        var expanded by remember { mutableStateOf(false) }
        val selectedBrand = uiState.brands.find { it.id == uiState.id }
        
        // Debug logs
        LaunchedEffect(uiState.id, uiState.brands) {
            println("DEBUG: Current brandId = ${uiState.id}")
            println("DEBUG: Available brands = ${uiState.brands.map { "${it.brandName} (${it.id})" }}")
            println("DEBUG: Selected brand = ${selectedBrand?.brandName}")
        }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedBrand?.brandName ?: "Select Brand",
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
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Model
        OutlinedTextField(
            value = uiState.model,
            onValueChange = { viewModel.updateForm("model", it) },
            label = { Text("Model") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Car Specification
        OutlinedTextField(
            value = uiState.carSpecification,
            onValueChange = { viewModel.updateForm("carSpecification", it) },
            label = { Text("Car Specification") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Engine Volume
        OutlinedTextField(
            value = uiState.engineVolume,
            onValueChange = { viewModel.updateForm("engineVolume", it) },
            label = { Text("Engine Volume (L)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Is New Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Is New Car?")
            Switch(
                checked = uiState.isNew,
                onCheckedChange = { viewModel.updateForm("isNew", it) }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Price
        OutlinedTextField(
            value = uiState.price,
            onValueChange = { viewModel.updateForm("price", it) },
            label = { Text("Price (₺)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Release DateTime Picker
        var showDateTimePicker by remember { mutableStateOf(false) }
        
//        OutlinedButton(
//            onClick = { showDateTimePicker = true },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Release Date & Time: ${uiState.releaseDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
//        }
//
//        // DateTime Picker Dialog
//        if (showDateTimePicker) {
//            val datePickerState = rememberDatePickerState(
//                initialSelectedDateMillis = uiState.releaseDateTime.toLocalDate().toEpochDay() * 24 * 60 * 60 * 1000
//            )
//            val timePickerState = rememberTimePickerState(
//                initialHour = uiState.releaseDateTime.hour,
//                initialMinute = uiState.releaseDateTime.minute
//            )
//
//            AlertDialog(
//                onDismissRequest = { showDateTimePicker = false },
//                title = { Text("Select Date & Time") },
//                text = {
//                    Column {
//                        DatePicker(state = datePickerState)
//                        Spacer(modifier = Modifier.height(16.dp))
//                        TimePicker(state = timePickerState)
//                    }
//                },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            datePickerState.selectedDateMillis?.let { millis ->
//                                val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
//                                val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
//                                val dateTime = LocalDateTime.of(date, time)
//                                viewModel.updateForm("releaseDateTime", dateTime)
//                            }
//                            showDateTimePicker = false
//                        }
//                    ) {
//                        Text("OK")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDateTimePicker = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error Message
        uiState.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Submit Button
        Button(
            onClick = { viewModel.createCar(context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Add Car")
            }
        }
        
        Spacer(modifier = Modifier.height(46.dp))
    }
}

