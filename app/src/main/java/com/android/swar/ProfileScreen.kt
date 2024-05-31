package com.android.swar

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.swar.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = viewModel(), navController: NavController) {
    val userProfile by profileViewModel.userProfile.observeAsState()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = displayName,
                onValueChange = setDisplayName,
                label = { Text("Display Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = bio,
                onValueChange = setBio,
                label = { Text("Bio") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val updatedProfile = profile.copy(
                    displayName = displayName,
                    bio = bio
                )
                profileViewModel.updateProfile(updatedProfile) { success ->
                    if (success) {
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Profile update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Save")
            }
        }
    } ?: run {
        Text(text = "Loading profile...", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
    }
}
