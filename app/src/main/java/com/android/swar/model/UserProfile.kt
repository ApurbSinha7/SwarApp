package com.android.swar.model

data class UserProfile(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val bio: String? = null,
//    val likes: Int = 0,  // Custom field for likes
//    val comments: Int = 0 // Custom field for comments
)
