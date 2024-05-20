package com.android.swar

//import com.android.swar.ui.theme.ComposeGoogleSignInCleanArchitectureTheme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.swar.presentation.profile.ProfileScreen
import com.android.swar.presentation.sign_in.GoogleAuthUiClient
import com.android.swar.presentation.sign_in.SignInViewModel
import com.android.swar.ui.theme.ComposeGoogleSignInCleanArchitectureTheme
import com.android.swar.ui.theme.SwarTheme
import com.android.swar.ui.theme.Typography
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private val auth: FirebaseAuth = FirebaseAuth.getInstance()


class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                androidx.lifecycle.compose.LocalLifecycleOwner provides this@MainActivity,
            ) {
                SwarTheme {

                    val SPLASH_SCREEN_DURATION = 3000L // 3000 milliseconds = 3 seconds

//                val splashShown = remember { mutableStateOf(true) }
                    val navController = rememberNavController()
                    val authviewModel = AuthViewModel(navController = navController)
                    AuthManager(navController = navController)

//                val coroutineScope = rememberCoroutineScope()

//                LaunchedEffect(Unit) {
//                    coroutineScope.launch {
//                        delay(SPLASH_SCREEN_DURATION)
//                        splashShown.value = false
//                    }
//                }

                    ComposeGoogleSignInCleanArchitectureTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            NavHost(
                                navController = navController, startDestination = "login_screen"
//                            startDestination = determineStart()
                            ) {
//                            composable("splash_screen") {
//                                if (splashShown.value) {
//                                    SplashScreen()
//                                } else {
//                                    // Navigate to the login screen
//                                    navController.navigate("login_screen") {
//                                        // Pop splash screen from back stack
//                                        popUpTo("splash_screen") { inclusive = true }
//                                    }
//                                }
//                            }
                                composable("login_screen") {
                                    val viewModel = viewModel<SignInViewModel>()
                                    val state by viewModel.state.collectAsStateWithLifecycle()

                                    LaunchedEffect(key1 = Unit) {
                                        if (googleAuthUiClient.getSignedInUser() != null) {
                                            navController.navigate("profile")
                                        }
                                    }

                                    val launcher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signInResult =
                                                        googleAuthUiClient.signInWithIntent(
                                                            intent = result.data ?: return@launch
                                                        )
                                                    viewModel.onSignInResult(signInResult)
                                                }
                                            }
                                        }
                                    )

                                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                                        if (state.isSignInSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Sign in successful",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            navController.navigate("profile")
                                            viewModel.resetState()
                                        }
                                    }

                                    LoginScreen(
                                        navController = navController,
                                        modifier = Modifier.padding(innerPadding),
                                        typography = Typography,
                                        viewModel = authviewModel,
                                        state = state,
                                        onSignInClick = {
                                            lifecycleScope.launch {
                                                val signInIntentSender = googleAuthUiClient.signIn()
                                                launcher.launch(
                                                    IntentSenderRequest.Builder(
                                                        signInIntentSender ?: return@launch
                                                    ).build()
                                                )
                                            }
                                        }
                                    )

                                }
                                composable("profile") {
                                    ProfileScreen(
                                        userData = googleAuthUiClient.getSignedInUser(),
                                        onSignOut = {
                                            lifecycleScope.launch {
                                                googleAuthUiClient.signOut()
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Signed out",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                navController.popBackStack()
                                            }
                                        }
                                    )
                                }
                                composable("register") {
                                    val viewModel = viewModel<SignInViewModel>()
                                    val state by viewModel.state.collectAsStateWithLifecycle()

                                    LaunchedEffect(key1 = Unit) {
                                        if (googleAuthUiClient.getSignedInUser() != null) {
                                            navController.navigate("profile")
                                        }
                                    }

                                    val launcher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signInResult =
                                                        googleAuthUiClient.signInWithIntent(
                                                            intent = result.data ?: return@launch
                                                        )
                                                    viewModel.onSignInResult(signInResult)
                                                }
                                            }
                                        }
                                    )

                                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                                        if (state.isSignInSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Sign in successful",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            navController.navigate("profile")
                                            viewModel.resetState()
                                        }
                                    }
                                    RegistrationScreen(
                                        navController = navController,
                                        modifier = Modifier.padding(innerPadding),
                                        typography = Typography,
                                        title = "Swar",
                                        viewModel = authviewModel,
                                        state = state,
                                        onSignInClick = {
                                            lifecycleScope.launch {
                                                val signInIntentSender = googleAuthUiClient.signIn()
                                                launcher.launch(
                                                    IntentSenderRequest.Builder(
                                                        signInIntentSender ?: return@launch
                                                    ).build()
                                                )
                                            }
                                        }
                                    )
                                }
                                composable("home") {
                                    HomeScreen(
                                        navController = navController,
                                        modifier = Modifier.padding(innerPadding),
                                        typography = Typography,
                                        viewModel = authviewModel
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SplashScreen() {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your App Name",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }

    }

//    @Composable
//    fun NavHostWithSplashScreen(
//        navController: NavHostController,
//        viewModel: AuthViewModel,
//        splashShown: Boolean,
//        googleAuthUiClient: GoogleAuthUiClient
//    ) {
//
//
//    }

//    fun determineStart(): String {
//        return if ((auth.currentUser != null)) {
//            "home"
//        } else if ((auth.currentUser != null)) {
//            "home"
//        } else "login_screen"
//    }
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