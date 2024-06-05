package com.android.swar.viewModel
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.swar.model.NewPost
import com.android.swar.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HomeViewModel : ViewModel() {
//    private val firestore = FirebaseFirestore.getInstance()

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingUrl: String? = null

    private val _currentlyPlayingId = MutableStateFlow<String?>(null)
    val currentlyPlayingId: StateFlow<String?> get() = _currentlyPlayingId

    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")
    private val likesCollection = db.collection("likes")

    private val _posts = MutableStateFlow<List<NewPost>>(emptyList())
    private val _timeNday = MutableStateFlow<List<TimeDay?>>(emptyList())
    val posts: StateFlow<List<NewPost>> get() = _posts
    val timeNday: StateFlow<List<TimeDay?>> get() = _timeNday

    private var postsListener: ListenerRegistration? = null
    private var likesListener: ListenerRegistration? = null

    var userProfile: UserProfile? = null


    private val userId = FirebaseAuth.getInstance().currentUser?.uid


    init {
        fetchPosts()
        setupPostsListener()
        setupLikesListener()
        getCurrentProfile()
    }

    private fun getCurrentProfile(){
        if(userId!= null){
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userProfile = document.toObject(UserProfile::class.java)
                        Log.d("TAG", "ClickProfile: $userProfile")
                    }
                }
        }
    }

    private fun setupPostsListener() {
        postsListener = postsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { postsSnapshot, error ->
                if (error != null) {
                    Log.e("HomeViewModel", "Error fetching posts", error)
                    return@addSnapshotListener
                }
                fetchPosts()
            }
    }

    private fun setupLikesListener() {
        if (userId == null) return

        likesListener = likesCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { likesSnapshot, error ->
                if (error != null) {
                    Log.e("HomeViewModel", "Error fetching likes", error)
                    return@addSnapshotListener
                }
                fetchPosts()
            }
    }

    fun fetchPosts() {
        if (userId == null) return

        viewModelScope.launch {
            try {
                val postsDeferred = async {
                    postsCollection.orderBy("timestamp", Query.Direction.DESCENDING).get().await()
                }
                val likesDeferred = async {
                    likesCollection.whereEqualTo("userId", userId).get().await()
                }

                val postsSnapshot = postsDeferred.await()
                val likesSnapshot = likesDeferred.await()

                val likedPosts = likesSnapshot.documents.mapNotNull { it.getString("postId") }

                var timay: TimeDay? = null

                val postsList = postsSnapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(NewPost::class.java)
                    post?.let {
                        it.liked = likedPosts.contains(post.id)
                        post
                    }
                }

                _timeNday.value = listOf(timay)
                _posts.value = postsList
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching posts or likes", e)
            }
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }
    private fun formatDateToDayMonth(date: Date): String {
        val dayMonthFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        return dayMonthFormat.format(date)
    }

    // Function to format time in HH:MM format
    private fun formatTime(date: Date): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(date)
    }

    data class TimeDay(val dayMonth: String, val time: String)


    fun likeOrUnlikePost(post: NewPost) {
        userId?.let { uid ->
            if (post.liked) {
                unlikePost(post.id, uid)
            } else {
                likePost(post.id, uid)
            }
            post.liked = !post.liked
            _posts.value = _posts.value // Trigger recomposition
        }
    }



//    private fun checkLikesForPosts() {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//        viewModelScope.launch {
//            _posts.value.forEach { post ->
//                likesCollection
//                    .whereEqualTo("postId", post.id)
//                    .whereEqualTo("userId", userId)
//                    .limit(1)
//                    .get()
//                    .addOnSuccessListener { querySnapshot ->
//                        post.liked = !querySnapshot.isEmpty
//                        _posts.value = _posts.value // Trigger recomposition
//                    }
//            }
//        }
//    }

//    fun getAudioItems(): Flow<List<AudioItem>> = callbackFlow {
//        val listener = firestore.collection("audioItems")
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    close(e)
//                    return@addSnapshotListener
//                }
//                if (snapshot != null) {
//                    val items = snapshot.documents.map { doc ->
//                        AudioItem(
//                            id = doc.id,
//                            userName = doc.getString("userName") ?: "",
//                            caption = doc.getString("caption") ?: "",
//                            audioUrl = Uri.decode(doc.getString("audioUrl") ?: ""),
//                            duration = doc.getString("duration") ?: "",
////                            uploadTime = doc.getTimestamp("uploadTime"),
//                            storageRef = doc.getString("storageRef") ?: "",
//                            likes = doc.getLong("likes")?: 0
//                        )
//                    }
//                    trySend(items)
//                }
//            }
//        awaitClose { listener.remove() }
//    }

//    fun likeAudioItem(itemId: String) {
//        val docRef = firestore.collection("audioItems").document(itemId)
//        firestore.runTransaction { transaction ->
//            val snapshot = transaction.get(docRef)
//            val newLikes = snapshot.getLong("likes")?.plus(1) ?: 1
//            transaction.update(docRef, "likes", newLikes)
//        }
//    }

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
        postsListener?.remove()
        likesListener?.remove()
        stopPlaying()
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
