package com.emreyildirim.carmarketmobilee.ui.favoritesScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = viewModel(), navController: NavHostController) {
    val favorites by viewModel.favorites.observeAsState(emptyList())

    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()



    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Favorites") })
    }) { innerPadding ->
        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text("Loading favorites...")
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error ?: "Error", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(onClick = { viewModel.loadFavorites() }) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                LazyColumn(contentPadding = innerPadding) {
                    items(favorites) { car ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("detail/${car.carId}") }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = RetrofitInstance.buildAbsoluteUrl(car.filePath),
                                    contentDescription = "CarPhoto",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(8.dp),
                                    contentScale = ContentScale.Crop,
                                    error = rememberVectorPainter(Icons.Outlined.Photo),
                                    fallback = rememberVectorPainter(Icons.Outlined.Photo)
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "${car.brandName} ${car.model}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Price: ${car.price} ₺")
                                    Text(if (car.isNew) "New" else "Second Hand")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


