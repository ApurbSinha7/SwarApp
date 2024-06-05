package com.android.swar.model

data class NewPost(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val fileName: String = "",
//    val userPhotoUrl: String? = null,
    val audioUrl: String = "",
    val storageRef: String = "",
    val caption: String = "",
    val duration: String = "",
//    val timestamp: FieldValue? = null,
    val likesCount: Int = 0,
    val comments: Int = 0,
    var liked: Boolean = false // New field to track liked status
)