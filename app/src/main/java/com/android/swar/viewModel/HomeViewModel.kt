package com.android.swar.viewModel
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.swar.model.AudioItem
import com.android.swar.model.NewPost
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingUrl: String? = null

    private val _currentlyPlayingId = MutableStateFlow<String?>(null)
    val currentlyPlayingId: StateFlow<String?> get() = _currentlyPlayingId

    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")
    private val likesCollection = db.collection("likes")

    private val _posts = MutableStateFlow<List<NewPost>>(emptyList())
    val posts: StateFlow<List<NewPost>> get() = _posts

    init {
        fetchPosts()
    }

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
                            audioUrl = Uri.decode(doc.getString("audioUrl") ?: ""),
                            duration = doc.getString("duration") ?: "",
//                            uploadTime = doc.getTimestamp("uploadTime"),
                            storageRef = doc.getString("storageRef") ?: "",
                            likes = doc.getLong("likes")?: 0
                        )
                    }
                    trySend(items)
                }
            }
        awaitClose { listener.remove() }
    }

    fun likeAudioItem(itemId: String) {
        val docRef = firestore.collection("audioItems").document(itemId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val newLikes = snapshot.getLong("likes")?.plus(1) ?: 1
            transaction.update(docRef, "likes", newLikes)
        }
    }

    fun hasUserLikedPost(postId: String, userId: String) {
        val likeQuery = db.collection("likes")
            .whereEqualTo("postId", postId)
            .whereEqualTo("userId", userId)
            .limit(1)

        likeQuery.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // User has already liked the post
                // Call unlikePost function

                unlikePost(postId, userId)
            } else {
                // User hasn't liked the post yet
                // Call likePost function
                likePost(postId, userId)
            }
        }.addOnFailureListener {
            // Handle the error
            Log.d("HomeViewModel", "Snapshot not found")
        }
    }


    private fun likePost(postId: String, userId: String) {
        val likeId = UUID.randomUUID().toString()
        val likeData = mapOf(
            "postId" to postId,
            "userId" to userId
        )

        val likeRef = db.collection("likes").document(likeId)
        val postRef = db.collection("posts").document(postId)

        db.runBatch { batch ->
            batch.set(likeRef, likeData)
            batch.update(postRef, "likesCount", FieldValue.increment(1))
        }.addOnSuccessListener {
            // Successfully liked the post
        }.addOnFailureListener {
            // Handle the error
        }
    }

    private fun unlikePost(postId: String, userId: String) {
        val likeQuery = db.collection("likes")
            .whereEqualTo("postId", postId)
            .whereEqualTo("userId", userId)
            .limit(1)

        likeQuery.get().addOnSuccessListener { querySnapshot ->
            val likeDocument = querySnapshot.documents.firstOrNull()
            if (likeDocument != null) {
                val likeRef = likeDocument.reference
                val postRef = db.collection("posts").document(postId)

                db.runBatch { batch ->
                    batch.delete(likeRef)
                    batch.update(postRef, "likesCount", FieldValue.increment(-1))
                }.addOnSuccessListener {
                    // Successfully unliked the post
                }.addOnFailureListener {
                    // Handle the error
                }
            }
        }
    }


    fun playAudio(id: String, url: String) {
        stopPlaying()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            start()
            setOnCompletionListener {
                it.reset()
                _currentlyPlayingId.value = null
            }
        }
        _currentlyPlayingId.value = id
    }

    fun stopPlaying() {
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaPlayer = null
        _currentlyPlayingId.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPlaying()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            postsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val postsList = snapshot.documents.mapNotNull { it.toObject(NewPost::class.java) }
                        _posts.value = postsList
                    }
                }
        }
    }

//    fun fetchPostsWithLikesAndComments() {
//        db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).get()
//            .addOnSuccessListener { postsSnapshot ->
//                val posts = postsSnapshot.toObjects(Post::class.java)
//                posts.forEach { post ->
//                    // Fetch likes for the post
//                    db.collection("likes").whereEqualTo("postId", post.postId).get()
//                        .addOnSuccessListener { querySnapshot ->
//                        val userIds = querySnapshot.documents.mapNotNull { it.getString("userId") }
//                        onResult(userIds)
//                        }
//                        .addOnFailureListener {
//                        // Handle the error
//                        onResult(emptyList())
//                        }
//
//                    // Fetch comments for the post
//                    db.collection("comments").whereEqualTo("postId", post.postId)
//                    .orderBy("timestamp", Query.Direction.ASCENDING)
//                    .get()
//                        .addOnSuccessListener { commentsSnapshot ->
//                            val comments = commentsSnapshot.toObjects(Comment::class.java)
//                            // Do something with the comments
//                             onResult(comments)
//                        }
//                        .addOnFailureListener {
//                            // Handle the error
//                            onResult(emptyList())
//                        }
//                }
//            }
//    }
}
