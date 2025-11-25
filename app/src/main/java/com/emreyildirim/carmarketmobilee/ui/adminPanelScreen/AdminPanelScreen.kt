package com.emreyildirim.carmarketmobilee.ui.adminPanelScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.emreyildirim.carmarketmobilee.data.RetrofitInstance
import com.emreyildirim.carmarketmobilee.model.CartItemDto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navController: NavHostController,
    viewModel: AdminPanelViewModel = viewModel(),

) {

    val context = LocalContext.current

    var showAddAdminDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val addAdminResult by viewModel.addAdminResult.observeAsState()

    addAdminResult?.onFailure {
        showAddAdminDialog = false
        Toast.makeText(context, "Admin add failed: ${it.message}", Toast.LENGTH_SHORT).show()

    }



    LaunchedEffect(Unit) {
        viewModel.refreshUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Admin Panel", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { viewModel.refreshUsers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                    IconButton(onClick = {showAddAdminDialog = true} ) {
                        Icon(Icons.Default.AddModerator, contentDescription = "Add Admin")
                    }
                    if (showAddAdminDialog) {

                        var username by remember { mutableStateOf("") }
                        var email by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }

                        AlertDialog(
                            onDismissRequest = { showAddAdminDialog = false },
                            title = { Text("Yeni Admin Ekle") },
                            text = {
                                Column {
                                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
                                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    // Backend’e istek at
                                    viewModel.addAdmin(username,email, password)
                                    showAddAdminDialog = false
                                }) { Text("Ekle") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddAdminDialog = false }) { Text("İptal") }
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            uiState.errorMessage?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hata mesajını kapat",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            if (uiState.isLoadingUsers && uiState.users.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.users.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Gösterilecek kullanıcı bulunamadı")
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    UsersColumn(
                        uiState = uiState,
                        onUserSelected = { viewModel.selectUser(it) },
                        modifier = Modifier.weight(0.4f)
                    )

                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 16.dp),
                        thickness = 1.dp
                    )

                    DetailsColumn(
                            uiState = uiState,
                            modifier = Modifier.weight(0.6f)
                    )

                }
            }
        }
    }
}

@Composable
private fun UsersColumn(
    uiState: AdminPanelUiState,
    onUserSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "Kullanıcılar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.users) { user ->
                val isSelected = uiState.selectedUser?.id == user.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onUserSelected(user.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = user.username ?: "İsimsiz kullanıcı",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = user.email ?: "Email bulunamadı",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Rol: ${user.role}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsColumn(
    uiState: AdminPanelUiState,
    modifier: Modifier = Modifier,
    viewModel: AdminPanelViewModel = viewModel()
) {
    val selectedUser = uiState.selectedUser

    if (selectedUser == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Detay görmek için bir kullanıcı seçin")
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = selectedUser.username ?: "İsimsiz kullanıcı",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = selectedUser.email ?: "Email bulunamadı",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Rol: ${selectedUser.role}",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = {viewModel.deleteUser(selectedUser.id)}) {
            Text("Kullanıcıyı Sil")
        }

        if (uiState.isLoadingDetails) {
            Spacer(modifier = Modifier.size(6.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.size(12.dp))



        if (uiState.selectedUser.role == "USER"){

            Text(
                text = "Sepet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            val cart = uiState.cart
            if (cart == null) {
                val message = if (uiState.isLoadingDetails) {
                    "Sepet yükleniyor..."
                } else {
                    "Sepet bilgisi bulunamadı"
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else if (cart.items.isEmpty()) {
                Text(
                    text = "Sepet boş",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                cart.items.forEach { item ->
                    CartItemCard(item)
                }
                Text(
                    text = "Toplam: %.2f ₺".format(cart.totalPrice),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = "Favoriler",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (uiState.favorites.isEmpty()) {
                Text(
                    text = if (uiState.isLoadingDetails) "Favoriler yükleniyor..." else "Favori araç bulunamadı",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                uiState.favorites.forEach { car ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = RetrofitInstance.buildAbsoluteUrl(car.filePath),
                                contentDescription = "Araç fotoğrafı",
                                modifier = Modifier
                                    .size(72.dp)
                                    .padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${car.brandName} ${car.model}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${car.price} ₺",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (car.isNew) "Sıfır" else "İkinci El",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItemDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = RetrofitInstance.buildAbsoluteUrl(item.car?.filePath),
                contentDescription = "Sepet araç fotoğrafı",
                modifier = Modifier.size(72.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.car?.let { "${it.brandName} ${it.model}" } ?: "Araç #${item.carId}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Adet: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
