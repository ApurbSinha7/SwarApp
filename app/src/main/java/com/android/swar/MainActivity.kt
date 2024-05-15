package com.android.swar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.swar.ui.theme.SwarTheme
import com.android.swar.ui.theme.Typography


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        setContent {
            SwarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    val viewModel = AuthViewModel()

                    val navController = rememberNavController()

                    // Set up the NavHost with the navigation graph
                    NavHost(navController = navController, startDestination = "login_screen") {
                        composable("login_screen") {
                            LoginScreen(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                                typography = Typography,
                                title = "Swar",
                                viewModel = viewModel
                            )
                        }
                        composable("register") {
                            RegistrationScreen(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                                typography = Typography,
                                title = "Swar",
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier)
//
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    SwarTheme {
//        Greeting("Android")
//    }
//}