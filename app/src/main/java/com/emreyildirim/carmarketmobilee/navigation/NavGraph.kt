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
import com.emreyildirim.carmarketmobilee.ui.carUpdateScreen.CarUpdateScreen
import com.emreyildirim.carmarketmobilee.ui.detailScreen.DetailScreen
import com.emreyildirim.carmarketmobilee.ui.loginScreen.LoginRoute
import com.emreyildirim.carmarketmobilee.ui.homeScreen.HomeScreen
import com.emreyildirim.carmarketmobilee.ui.registerScreen.RegisterScreen
import com.emreyildirim.carmarketmobilee.ui.cartScreen.CartScreen
import com.emreyildirim.carmarketmobilee.ui.addCarScreen.AddCar
import com.emreyildirim.carmarketmobilee.ui.favoritesScreen.FavoritesScreen
import com.emreyildirim.carmarketmobilee.ui.adminPanelScreen.AdminPanelScreen
import com.emreyildirim.carmarketmobilee.ui.profileScreen.ProfileScreen
import com.emreyildirim.carmarketmobilee.utils.isJwtExpired

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val token = prefs.getString("jwt", null)

    val start = if (token.isNullOrBlank() || isJwtExpired(token)) "login" else "home"


    NavHost(navController = navController, startDestination = start, modifier = modifier) {
        composable("login") {
            LoginRoute(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("add") {
            AddCar(navController = navController)
        }
        composable("panel") {
            AdminPanelScreen(navController = navController)
        }
        composable("favorites") {
            FavoritesScreen(navController = navController)
        }
        composable("cart") {
            CartScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable(
            route = "update/{carId}",
            arguments = listOf(
                navArgument("carId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getLong("carId") ?: return@composable
            CarUpdateScreen(carId = carId, navController = navController)
        }
        composable(
            route = "detail/{carId}",
            arguments = listOf(
                navArgument("carId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getLong("carId") ?: return@composable
            DetailScreen(carId = carId,navController=navController)
        }
        // diğer composable routlteleri buraya ekleyebilirsiniz
    }
}



