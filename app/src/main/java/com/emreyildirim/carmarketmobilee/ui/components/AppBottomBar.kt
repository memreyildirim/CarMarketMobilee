package com.emreyildirim.carmarketmobilee.ui.components

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emreyildirim.carmarketmobilee.data.BottomNavItem



@Composable
fun AppBottomBar(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val role = prefs.getStringSet("role", emptySet()) ?: emptySet()
    val isUser = role.contains("USER")
    val isAdmin = role.contains("ADMIN")
    
    // Kullanıcı rolüne göre menü öğelerini filtrele
    val allItems = listOf(
         BottomNavItem(route = "home", Icons.Default.Home, label = "Home"),
         BottomNavItem(route = "cart", Icons.Default.ShoppingCart, label = "Cart"),
         BottomNavItem(route = "add", icon = Icons.Default.Add, label = "Add Car"),
         BottomNavItem(route = "favorites", Icons.Default.Favorite, label = "Favorites"),
         BottomNavItem(route = "panel", Icons.Default.AdminPanelSettings, label = "Panel"),
         BottomNavItem(route = "profile", Icons.Default.Person, label = "Profile")

    )
    
    // Eğer kullanıcı rolü USER ise cart menüsünü göster, değilse gizle
    val items = allItems.filter { item ->
        when(item.route){
            "home" -> isUser || isAdmin
            "add" -> isAdmin
            "panel" ->  isAdmin
            "cart" -> isUser
            "favorites" -> isUser
            "profile" -> isUser || isAdmin

            else -> true
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination.isInHierarchy(item.route)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

private fun NavDestination?.isInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}