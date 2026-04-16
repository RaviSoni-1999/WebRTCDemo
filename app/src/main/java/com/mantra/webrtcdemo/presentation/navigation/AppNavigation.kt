package com.mantra.webrtcdemo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mantra.webrtcdemo.presentation.ui.screens.CallScreen
import com.mantra.webrtcdemo.presentation.ui.screens.HomeScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Call : Screen("call_screen/{roomId}/{userName}") {
        fun createRoute(roomId: String, userName: String): String {
            return "call_screen/$roomId/$userName"
        }
    }
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
                navArgument("roomId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"

            CallScreen(
                roomId = roomId,
                userName = userName,
                navController = navController
            )
        }
    }
}