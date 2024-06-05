package com.android.swar.model
data class AudioPost(
    val post: NewPost,
    val timeDay: TimeDay
)

data class TimeDay(val dayMonth: String, val time: String)