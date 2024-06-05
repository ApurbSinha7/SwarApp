package com.android.swar
import android.content.ContentValues.TAG
import android.util.Log
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

class AuthManager(private val navController: NavController) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(email: String, password: String, username: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    onSuccess(); navController.navigate("home")
                } else {
                    onError(task.exception?.message ?: "Registration failed")
                    Log.e("AuthManager", "Registration failed: ${task.exception?.message}")
                }
            }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(); navController.navigate("home"){
                        popUpTo("login_screen") { inclusive = true }
                    }
                } else {
                    onError(task.exception?.message ?: "Login failed")
                    Log.e("AuthManager", "Login failed: ${task.exception?.message}")
                }
            }
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser !=null
    }
    fun logout() {
        auth.signOut()
        navController.navigate("login_screen"){
            popUpTo("home") { inclusive = true }
        }
    }
    fun sendPasswordReset(email: String, toast: (done : Boolean) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    toast(true)
                }
                else{
                    toast(false)
                }
            }
    }
}
