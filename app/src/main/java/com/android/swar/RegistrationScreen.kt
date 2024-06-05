package com.android.swar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import com.android.swar.presentation.sign_in.SignInState
import com.android.swar.ui.theme.AppTheme

//@Composable
//fun RegistrationScreen(modifier: Modifier = Modifier, state: SignInState,
//                       title: String, typography: Typography, viewModel: AuthViewModel, navController: NavController, onSignInClick: () -> Unit
//) {
//    val context = LocalContext.current
//    LaunchedEffect(key1 = state.signInError) {
//        state.signInError?.let { error ->
//            Toast.makeText(
//                context,
//                error,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var displayName by remember { mutableStateOf("") }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = title,
//            style = typography.headlineLarge,
//            modifier = Modifier.padding(bottom = 32.dp)
//        )
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
//        )
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
//            visualTransformation = PasswordVisualTransformation()
//        )
//        OutlinedTextField(
//            value = displayName,
//            onValueChange = { displayName = it },
//            label = { Text("Display Name") },
//            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
//        )
//        Button(
//            onClick = {
//                viewModel.register(email, password, displayName)
//            },
//            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
//        ) {
//            Text(text = "Register")
//        }
//        Text(
//            text = "Already in the club? Login.",
//            color = Color.Blue,
//            textDecoration = TextDecoration.Underline,
//            modifier = Modifier
//                .padding(vertical = 16.dp)
//                .clickable { navController.navigate("login_screen") }
//        )
//        Button(onClick = onSignInClick) {
//            Text(text = "Sign in")
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    state: SignInState,
    title: String,
    typography: Typography,
    viewModel: AuthViewModel,
    navController: NavController,
    onSignInClick: () -> Unit
) {
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
    var displayName by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    AppTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo placeholder (if any)
                    Image(
                        painter = painterResource(id = R.drawable.swarlogo), // Replace with your logo
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .width(180.dp)
                            .height(180.dp)
                            .padding(bottom = 12.dp)
                    )

                    Text(
                        text = title,
                        style = typography.headlineLarge,
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
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                        },
                        trailingIcon = {
                            val image = if (showPassword) painterResource(R.drawable.visibility_off_24px) else painterResource(R.drawable.visibility_24px)
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(painter = image, contentDescription = "Toggle Password Visibility")
                            }
                        },
                        isError = state.signInError != null
                    )
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Display Name Icon")
                        },
                        isError = state.signInError != null
                    )
                    Button(
                        onClick = {
                            val emailValidationResult = isValidEmail(email)
                            val passwordValidationResult = isValidPassword(password)
                            if (emailValidationResult.first && passwordValidationResult.first) {
                                viewModel.register(email, password, displayName)
                            } else {
                                val errorMessage = StringBuilder()
                                if (!emailValidationResult.first) {
                                    errorMessage.append(emailValidationResult.second)
                                }
                                if (!passwordValidationResult.first) {
                                    if (errorMessage.isNotEmpty()) {
                                        errorMessage.append("\n")
                                    }
                                    errorMessage.append(passwordValidationResult.second)
                                }
                                Toast.makeText(context, errorMessage.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Register", style = typography.bodyLarge)
                    }

                    Text(
                        text = "Already in the club? Login.",
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .clickable { navController.navigate("login_screen") }
                    )
                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.hiko),
                            contentDescription = "Sign In Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign in", style = typography.bodyLarge)
                    }
                }
            }
        }
    }
}

fun isValidEmail(email: String): Pair<Boolean, String> {
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return if (email.matches(emailPattern.toRegex())) {
        Pair(true, "")
    } else {
        Pair(false, "Invalid email format")
    }
}

fun isValidPassword(password: String): Pair<Boolean, String> {
    // Password should contain at least one digit, one upper case letter, one lower case letter, and one special character
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    return if (password.matches(passwordPattern.toRegex())) {
        Pair(true, "")
    } else {
        Pair(
            false,
            "Password must contain at least one digit, one upper case letter, one lower case letter, and one special character"
        )
    }
}

