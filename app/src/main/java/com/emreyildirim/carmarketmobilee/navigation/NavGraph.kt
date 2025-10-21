package com.emreyildirim.carmarketmobilee.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.emreyildirim.carmarketmobilee.ui.detailScreen.DetailScreen
import com.emreyildirim.carmarketmobilee.ui.loginScreen.LoginRoute
import com.emreyildirim.carmarketmobilee.ui.homeScreen.HomeScreen
import com.emreyildirim.carmarketmobilee.ui.registerScreen.RegisterScreen
import com.emreyildirim.carmarketmobilee.ui.cartScreen.CartScreen
import com.emreyildirim.carmarketmobilee.utils.isJwtExpired

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val token = prefs.getString("jwt", null)

    val start = if (token.isNullOrBlank() || isJwtExpired(token)) "login" else "home"


    NavHost(navController = navController, startDestination = start, modifier = Modifier) {
        composable("login") {
            LoginRoute(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("profile") {

        }
        composable("favorites") {

        }
        composable("cart") {
            CartScreen()
        }
        composable(
            route = "detail/{carId}",
            arguments = listOf(
                navArgument("carId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getLong("carId") ?: return@composable
            DetailScreen(carId = carId)
        }
        // diğer composable routlteleri buraya ekleyebilirsiniz
    }
}



