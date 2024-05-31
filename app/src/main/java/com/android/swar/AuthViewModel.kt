package com.android.swar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch

class AuthViewModel(navController : NavController) : ViewModel() {

    private val authManager = AuthManager(navController = navController)

    private val loggedIn: MutableState<Boolean> = mutableStateOf(false)
    private val errorMessage: MutableState<String?> = mutableStateOf(null)
    val logger: MutableState<Boolean> = mutableStateOf(false)
    fun register(email: String, password: String) {
        viewModelScope.launch {
            authManager.register(
                email,
                password,
                onSuccess = { loggedIn.value = true; logger.value = true },
                onError = { errorMessage.value = it }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authManager.login(
                email,
                password,
                onSuccess = { loggedIn.value = true },
                onError = { errorMessage.value = it }
            )
        }
    }
    fun isLoggedIn(): Boolean {
        return authManager.isLoggedIn()
    }


    fun logout() {
        authManager.logout()
        loggedIn.value = false
    }
}
