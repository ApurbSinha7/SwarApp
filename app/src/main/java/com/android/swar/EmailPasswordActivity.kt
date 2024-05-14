package com.android.swar
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth
// Initialize Firebase Auth
auth = Firebase.auth
public override fun onStart() {
    super.onStart()
    // Check if user is signed in (non-null) and update UI accordingly.
    val currentUser = auth.currentUser
    if (currentUser != null) {
        reload()
    }
}

class EmailPasswordActivity {


}