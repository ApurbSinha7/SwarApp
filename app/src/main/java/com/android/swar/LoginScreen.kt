package com.android.swar


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.swar.presentation.sign_in.SignInState
import com.android.swar.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
        modifier: Modifier = Modifier,
        typography: Typography,
        state: SignInState,
        onSignInClick: () -> Unit,
        viewModel: AuthViewModel,
        navController: NavController
) {
        //new
        val coroutineScope = rememberCoroutineScope()
        var showPassword by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        //end new
        val context = LocalContext.current
        LaunchedEffect(key1 = state.signInError) {
                state.signInError?.let { error ->
                        Toast.makeText(
                                context,
                                error,
                                Toast.LENGTH_LONG
                        ).show()
                }
        }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var forgotPassword = remember { mutableStateOf(false) }

        AppTheme {
                Box(
                        modifier = modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp)
                                .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                ) {
                        if (isLoading) {
                                CircularProgressIndicator()
                        } else {
                                Card(
                                        modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(8.dp)
                                ) {
                                        Column(
                                                modifier = Modifier
                                                        .padding(16.dp)
                                                        .fillMaxWidth(),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                                Image(
                                                        painter = painterResource(id = R.drawable.swarlogo), // Replace with your logo
                                                        contentDescription = "App Logo",
                                                        modifier = Modifier
                                                                .width(180.dp)
                                                                .height(180.dp)
                                                                .padding(bottom = 12.dp)
                                                )
                                                Text(
                                                        text = "Swar",
                                                        style = typography.displayMedium,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(bottom = 32.dp)
                                                )
                                                OutlinedTextField(
                                                        value = email,
                                                        onValueChange = { email = it },
                                                        label = { Text("Email") },
                                                        modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 8.dp),
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                                        singleLine = true,
                                                        leadingIcon = {
                                                                Icon(Icons.Default.Email, contentDescription = "Email Icon")
                                                        },
                                                        isError = state.signInError != null
                                                )
                                                if (errorMessage != null) {
                                                        Text(
                                                                text = errorMessage ?: "",
                                                                color = MaterialTheme.colorScheme.error,
                                                                fontSize = 12.sp,
                                                                modifier = Modifier
                                                                        .padding(bottom = 8.dp)
                                                                        .align(Alignment.Start)
                                                        )
                                                }
                                                OutlinedTextField(
                                                        value = password,
                                                        onValueChange = { password = it },
                                                        label = { Text("Password") },
                                                        modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 8.dp),
                                                        visualTransformation = if (showPassword) VisualTransformation.None
                                                        else PasswordVisualTransformation(),
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                                        singleLine = true,
                                                        leadingIcon = {
                                                                Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                                                        },
                                                        trailingIcon = {
                                                                val image = if (showPassword) painterResource(R.drawable.visibility_off_24px)
                                                                else painterResource(R.drawable.visibility_24px)
                                                                IconButton(onClick = { showPassword = !showPassword }) {
                                                                        Icon(painter = image, contentDescription = "Toggle Password Visibility")
                                                                }
                                                        },
                                                        isError = state.signInError != null
                                                )
                                                Button(
                                                        onClick = {
                                                                coroutineScope.launch {
                                                                        isLoading = true
                                                                        viewModel.login(email, password)
                                                                        isLoading = false
                                                                }
                                                        },
                                                        modifier = Modifier
                                                                .padding(vertical = 16.dp)
                                                                .fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp)
                                                ) {
                                                        Text(text = "Login")
                                                }
                                                Text(
                                                        text = "Forgot Password?",
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        textDecoration = TextDecoration.Underline,
                                                        modifier = Modifier
                                                                .padding(vertical = 8.dp)
                                                                .clickable { forgotPassword.value = true }
                                                )
                                                Text(
                                                        text = "First Time Here? Register",
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        textDecoration = TextDecoration.Underline,
                                                        modifier = Modifier
                                                                .padding(vertical = 8.dp)
                                                                .clickable { navController.navigate("register") }
                                                )
                                                Button(
                                                        onClick = onSignInClick,
                                                        modifier = Modifier
                                                                .padding(vertical = 8.dp)
                                                                .fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.buttonColors(
                                                                containerColor = MaterialTheme.colorScheme.secondary
                                                        )
                                                ) {
                                                        Text(text = "Sign in with Google")
                                                }
                                        }
                                }
                        }
                        if (forgotPassword.value) {
                                ForgotPasswordDialog(
                                        onDismiss = { forgotPassword.value = false },
                                        onResetPassword = { email ->
                                                coroutineScope.launch {
                                                        isLoading = true
                                                        viewModel.sendPasswordReset(email, onToast = {
                                                                if (it) {
                                                                Toast.makeText(
                                                                        context,
                                                                        "Password reset email sent. Check Inbox",
                                                                        Toast.LENGTH_LONG
                                                                ).show()
                                                                        }
                                                                else {
                                                                        Toast.makeText(
                                                                                context,
                                                                                "No user found with this email",
                                                                                Toast.LENGTH_LONG
                                                                        ).show()
                                                                }
                                                        })
                                                        isLoading = false
                                                }
                                                }
                                )
                        }

                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
        onDismiss: () -> Unit,
        onResetPassword: (email: String) -> Unit
) {
        var email = remember { mutableStateOf("") }
        AppTheme {
                AlertDialog(
                        onDismissRequest = { onDismiss() },
                        title = {
                                Text("Enter your email address")
                        },
                        text = {
                                Column {
                                        Text("Get a password reset link:")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                                value = email.value,
                                                onValueChange = { email.value = it },
                                                label = { Text("Email") },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = TextFieldDefaults.textFieldColors(
                                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                                                )
                                        )
                                }
                        },
                        confirmButton = {
                                Button(onClick = {
                                        onResetPassword(email.value)
                                        onDismiss()
                                }) {
                                        Text( "Send Link")
                                }
                        },
                        dismissButton = {
                                Button(onClick = onDismiss) {
                                        Text("Cancel")
                                }
                        }
                )
        }
}


//@Composable
//fun LoginScreen(modifier: Modifier = Modifier, typography: Typography,
//                state: SignInState,
//                onSignInClick: () -> Unit,
//                viewModel: AuthViewModel, navController: NavController
//) {
//        val context = LocalContext.current
//        LaunchedEffect(key1 = state.signInError) {
//                state.signInError?.let { error ->
//                        Toast.makeText(
//                                context,
//                                error,
//                                Toast.LENGTH_LONG
//                        ).show()
//                }
//        }
//        var email by remember { mutableStateOf("") }
//        var password by remember { mutableStateOf("") }
//
//        Column(
//                modifier = modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//                Text(
//                        text = "Swar",
//                        style = typography.bodyLarge,
//                        modifier = Modifier.padding(top = 48.dp, bottom = 32.dp)
//                )
//                OutlinedTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        label = { Text("Email") },
//                        modifier = Modifier.padding(16.dp)
//                )
//
//                OutlinedTextField(
//                        value = password,
//                        onValueChange = { password = it },
//                        label = { Text("Password") },
//                        modifier = Modifier.padding(16.dp)
//                )
//
//                Button(
//                        onClick = {
//                                viewModel.login(email, password)
//                        },
//                        modifier = Modifier.padding(16.dp)
//                ) {
//                        Text(text = "Login")
//                }
//                Text(
//                        text = "First Time Here? Register",
//                        color = Color.Blue,
//                        textDecoration = TextDecoration.Underline,
//                        modifier = Modifier
//                                .padding(16.dp)
//                                .fillMaxWidth()
//                                .clickable { navController.navigate("register") }
//                )
//                Button(onClick = onSignInClick) {
//                        Text(text = "Sign in")
//                }
//        }
//
//}