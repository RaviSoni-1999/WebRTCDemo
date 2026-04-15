package com.mantra.webrtcdemo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Call : Screen("call/{isVideo}/{roomId}") {
        fun createRoute(isVideo: Boolean, roomId: String) = "call/$isVideo/$roomId"
    }
    data object Chat : Screen("chat/{roomId}")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { 
            HomeScreen(navController) 
        }
        composable(
            route = Screen.Call.route,
            arguments = listOf(
                navArgument("isVideo") { type = NavType.BoolType },
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isVideo = backStackEntry.arguments?.getBoolean("isVideo") ?: false
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            CallScreen(isVideo = isVideo, roomId = roomId, navController = navController)
        }
        composable(Screen.Chat.route) { 
            /* Chat screen later */ 
        }
    }
}

// Dummy screen composables to allow compilation
@Composable
fun HomeScreen(navController: NavHostController) {
    // Placeholder
}

@Composable
fun CallScreen(isVideo: Boolean, roomId: String, navController: NavHostController) {
    // Placeholder
}