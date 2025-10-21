package com.emreyildirim.carmarketmobilee.ui.homeScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.core.content.edit

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
                IconButton(onClick = {
                    val prefs = context.getSharedPreferences("auth",Context.MODE_PRIVATE)
                    prefs.edit() {
                        remove("jwt")
                            .remove("role")
                    }

                    navController.navigate("login"){
                        popUpTo("home"){
                            inclusive = true
                        }
                        launchSingleTop =true
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                }
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
                    Column (modifier = Modifier.padding(16.dp)){
                        Text("${car.brandName} ${car.model}", style = MaterialTheme.typography.titleMedium)
                        Text("Price:${car.price} ₺")
                        Text(if (car.isNew) "New" else "Second Hand")
                    }
                }
            }
        }


    }

}