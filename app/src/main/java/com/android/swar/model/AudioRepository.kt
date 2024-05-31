package com.android.swar.model

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AudioRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAudioItems(): Flow<List<AudioItem>> = callbackFlow {
        val listener = firestore.collection("audioItems")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.map { doc ->
                        AudioItem(
                            id = doc.id,
                            userName = doc.getString("userName") ?: "",
                            caption = doc.getString("caption") ?: "",
                            audioUrl = doc.getString("audioUrl") ?: "",
                            duration = doc.getString("duration") ?: "",
                            likes = doc.getLong("likes") ?: 0,
                            storageRef = doc.getString("storageRef") ?: "",
//                            uploadTime = doc.getTimestamp("uploadTime")
                        )
                    }
                    trySend(items)
                }
            }
        awaitClose { listener.remove() }
    }
}
