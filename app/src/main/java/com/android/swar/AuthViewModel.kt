package com.android.swar
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.android.swar.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel(navController : NavController) : ViewModel() {

    private val authManager = AuthManager(navController = navController)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val loggedIn: MutableState<Boolean> = mutableStateOf(false)
    private val errorMessage: MutableState<String?> = mutableStateOf(null)
    //val logger: MutableState<Boolean> = mutableStateOf(false)
    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            authManager.register(
                email,
                password,
                username,
                onSuccess = {
                    loggedIn.value = true;
                    val userNow = auth.currentUser
                    userNow?.let { user ->
                        val newUid = user.uid
                        val userProfile = UserProfile(
                            uid = user.uid,
                            email = user.email,
                            displayName = username,
                            photoUrl = ""
                        )

                        db.collection("users").document(newUid).set(userProfile)
                            .addOnSuccessListener {
                                Log.d("AuthViewModel", "User data saved successfully")
                            }
                            .addOnFailureListener { e ->
                                // Handle the error, possibly show a Toast or Snackbar
                                Log.e("AuthViewModel", "Failed to save user data: ${e.message}")
                            }
                    }
                },
                onError = { errorMessage.value = it }
            )
        }
    }
    fun sendPasswordReset(email: String, onToast: (done: Boolean) -> Unit) {
        viewModelScope.launch {
            authManager.sendPasswordReset(
                email,
                toast = {
                   onToast(it)
                }
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
