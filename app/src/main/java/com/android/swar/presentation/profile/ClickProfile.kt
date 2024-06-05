package com.android.swar.presentation.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.swar.model.NewPost
import com.android.swar.model.UserProfile
import com.android.swar.ui.theme.AppTheme
import com.android.swar.viewModel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ClickProfile(viewModel: HomeViewModel = viewModel(),  userId: String, navController: NavController) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var userPosts by remember { mutableStateOf<List<NewPost>>(emptyList()) }
    val db = FirebaseFirestore.getInstance()


//    private val _posts = MutableStateFlow<List<NewPost>>(emptyList())
//    val posts: StateFlow<List<NewPost>> get() = _posts


    val currentlyPlayingId by viewModel.currentlyPlayingId.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val usersPosts = posts.filter { it.userId == userId }
    if(userId!= null){
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userProfile = document.toObject(UserProfile::class.java)
                    Log.d("TAG", "ClickProfile: $userProfile")
                }
            }
    }
    else
        Log.d("TAG", "ClickProfile: null")

                // this is before i used posts from viewmodel use this when fetching
                // only user posts and create a likeslistener for it
                // check homeviewmodel and do the same for liekslistener
                // and postlistener and make it livedata stateflow

//    LaunchedEffect(userId) {
//        // Fetch user profile
//        db.collection("users").document(userId).get()
//            .addOnSuccessListener { document ->
//                Log.d("TAG", "ClickProfile: success")
//                if (document.exists()) {
//                    userProfile = document.toObject(UserProfile::class.java)
//                    Log.d("TAG", "ClickProfile: $userProfile")
//                }
//            }
//
//        // Fetch user posts
//        db.collection("posts").whereEqualTo("userId", userId).get()
//            .addOnSuccessListener { documents ->
//                Log.d("TAG", "ClickProfile: posts")
//                val posts = documents.mapNotNull { it.toObject(NewPost::class.java) }
//                userPosts = posts
//
////                _posts.value = posts
//            }
//    }

    userProfile?.let { profile ->
        Log.d("TAG", "ClickProfile: what?")
        AppTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // 16.dp
            ) {
                // User info section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = profile.displayName ?: "No Name", fontSize = 24.sp)
                        Text(text = profile.email ?: "No Email", fontSize = 16.sp)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = profile.bio ?: "No Bio", fontSize = 16.sp)
                    }
                }

                // User posts section
                Text(
                    text = "Posts",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn {
                    items(usersPosts) { post -> // change to userPosts on error fix check typo
                        AudioItem(
                            audioItem = post,
                            isPlaying = currentlyPlayingId == post.id,
                            onLike = { viewModel.likeOrUnlikePost(post) },
                            onPlayPause = {
                                if (currentlyPlayingId == post.id) {
                                    viewModel.stopPlaying()
                                } else {
                                    viewModel.playAudio(post.id, post.audioUrl)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
    }
}


@Composable
fun AudioItem(audioItem: NewPost, onLike: (NewPost) -> Unit, onPlayPause: () -> Unit, isPlaying: Boolean ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
//            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(onClick = {onPlayPause()
            }
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = audioItem.userName, fontWeight = FontWeight.Bold)
                Text(text = audioItem.caption)
            }
            Text(text = audioItem.duration, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = { onLike(audioItem) }) {
                Icon(imageVector = if (audioItem.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if(audioItem.liked)
                        "Unlike" else  "Like",
                    tint = if (audioItem.liked) Color.Red else Color.Gray)
            }
            Text(text = "${audioItem.likesCount}", modifier = Modifier.padding(horizontal = 8.dp)) // had "${audioItem.likesCount}""
        }
    }
}

//@Composable
//fun PostCard(post: NewPost, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(bottom = 8.dp)
//            .clickable(onClick = onClick)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(8.dp)
//        ) {
//            IconButton(onClick = {onPlayPause()
//            }) {
//                Icon(
//                    if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
//                    contentDescription = if (isPlaying) "Pause" else "Play"
//                )
//            }
//            Column(modifier = Modifier.weight(1f)) {
//                Text(text = audioItem.userName, fontWeight = FontWeight.Bold)
//                Text(text = audioItem.caption)
//            }
//            Text(text = audioItem.duration, modifier = Modifier.padding(horizontal = 8.dp))
//            IconButton(onClick = { onLike(audioItem) }) {
//                Icon(imageVector = if (audioItem.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                    contentDescription = if(audioItem.liked)
//                        "Unlike" else  "Like",
//                    tint = if (audioItem.liked) Color.Red else Color.Gray)
//            }
//            Text(text = "${audioItem.likes}", modifier = Modifier.padding(horizontal = 8.dp))
//        }
//    }
//}

