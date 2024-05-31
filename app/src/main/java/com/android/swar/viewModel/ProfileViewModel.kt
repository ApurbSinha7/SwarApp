package com.android.swar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.swar.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val user = auth.currentUser
        user?.let {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        _userProfile.value = document.toObject(UserProfile::class.java)
                    } else {
                        _userProfile.value = UserProfile(
                            uid = user.uid,
                            email = user.email,
                            displayName = user.displayName,
                            photoUrl = user.photoUrl?.toString()
                        )
                    }
                }
                .addOnFailureListener {
                    _userProfile.value = null
                }
        }
    }

    fun updateProfile(profile: UserProfile, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        user?.let {
            db.collection("users").document(user.uid).set(profile)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
    }

    // Example method to update likes
//    fun updateLikes(newLikes: Int) {
//        val user = auth.currentUser
//        user?.let {
//            val userProfile = _userProfile.value
//            userProfile?.let { profile ->
//                val updatedProfile = profile.copy(likes = newLikes)
//                updateProfile(updatedProfile) { success ->
//                    if (success) {
//                        _userProfile.value = updatedProfile
//                    }
//                }
//            }
//        }
//    }
//
//    // Example method to update comments
//    fun updateComments(newComments: Int) {
//        val user = auth.currentUser
//        user?.let {
//            val userProfile = _userProfile.value
//            userProfile?.let { profile ->
//                val updatedProfile = profile.copy(comments = newComments)
//                updateProfile(updatedProfile) { success ->
//                    if (success) {
//                        _userProfile.value = updatedProfile
//                    }
//                }
//            }
//        }
//    }
}
