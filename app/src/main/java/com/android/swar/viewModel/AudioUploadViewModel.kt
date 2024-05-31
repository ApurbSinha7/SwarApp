package com.android.swar.viewModel

import android.content.ContentValues.TAG
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.swar.model.NewPost
import com.android.swar.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.util.UUID

class AudioUploadViewModel : ViewModel() {
    private var mediaRecorder: MediaRecorder? = null
    var isRecording = mutableStateOf(false)
    private var audioFile: File? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
        }

        try {
            audioFile = File.createTempFile("audio_", ".3gp")
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            isRecording.value = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        mediaRecorder = null
        isRecording.value = false
        audioFile?.let { uploadAudio(it) }
    }

    private fun uploadAudio(file: File) {
        val storageRef = FirebaseStorage.getInstance().reference.child("audios/${file.name}")
        val uploadTask = storageRef.putFile(Uri.fromFile(file))

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Save the download URL to Firestore
//                saveAudioMetadata(uri.toString(), file.name)
                createPost(uri.toString(), "Caption for audio ${file.name}"){ onResult ->
                    Log.d(TAG, "uploadAudio: $onResult")}
            }.addOnFailureListener {
                // Handle unsuccessful uploads
            }
        }
    }

        private fun saveAudioMetadata(audioUrl: String, fileName: String) {
            // Assuming you have a Firestore collection called "audioItems"
            val firestore = FirebaseFirestore.getInstance()
            val sanitizedUrl = Uri.encode(audioUrl)
            val audioItem = hashMapOf(
                "userName" to "User Name", // Replace with actual user's name
                "caption" to "Caption for audio $fileName",
                "storageRef" to "audios/$fileName",
                "audioUrl" to sanitizedUrl,
                "duration" to "6:31",
//            "uploadTime" to TIMESTAMP1,
                "likes" to 0
            )
            firestore.collection("audioItems").add(audioItem)
        }

        private fun createPost(audioUrl: String, caption: String, onResult: (Boolean) -> Unit) {
            val user = auth.currentUser
            user?.let {
                val userId = it.uid
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userProfile = document.toObject(UserProfile::class.java)
                            val post = NewPost(
                                id = UUID.randomUUID().toString(),
                                userId = userId,
                                userName = userProfile?.displayName ?: "",
//                            userPhotoUrl = userProfile?.photoUrl,
                                audioUrl = audioUrl,
                                caption = caption,
//                                timestamp = FieldValue.serverTimestamp()
                            )
                            val postMap = hashMapOf<String, Any>(
                                "id" to post.id,
                                "userId" to post.userId,
                                "userName" to post.userName,
                                "audioUrl" to post.audioUrl,
                                "caption" to post.caption,
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                            db.collection("posts").document(post.id).set(postMap)
                                .addOnSuccessListener { onResult(true) }
                                .addOnFailureListener { onResult(false) }
                        } else {
                            onResult(false)
                        }
                    }
                    .addOnFailureListener { onResult(false) }
            }
        }



//    fun addComment(postId: String, userId: String, userName: String, userPhotoUrl: String, commentText: String) {
//        val commentId = UUID.randomUUID().toString()
//        val commentData = mapOf(
//            "postId" to postId,
//            "userId" to userId,
//            "userName" to userName,
//            "userPhotoUrl" to userPhotoUrl,
//            "commentText" to commentText,
//            "timestamp" to FieldValue.serverTimestamp()
//        )
//
//        val commentRef = db.collection("comments").document(commentId)
//        val postRef = db.collection("posts").document(postId)
//
//        db.runBatch { batch ->
//            batch.set(commentRef, commentData)
//            batch.update(postRef, "commentsCount", FieldValue.increment(1))
//        }.addOnSuccessListener {
//            // Successfully added the comment
//        }.addOnFailureListener {
//            // Handle the error
//        }
//    }


}
