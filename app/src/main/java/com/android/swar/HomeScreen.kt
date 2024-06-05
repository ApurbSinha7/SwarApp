package com.android.swar

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.swar.model.NewPost
import com.android.swar.ui.theme.AppTheme
import com.android.swar.viewModel.AudioUploadViewModel
import com.android.swar.viewModel.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    uploadViewModel: AudioUploadViewModel = viewModel(),
    modifier: Modifier,
    navController: NavController,
    typography: Typography
) {
    val audioUploadViewModel = viewModel<AudioUploadViewModel>()
//    val audioItems by viewModel.getAudioItems().collectAsState(initial = emptyList())
    val currentlyPlayingId by viewModel.currentlyPlayingId.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val thisUser = auth.currentUser
    val authViewModel = AuthViewModel(navController = navController)


    val posts by viewModel.posts.collectAsState()
    val timeNDay by viewModel.timeNday.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    val userProfile = viewModel.userProfile

//    val listState = rememberLazyListState()
//    val coroutineScope = rememberCoroutineScope()
//    var topBarVisible by remember { mutableStateOf(true) }
//
//    LaunchedEffect(listState) {
//        coroutineScope.launch {
//            snapshotFlow { listState.firstVisibleItemIndex }
//                .collect { index ->
//                    topBarVisible = index == 0
//                }
//        }
//    }
    val listState = rememberLazyListState()
    val topBarVisible = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val lastScrollIndex = remember { mutableIntStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var topBarOffsetHeightPx by remember { mutableFloatStateOf(0f) }
    var recordDialog = remember { mutableStateOf(false) }

    val topBarHeight = 5.dp
    val topBarHeightPx = with(LocalDensity.current) { topBarHeight.toPx() }

    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex }
//            .collect { index ->
//                coroutineScope.launch {
//                    if (index > lastScrollIndex.value) {
//                        topBarVisible.value = false
//                    } else if (index < lastScrollIndex.value) {
//                        topBarVisible.value = true
//                    }
//                    lastScrollIndex.value = index
//                }
//            }
//    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { scrollOffset ->
                coroutineScope.launch {
                    val delta = scrollOffset - topBarOffsetHeightPx
                    topBarOffsetHeightPx =
                        (topBarOffsetHeightPx + delta).coerceIn(-topBarHeightPx, 0f)
                }
            }
    }
    AppTheme {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Replace with actual user profile info
                    Text(
                        text = userProfile?.displayName ?: "Unknown User",
                        style = typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = userProfile?.email ?: "Unknown Email",
                        style = typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = userProfile?.bio ?: "No bio available",
                        style = typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    // Navigation items
                    TextButton(
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("profile_screen/${viewModel.userProfile?.uid}")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("My Profile")
                    }
                    // Add more navigation items here
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("another_screen")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Another Screen")
                    }
                    Button(
                        onClick = { navController.navigate("right_screen") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Edit Profile")
                    }
                    Button(
                        onClick = { authViewModel.logout() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Sign out")
                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    if (topBarVisible.value) {
                        TopAppBar(
                            title = { Text("Swar", style = typography.headlineLarge) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(x = 0, y = topBarOffsetHeightPx.roundToInt()) }
                                .zIndex(1f),
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { recordDialog.value = true },
                        shape = CircleShape,
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent,//MaterialTheme.colorScheme.onPrimary
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) { //{ uploadViewModel.startRecording() }
                        Image(
                            modifier = Modifier, // Adjust padding as needed
                            painter = painterResource(id = R.drawable.sl2),
                            contentDescription = "Your icon description"
                        )
                    }
                }, modifier = Modifier
                    .fillMaxSize()
//            .pointerInput(Unit) {
//                detectHorizontalDragGestures { _, dragAmount ->
//                    if (dragAmount > 50) {
//                        navController.navigate("right_screen")
//                    }
//                }
//            }
            ) { innerPadding ->
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
//            Column {
//                if (topBarVisible.value) {
//                    TopAppBar(
//                        title = { Text("Swar", fontSize = 20.sp) },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .offset { IntOffset(x = 0, y = topBarOffsetHeightPx.roundToInt()) }
//                            .zIndex(1f),
//                        scrollBehavior = scrollBehavior,
//                    )
//                }
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = {
                            viewModel.fetchPosts()
                            swipeRefreshState.isRefreshing = false // Stop the refresh indicator
                        }
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(5.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {

                            // currently changing items(audioItems) to items(posts), change back to get old audios
                            items(posts) { item ->
                                AudioItem(
                                    audioItem = item,
                                    isPlaying = currentlyPlayingId == item.id,
                                    onLike = { viewModel.likeOrUnlikePost(item) },
                                    navController = navController,
                                    onPlayPause = {
                                        if (currentlyPlayingId == item.id) {
                                            viewModel.stopPlaying()
                                        } else {
                                            viewModel.playAudio(item.id, item.audioUrl)
                                        }
                                    },
                                    onDelete ={ item ->
                                        uploadViewModel.deletePost(item){mes ->
                                            Toast.makeText(context, mes, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
//            }
                }
                if (recordDialog.value) {
                    RecordingDialog(
                        onStartStopRecording = {
                            if (!audioUploadViewModel.isRecording.value)
                                audioUploadViewModel.startRecording()
                            else {
                                audioUploadViewModel.stopRecording()
                                recordDialog.value = false
                            }
                        },
                        //  rec = recordDialog.value,
                        rec = uploadViewModel.isRecording.value,
                        onSaveCaption = { caption ->
                            audioUploadViewModel.captionFromHst = caption
                            // Save the caption to the database or handle it accordingly
                        },
                        onDismiss = { recordDialog.value = false }
                    )
                }

//        if (uploadViewModel.isRecording.value) {
//            RecordingDialog(
//                onStopRecording = { uploadViewModel.stopRecording() }
//            )
//        }
            }
        }
    )
}
}

// changing audioItem: AudioItem to NewPosts change back if needed
@Composable
fun AudioItem(
    audioItem: NewPost,
    onLike: (NewPost) -> Unit,
    onPlayPause: () -> Unit,
    onDelete: (NewPost) -> Unit,
    isPlaying: Boolean,
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    var alertNeeded by remember { mutableStateOf(false) }

    AppTheme {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.padding(8.dp)
//    ) {
//        IconButton(onClick = {
//            onPlayPause()
//        }) {
//            Icon(
//                if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
//                contentDescription = if (isPlaying) "Pause" else "Play"
//            )
//        }
//        Column(modifier = Modifier.weight(1f)) {
//            ClickableText(text = AnnotatedString(audioItem.userName),
//                style = TextStyle(fontWeight = FontWeight.Bold),
//                onClick = { navController.navigate("profile_screen/${audioItem.userId}") })
//
//            Text(text = audioItem.caption, modifier =Modifier.padding(top = 4.dp), fontSize = 14.sp)
//        }
//        Text(text = audioItem.duration, modifier = Modifier.padding(horizontal = 8.dp))
//        IconButton(onClick = { onLike(audioItem) }) {
//            Icon(
//                imageVector = if (audioItem.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                contentDescription = if (audioItem.liked)
//                    "Unlike" else "Like",
//                tint = if (audioItem.liked) Color.Red else Color.Gray
//            )
//        }
//        Text(text = "${audioItem.likesCount}", modifier = Modifier.padding(horizontal = 8.dp))
//    }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { /* Navigate to detailed view */ },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    IconButton(onClick = { onPlayPause() }) {
                        Icon(
                            if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp),
                            tint = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(text = audioItem.duration, style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        Text(
                            text = audioItem.userName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.clickable { navController.navigate("profile_screen/${audioItem.userId}") }
                        )


//                        Text(text = viewModel.timeNday.time, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = audioItem.caption,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { onLike(audioItem) }) {
                        Icon(
                            imageVector = if (audioItem.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (audioItem.liked) "Unlike" else "Like",
                            tint = if (audioItem.liked) MaterialTheme.colorScheme.error else Color.Gray
                        )
                    }
                    Text(
                        text = "${audioItem.likesCount}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if(audioItem.userId == viewModel.userProfile?.uid){
                    IconButton(onClick = {
                        alertNeeded = true
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Post")
                    }}
            }
            if(alertNeeded){
                Alert(onDelete, audioItem, onDismiss = { alertNeeded = false })
            }
        }
    }

}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RecordingDialog(onStartStopRecording: () -> Unit, rec: Boolean, onSaveCaption: (MutableState<String>) -> Unit) {
//    val caption = remember { mutableStateOf("") }
//   // var rec by remember { mutableStateOf(false) }
//    AlertDialog(
//        onDismissRequest = { /* Do nothing */ },
//        title = { if(rec) Text("Stop Playing") else Text("Record your Swar") },
//        text = {
//            Column {
//                Text("Enter a caption for this.")
//                TextField(
//                    value = caption.value,
//                    onValueChange = { caption.value = it },
//                    label = { Text("Caption") },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = TextFieldDefaults.textFieldColors(
//                        focusedIndicatorColor = Color.Blue,
//                        unfocusedIndicatorColor = Color.Gray
//                    )
//                )
//            }
//        },
//        confirmButton = {
//            Button(onClick = {
//                onStartStopRecording()
//                onSaveCaption(caption)
//            }) {
//                if (rec) Text("Stop") else Text("Record")
//            }
//        }
//    )
//}

@Composable
fun Alert(onDelete: (NewPost) -> Unit, audioItem: NewPost, onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = {  },
        title = {Text("Are you sure?")},
        confirmButton = { Button(onClick = {
            onDelete(audioItem)
            onDismiss()
        }) {
            Text("Delete", color = Color.Red)
        } },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Back", color = Color.Gray)
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingDialog(
    onStartStopRecording: () -> Unit,
    rec: Boolean,
    onSaveCaption: (MutableState<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val caption = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(if (rec) "Stop Recording" else "Record your Swar")
        },
        text = {
            Column {
                Text("Enter a caption for this recording:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = caption.value,
                    onValueChange = { caption.value = it },
                    label = { Text("Caption") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onStartStopRecording()
                onSaveCaption(caption)
            }) {
                Text(if (rec) "Stop" else "Record")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Close")
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
