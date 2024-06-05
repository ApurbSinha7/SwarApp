package com.android.swar

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.swar.ui.theme.AppTheme
import com.android.swar.viewModel.ProfileViewModel

//@Composable
//fun ProfileScreen(profileViewModel: ProfileViewModel = viewModel(), navController: NavController) {
//
//    val userProfile by profileViewModel.userProfile.observeAsState()
//
//    val authViewModel = AuthViewModel(navController = navController)
//
//    val (displayName, setDisplayName) = remember { mutableStateOf("") }
//    val (bio, setBio) = remember { mutableStateOf("") }
//
//    val context = LocalContext.current
//
//    LaunchedEffect(userProfile) {
//        userProfile?.let {
//            setDisplayName(it.displayName ?: "")
//            setBio(it.bio ?: "")
//        }
//    }
//
//    userProfile?.let { profile ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(modifier = Modifier
//                .border(width = 4.dp, color = Gray, shape = RoundedCornerShape(16.dp)),
//                contentAlignment = Alignment.Center) {
//                TextField(
//                    value = displayName,
//                    onValueChange = setDisplayName,
//                    label = { Text("Display Name") }
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                TextField(
//                    value = bio,
//                    onValueChange = setBio,
//                    label = { Text("Bio") }
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                Button(onClick = {
//                    val updatedProfile = profile.copy(
//                        displayName = displayName,
//                        bio = bio
//                    )
//                    profileViewModel.updateProfile(updatedProfile) { success ->
//                        if (success) {
//                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
//                            navController.navigate("home")
//                        } else {
//                            Toast.makeText(context, "Profile update failed", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                }) {
//                    Text("Save")
//                }
//            }
//            Button(onClick = { authViewModel.logout() }) {
//                Text(text = "Sign out")
//            }
//        }
//    } ?: run {
//        Text(text = "Loading profile...", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
//    }
//}

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    navController: NavController
) {
    val userProfile by profileViewModel.userProfile.observeAsState()
    val authViewModel = AuthViewModel(navController = navController)

    val (displayName, setDisplayName) = remember { mutableStateOf("") }
    val (bio, setBio) = remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(userProfile) {
        userProfile?.let {
            setDisplayName(it.displayName ?: "")
            setBio(it.bio ?: "")
        }
    }

    userProfile?.let { profile ->
        AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), //16dp padding if you can
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .border(width = 4.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "My Profile")
                        TextField(
                            value = displayName,
                            onValueChange = setDisplayName,
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = bio,
                            onValueChange = setBio,
                            label = { Text("Bio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val updatedProfile = profile.copy(
                                    displayName = displayName,
                                    bio = bio
                                )
                                profileViewModel.updateProfile(updatedProfile) { success ->
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "Profile updated",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        navController.navigate("home")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Profile update failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { authViewModel.logout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Sign out")
                }
                ThemedButton(onClick = { navController.navigate("home") })
            }
        }
    }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Loading profile...", textAlign = TextAlign.Center)
        }
    }
}