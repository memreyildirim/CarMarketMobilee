package com.emreyildirim.carmarketmobilee.ui.loginScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.emreyildirim.carmarketmobilee.utils.extractRolesFromJwt

@Composable
fun LoginRoute(navController: NavHostController) {
    val viewModel: LoginViewModel = viewModel()
    val loginResult by viewModel.loginResult.observeAsState()
    val context = LocalContext.current

    LoginScreen(
        onLoginClick = { email, password ->
            viewModel.login(email, password)
        },
        onRegisterClicked = {navController.navigate("register")}
    )

    loginResult?.onSuccess { token ->
        //token sakla
        val prefs =context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().putString("jwt", token).apply()

        val role = extractRolesFromJwt(token)//AuthUtlis den gelen
        prefs.edit().putStringSet("role", role.toSet()).apply()

        /*
        //eğer giriş anında role göre yönlendirme yapılmak istenirse bu kullnaılabilir
        val target = if (role.contains("ADMIN")) "home" else "register"
        navController.navigate(target)

         */

        //ana ekrana yönledmir
        navController.navigate("home"){
            popUpTo("login"){
                inclusive = true
            }
        }

    }

    loginResult?.onFailure {
        Toast.makeText(context, "Giriş başarısız : ${it.message}", Toast.LENGTH_SHORT).show()
    }
}
