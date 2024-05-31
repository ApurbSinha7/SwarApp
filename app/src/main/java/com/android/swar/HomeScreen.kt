package com.android.swar

//@Composable
//fun HomeScreen(modifier: Modifier = Modifier,
//               typography: Typography, viewModel: AuthViewModel, navController: NavController
//) {
//    Column(
//        modifier = modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    )  {
//        Text(
//            text = "Nigga Home",
//            style = typography.bodyLarge,
//            modifier = Modifier.padding(top = 48.dp, bottom = 32.dp)
//        )
//        Button(
//            onClick = {
//                viewModel.logout()
//            },
//            modifier = Modifier.padding(16.dp)
//                .clickable { navController.navigate("login_screen") }
//        ) {
//            Text(text = "Log Out")
//        }
//    }
//}

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.swar.model.NewPost
import com.android.swar.viewModel.AudioUploadViewModel
import com.android.swar.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), uploadViewModel: AudioUploadViewModel = viewModel(), modifier : Modifier,
               navController: NavController, typography: Typography) {
    val audioUploadViewModel = viewModel<AudioUploadViewModel>()
    val audioItems by viewModel.getAudioItems().collectAsState(initial = emptyList())
    val currentlyPlayingId by viewModel.currentlyPlayingId.collectAsState()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var userid = ""
    auth.currentUser?.let { userid = it.uid }


    val posts by viewModel.posts.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { uploadViewModel.startRecording() }) {
                Icon(
                    if (audioUploadViewModel.isRecording.value) Icons.Default.Add else Icons.Default.Close,
                    contentDescription = if (audioUploadViewModel.isRecording.value) "Stop Recording" else "Start Recording"
                )
            }
        }, modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    if (dragAmount > 0) {
                        navController.navigate("right_screen")
                    }
                }
            }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()) {
            // currently changing items(audioItems) to items(posts), change back to get old audios
            items(posts) { item ->
                    AudioItem(
                        audioItem = item,
                        isPlaying = currentlyPlayingId == item.id,
                        onLike = { viewModel.hasUserLikedPost(item.id, userid ) },
                        onPlayPause = {
                            if (currentlyPlayingId == item.id) {
                                viewModel.stopPlaying()
                            } else {
                                viewModel.playAudio(item.id, item.audioUrl)
                            }
                        },
                    )
                }
        }
    }

    if (uploadViewModel.isRecording.value) {
        RecordingDialog(
            onStopRecording = { uploadViewModel.stopRecording() }
        )
    }
}
// changing audioItem: AudioItem to NewPosts change back if needed
@Composable
fun AudioItem(audioItem: NewPost, onLike: (String) -> Unit, onPlayPause: () -> Unit, isPlaying: Boolean ) {
//    val mediaPlayer = remember { MediaPlayer() }
//    var isPlaying by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(onClick = {onPlayPause()
//                if (isPlaying) {
//                    viewModel.stopPlaying()
//                    isPlaying = false
//                } else {
//                    viewModel.playAudio(audioItem.audioUrl) {
//                        isPlaying = false
//                    }
//                    isPlaying = true
//                }
            }) {
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
            IconButton(onClick = { onLike(audioItem.id) }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
            }
            Text(text = "${audioItem.likes}", modifier = Modifier.padding(horizontal = 8.dp))
        }
}

@Composable
fun RecordingDialog(onStopRecording: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text("Recording...") },
        text = { Text("Recording audio, tap stop to finish.") },
        confirmButton = {
            Button(onClick = onStopRecording) {
                Text("Stop")
            }
        }
    )
}
/*
@Composable
fun LikersList(postId: String) {
    val context = LocalContext.current
    var likers by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(postId) {
        fetchLikers(postId) { userIds ->
            likers = userIds
        }
    }

    LazyColumn {
        items(likers) { userId ->
            Text(text = userId) // Replace with user details fetching logic if needed
        }
    }
}*/

//@Composable
//fun CommentsList(postId: String) {
//    val context = LocalContext.current
//    var comments by remember { mutableStateOf(listOf<Comment>()) }
//
//    LaunchedEffect(postId) {
//        fetchComments(postId) { commentList ->
//            comments = commentList
//        }
//    }
//
//    LazyColumn {
//        items(comments) { comment ->
//            CommentItem(comment)
//        }
//    }
//}
//
//@Composable
//fun CommentItem(comment: Comment) {
//    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
//        Image(
//            painter = rememberImagePainter(comment.userPhotoUrl),
//            contentDescription = null,
//            modifier = Modifier.size(40.dp).clip(CircleShape)
//        )
//        Column(modifier = Modifier.padding(start = 8.dp)) {
//            Text(text = comment.userName, fontWeight = FontWeight.Bold)
//            Text(text = comment.commentText)
//        }
//    }
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RightScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    if (dragAmount < 0) {
                        navController.popBackStack()
                    }
                }
            }
    ) {
        // Your Right Screen content here
        ProfileScreen(navController = navController)
    }
}
