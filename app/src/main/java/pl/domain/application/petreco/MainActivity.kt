package pl.domain.application.petreco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import pl.domain.application.petreco.auth.AuthRepository
import pl.domain.application.petreco.auth.FacebookAuthHandler
import pl.domain.application.petreco.ui.screens.HomeScreen
import pl.domain.application.petreco.ui.screens.LoginScreen
import pl.domain.application.petreco.ui.screens.ProfileScreen
import pl.domain.application.petreco.ui.screens.RecoScreen
import pl.domain.application.petreco.ui.theme.PetRecoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        FirebaseApp.initializeApp(this)

        FacebookAuthHandler.init(this)

        setContent {
            PetRecoTheme {
                val navController = rememberNavController()
                val isUserLoggedIn = remember { mutableStateOf(false) }
                val isLoading = remember { mutableStateOf(false) }
                val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                FacebookAuthHandler.onLoginResult = { success, errorMsg ->
                    isLoading.value = false
                    if (success) {
                        isUserLoggedIn.value = true
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMsg ?: "Nieznany błąd logowania przez Facebooka"
                            )
                        }
                    }
                }

                FacebookAuthHandler.onLoadingChanged = { loading ->
                    isLoading.value = loading
                }
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            onLoginSuccess = {
                                isUserLoggedIn.value = true
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onSkipLogin = {
                                isUserLoggedIn.value = false
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onFacebookLogin = {
                                isUserLoggedIn.value = true
                                FacebookAuthHandler.loginWithFacebook(this@MainActivity)
                            },
                            isUserLoggedIn = isUserLoggedIn.value
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            isUserLoggedIn = isUserLoggedIn.value,
                            activity = this@MainActivity
                        )
                    }

                    composable(
                        route = "reco?photoUri={photoUri}",
                        arguments = listOf(
                            navArgument("photoUri") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) {
                        RecoScreen(
                            navController = navController,
                            isUserLoggedIn = isUserLoggedIn.value,
                            activity = this@MainActivity
                        )
                    }

                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            isUserLoggedIn = isUserLoggedIn.value,
                            activity = this@MainActivity
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FacebookAuthHandler.callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
