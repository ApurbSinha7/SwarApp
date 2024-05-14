package com.android.swar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authManager = AuthManager()

    private val loggedIn: MutableState<Boolean> = mutableStateOf(false)
    private val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun register(email: String, password: String) {
        viewModelScope.launch {
            authManager.register(
                email,
                password,
                onSuccess = { loggedIn.value = true },
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

    fun logout() {
        authManager.logout()
        loggedIn.value = false
    }
}
