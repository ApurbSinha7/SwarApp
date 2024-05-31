package com.android.swar.model

data class NewPost(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
//    val userPhotoUrl: String? = null,
    val audioUrl: String = "",
    val storageRef: String = "",
    val caption: String = "",
    val duration: String = "",
//    val timestamp: FieldValue? = null,
    val likes: Int = 0,
    val comments: Int = 0
)