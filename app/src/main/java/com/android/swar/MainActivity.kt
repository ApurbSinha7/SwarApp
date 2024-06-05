package com.android.swar

//import com.android.swar.ui.theme.ComposeGoogleSignInCleanArchitectureTheme

import android.Manifest.permission
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.swar.presentation.profile.ClickProfile
import com.android.swar.presentation.profile.ProfileScreen
import com.android.swar.presentation.sign_in.GoogleAuthUiClient
import com.android.swar.presentation.sign_in.SignInViewModel
import com.android.swar.ui.theme.ComposeGoogleSignInCleanArchitectureTheme
import com.android.swar.ui.theme.SwarTheme
import com.android.swar.ui.theme.Typography
import com.android.swar.utils.navigateToHome
import com.android.swar.viewModel.ProfileViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch


//private val auth: FirebaseAuth = FirebaseAuth.getInstance()


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[permission.RECORD_AUDIO] == true &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || permissions[permission.READ_MEDIA_AUDIO] == true)) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Explain to the user that the feature is unavailable
                Toast.makeText(this, "Permission denied. You won't be able to record", Toast.LENGTH_SHORT).show()
            }
        }

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    //val application = applicationContext as MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
//        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            window.statusBarColor = Color(0xFFF6E517).toArgb()

            // Customize status bar text and icon color
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true // Ensures dark icons/text
//            windowInsetsController.setSystemBarsAppearance(
//                0,
//                WindowInsetsControllerCompat.APPEARANCE_LIGHT_STATUS_BARS
//            )
            CompositionLocalProvider(
                androidx.lifecycle.compose.LocalLifecycleOwner provides this@MainActivity,
            ) {
                SwarTheme {

                    val navController = rememberNavController()
                    val authViewModel = AuthViewModel(navController = navController)
                    AuthManager(navController = navController)
                    val profileViewModel : ProfileViewModel = viewModel()
                    var strtdes = ""
                    strtdes = if(authViewModel.isLoggedIn()) "home" else "login_screen"

                    ComposeGoogleSignInCleanArchitectureTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            NavHost(
                                navController = navController, startDestination = strtdes
                            ) {
//
                                composable("login_screen") {
                                    val viewModel = viewModel<SignInViewModel>()
                                    val state by viewModel.state.collectAsStateWithLifecycle()

                                    LaunchedEffect(key1 = Unit) {
                                        if (googleAuthUiClient.getSignedInUser() != null) {
                                            navController.navigate("home")
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
                                            val signedInUser = googleAuthUiClient.getSignedInUser()
                                            signedInUser?.let {
                                                profileViewModel.saveUserToDatabase(it){
                                                    navigateToHome(navController = navController)
                                                }
                                            }

                                            navController.navigate("home")
                                            viewModel.resetState()
                                        }
                                    }

                                    LoginScreen(
                                        navController = navController,
                                        modifier = Modifier.padding(innerPadding),
                                        typography = Typography,
                                        viewModel = authViewModel,
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
                                            navController.navigate("home")
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
                                            val signedInUser = googleAuthUiClient.getSignedInUser()
                                            signedInUser?.let {
                                                profileViewModel.saveUserToDatabase(it){
                                                    navigateToHome(navController = navController)
                                                }
                                            }

                                            navController.navigate("home")
                                            viewModel.resetState()
                                        }
                                    }
                                    RegistrationScreen(
                                        navController = navController,
                                        modifier = Modifier.padding(innerPadding),
                                        typography = Typography,
                                        title = "Swar",
                                        viewModel = authViewModel,
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

                                        )
                                }
                                composable("right_screen") {
                                    RightScreen(navController = navController)
                                }
                                composable(
                                    route = "profile_screen/{userId}",
                                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                                ) { backStackEntry ->
                                    val userId = backStackEntry.arguments?.getString("userId")
                                    userId?.let { ClickProfile(viewModel = viewModel(), userId = it, navController =  navController) }
                                }
                            }
                        }
                    }
                }
            }
        }
        requestPermissions()
    }
    private fun requestPermissions() {
        val permissions = mutableListOf(permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(permission.READ_MEDIA_AUDIO)
        } else {
            permissions.add(permission.READ_EXTERNAL_STORAGE)
        }

        requestPermissionLauncher.launch(permissions.toTypedArray())
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