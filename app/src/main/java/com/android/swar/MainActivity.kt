package com.android.swar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.android.swar.ui.theme.SwarTheme
import com.android.swar.ui.theme.Typography


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        setContent {
            SwarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    LoginScreen(modifier = Modifier.padding(innerPadding), typography = Typography, title = "Swar")
                }
            }
        }

//        val sendButton: Button = findViewById(R.id.send_button)
//        sendButton.setOnClickListener {view ->
//            sendData(view)
//        }
//    }
//        fun sendData(view: View) {
//        // Write a message to the database
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//
//        myRef.setValue("Hello, World!")
//
//        Toast.makeText(view.context, "Data sent successfully", Toast.LENGTH_SHORT).show()
//    }

    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier)
//
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    SwarTheme {
//        Greeting("Android")
//    }
//}