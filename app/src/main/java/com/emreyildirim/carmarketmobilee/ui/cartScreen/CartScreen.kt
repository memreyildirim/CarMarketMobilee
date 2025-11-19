package com.emreyildirim.carmarketmobilee.ui.cartScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CartItemDto
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: CartViewModel = viewModel(),
               navController : NavHostController) {
    val cart by viewModel.cart.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()


    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sepetim") },
                actions = {
                    IconButton(onClick = { viewModel.refreshCart() }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Hata: $error",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refreshCart() }) {
                                Text("Tekrar Dene")
                            }
                        }
                    }
                }
                
                cart?.items?.isEmpty() == true -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sepetiniz boş",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cart?.items ?: emptyList()) { item ->
                            CartItemCard(item = item,
                                onDelete = { viewModel.deleteFromCart(item.carId) },
                                onIncrease = { viewModel.increaseQuantity(item) },
                                onDecrease = { viewModel.decreaseQuantity(item) },
                                onClick = { id -> navController.navigate("detail/$id") })
                        }

                        item {
                            // Toplam fiyat ve checkout butonu
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Toplam:",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${cart?.totalPrice ?: 0.0} ₺",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { viewModel.clearCart()  },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Sepeti Temizle")
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItemDto,
                         onDelete: () -> Unit,
                         onIncrease: () -> Unit,
                         onDecrease: () -> Unit,
                         onClick: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick(item.carId) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Araba resmi
            AsyncImage(
                model = RetrofitInstance.buildAbsoluteUrl(item.car?.filePath),
                contentDescription = "Car Image",
                modifier = Modifier
                    .size(80.dp)
                    .weight(0.3f)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Araba bilgileri
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${item.car?.brandName ?: ""} ${item.car?.model ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Fiyat: ${item.car?.price ?: 0} ₺",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "Adet: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )

                val lineTotal: BigDecimal =
                    (item.car?.price ?: BigDecimal.ZERO).multiply(BigDecimal.valueOf(item.quantity.toLong()))
                Text(
                    text = "Toplam: $lineTotal ₺",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            //Butonlar
            Column(modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End) {
                // Sil butonu
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Sepetten Çıkar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Row (modifier = Modifier.padding(5.dp)){
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = onIncrease
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase Quantity",
                            tint = MaterialTheme.colorScheme.error

                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = onDecrease
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Increase Quantitr",
                            tint = MaterialTheme.colorScheme.error

                        )
                    }
                }
            }
        }
    }
}
