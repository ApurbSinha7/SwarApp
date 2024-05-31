package com.android.swar
//
//import android.app.Application
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//import com.google.firebase.storage.FirebaseStorage
//
//class MyApplication : Application() {
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var storage: FirebaseStorage
//    private lateinit var firestore: FirebaseFirestore
//
//    override fun onCreate() {
//        super.onCreate()
//        Firebase.initializeApp(this)
//
//        auth = FirebaseAuth.getInstance()
//        storage = FirebaseStorage.getInstance()
//        firestore = FirebaseFirestore.getInstance()
//    }
//
//    fun getAuth(): FirebaseAuth {
//        return auth
//    }
//
//    fun getStorage(): FirebaseStorage {
//        return storage
//    }
//
//    fun getFirestore(): FirebaseFirestore {
//        return firestore
//    }
//}
