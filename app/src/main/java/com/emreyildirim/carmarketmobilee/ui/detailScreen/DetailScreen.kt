package com.emreyildirim.carmarketmobilee.ui.detailScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(carId: Long, viewModel: DetailViewModel = viewModel()) {
    val carDetail by viewModel.carDetail.observeAsState()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val role = prefs.getStringSet("role", emptySet()) ?: emptySet()
    val isAdmin = role.contains("ADMIN")
    val isUser = role.contains("USER")
    val addToCartResult by viewModel.addToCartResult.observeAsState()


    LaunchedEffect(carId) {
        viewModel.loadCarDetail(carId)
    }

    LaunchedEffect(addToCartResult) {
        addToCartResult?.onSuccess {
            Toast.makeText(context,"Added to cart succesfully", Toast.LENGTH_SHORT).show()
        }?.onFailure {
            Toast.makeText(context,"Adding failure: ${it.message}",Toast.LENGTH_SHORT).show()
        }
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detail Screen") }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Image
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                val imageUrl = RetrofitInstance.buildAbsoluteUrl(carDetail?.filePath)
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title and price
            Text(
                text = "${carDetail?.brandName ?: ""} ${carDetail?.model ?: ""}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fiyat: ${carDetail?.price?.toPlainString() ?: "-"} ₺",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (carDetail?.isNew == true) "New" else "Second Hand",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(6.dp))

            // Specs
            Text("Specifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            KeyValueRow(key = "Engine Volume", value = "${carDetail?.engineVolume ?: 0f} L")
            KeyValueRow(key = "Release Time", value = carDetail?.releaseDatetime ?: "-")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Descriptions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = carDetail?.carSpecification ?: "-", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isAdmin){
                    Button(onClick = {}) {
                        Text("Update Car")
                    }
                }else if (isUser){
                    Button(onClick = {
                        viewModel.addToCart(carId)
                    }) {
                        Text("Add to Cart")
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(Icons.Default.Add, contentDescription = "Add to Cart")
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyValueRow(key: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = key, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
    }
}