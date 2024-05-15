package com.android.swar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               typography: Typography, viewModel: AuthViewModel, navController: NavController
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        Text(
            text = "Nigga Home",
            style = typography.bodyLarge,
            modifier = Modifier.padding(top = 48.dp, bottom = 32.dp)
        )
        Button(
            onClick = {
                viewModel.logout()
            },
            modifier = Modifier.padding(16.dp)
                .clickable { navController.navigate("login_screen") }
        ) {
            Text(text = "Log Out")
        }
    }
}