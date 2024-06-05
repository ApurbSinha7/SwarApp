package com.android.swar.utils

import androidx.navigation.NavController

fun navigateToProfile(navController: NavController) {
    navController.navigate("home") {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}