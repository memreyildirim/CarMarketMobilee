package com.emreyildirim.carmarketmobilee.ui.registerScreen

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.emreyildirim.carmarketmobilee.utils.extractRolesFromJwt

@Composable
fun RegisterScreen(navController: NavHostController,
                   modifier: Modifier = Modifier,
){
    val viewModel : RegisterViewModel = viewModel()
    val registerResult by viewModel.registerResult.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(registerResult) {
        registerResult?.onSuccess {token ->

            val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            prefs.edit().putString("jwt", token).apply()

            val role = extractRolesFromJwt(token)
            prefs.edit().putStringSet("role", role.toSet()).apply()

            Toast.makeText(context, "Register Successful", Toast.LENGTH_SHORT).show()

            navController.navigate("home"){
                popUpTo("register"){
                    inclusive = true
                }
            }
        }
    }



    val (userName, setUserName) = remember { mutableStateOf("") }
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Başlık
        Text("Register App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        //Username
        OutlinedTextField(
            value = userName.trim(),
            onValueChange = setUserName,
            label = { Text("Enter the Username") },
            placeholder = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        //email
        OutlinedTextField(
            value = email.trim(),
            onValueChange = setEmail,
            label = { Text("Enter The Email") },
            placeholder = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        //password
        OutlinedTextField(
            value = password.trim(),
            onValueChange = setPassword,
            label = { Text("Enter Password")},
            placeholder = { Text("Password")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {viewModel.registerUser(username = userName, email = email, password = password)},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

    }

}