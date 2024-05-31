package com.android.swar.model

data class AudioItem(
    val id: String,
    val userName: String,
    val caption: String,
    val storageRef: String,
    val audioUrl: String,
    val duration: String,
//    val uploadTime: Timestamp,
    val likes: Long
)
