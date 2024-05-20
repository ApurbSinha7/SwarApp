package com.android.swar.viewModel
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.android.swar.model.AuthRepository
//
//class AuthViewModel(application: Application) : AndroidViewModel(application) {
//    private val authRepository = AuthRepository()
//    private val _user = MutableLiveData<User?>()
//    val user: LiveData<User?> = _user
//
//    fun signInWithGoogle(idToken: String) {
//        authRepository.signInWithGoogle(idToken) { success ->
//            if (success) {
//                _user.value = firebaseAuth.currentUser?.let { User(it.uid, it.email) }
//            } else {
//                _user.value = null
//            }
//        }
//    }
//
//    fun signInWithMicrosoft(idToken: String) {
//        authRepository.signInWithMicrosoft(idToken) { success ->
//            if (success) {
//                _user.value = firebaseAuth.currentUser?.let { User(it.uid, it.email) }
//            } else {
//                _user.value = null
//            }
//        }
//    }
//}
