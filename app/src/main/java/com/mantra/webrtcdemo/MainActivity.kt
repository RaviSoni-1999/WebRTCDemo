package com.mantra.webrtcdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.mantra.webrtcdemo.presentation.navigation.AppNavHost
import com.mantra.webrtcdemo.ui.theme.WebRTCDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebRTCDemoTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}