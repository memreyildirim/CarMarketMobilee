package com.emreyildirim.carmarketmobilee

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.emreyildirim.carmarketmobilee.navigation.NavGraph
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emreyildirim.carmarketmobilee.ui.components.AppBottomBar

@Composable
fun CarMarketMobileeApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    // Login, register ve detail sayfalarında bottom bar'ı gizle
    val showBottomBar = when {
        route == "login" || route == "register" || route == "update" -> false
        route?.startsWith("detail/") == true -> false
        else -> true
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavGraph(navController = navController, modifier = androidx.compose.ui.Modifier.padding(innerPadding))
    }
}