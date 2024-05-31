package com.android.swar.model

data class Like(
    val postId: String = "",
    val userId: String = ""
)

data class Comment(
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String = "",
    val commentText: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)