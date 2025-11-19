package com.emreyildirim.carmarketmobilee.ui.homeScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.core.content.edit
import coil3.compose.AsyncImage
import com.emreyildirim.carmarketmobilee.R
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), navController: NavHostController)  {
    val cars by viewModel.cars.observeAsState(emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadCars()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {Text("Ilan Sayfası")},
            actions = {
            }
        )
        })
    { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(cars) { car ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable{
                        Toast.makeText(context, "Card Clicked", Toast.LENGTH_SHORT).show()
                        // detail ekranına yönlendirme
                        navController.navigate("detail/${car.carId}")
                    }
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
                            error = rememberVectorPainter(Icons.Outlined.Photo),
                            fallback = rememberVectorPainter(Icons.Outlined.Photo)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            Text(
                                "${car.brandName} ${car.model}",
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