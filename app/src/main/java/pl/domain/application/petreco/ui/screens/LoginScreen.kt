package pl.domain.application.petreco.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.material3.Icon
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import pl.domain.application.petreco.auth.AuthRepository
import pl.domain.application.petreco.R
import android.content.pm.ActivityInfo
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import pl.domain.application.petreco.data.utils.lockOrientation
import pl.domain.application.petreco.data.utils.unlockOrientation
import pl.domain.application.petreco.ui.theme.themedGradientBackground
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*

fun Char.isEmoji(): Boolean {
    val code = this.code
    return (code in 0x1F600..0x1F64F) ||  // Emoticons
            (code in 0x1F300..0x1F5FF) ||  // Misc Symbols and Pictographs
            (code in 0x1F680..0x1F6FF) ||  // Transport and Map
            (code in 0x2600..0x26FF)   ||  // Misc symbols
            (code in 0x2700..0x27BF)   ||  // Dingbats
            (code in 0xFE00..0xFE0F)   ||  // Variation Selectors
            (code in 0x1F900..0x1F9FF) ||  // Supplemental Symbols and Pictographs
            (code in 0x1F1E6..0x1F1FF)     // Flags
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    onSkipLogin: () -> Unit,
    onFacebookLogin: () -> Unit,
    isUserLoggedIn: Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = context as Activity

    fun startLoadingWithTimeout() {
        isLoading = true
        scope.launch {
            delay(60000L)
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        activity.lockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        onDispose {
            activity.unlockOrientation()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = themedGradientBackground()
                )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item{
                Spacer(modifier = Modifier.height(50.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo aplikacji",
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            clip = false
                        )
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "PetReco",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))
            }


            item{
                Icon(
                    imageVector = Icons.Default.Facebook,
                    contentDescription = "Facebook login",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .size(42.dp)
                        .clickable {
                            isLoading = true
                            if (!isNetworkAvailable(context)) {
                                isLoading = false
                                loginError = "Brak połączenia z internetem"
                            } else {
                                startLoadingWithTimeout()
                                onFacebookLogin()
                            }
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("lub", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item{
                OutlinedTextField(
                    value = email,
                    onValueChange = { input -> email = input.filterNot { it.isEmoji() } },
                    label = { Text("Email", color = MaterialTheme.colorScheme.onBackground )},
                    modifier = Modifier.fillMaxWidth(0.85f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedIndicatorColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.background,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                    )

                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            item{
                OutlinedTextField(
                    value = password,
                    onValueChange = { input -> password = input.filterNot { it.isEmoji() } },
                    label = { Text("Hasło", color = MaterialTheme.colorScheme.onBackground ) },
                    modifier = Modifier.fillMaxWidth(0.85f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedIndicatorColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.background,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            item{
                if (isRegistering) {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { input -> confirmPassword = input.filterNot { it.isEmoji() } },
                        label = { Text("Potwierdź hasło", color = MaterialTheme.colorScheme.onBackground ) },
                        modifier = Modifier.fillMaxWidth(0.85f),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground,
                            focusedIndicatorColor = MaterialTheme.colorScheme.background,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.background,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                        ),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            item{
                if (loginError != null) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { loginError = null },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        confirmButton = {
                            TextButton(onClick = { loginError = null }) {
                                Text("OK",
                                    color = MaterialTheme.colorScheme.onBackground)
                            }
                        },
                        title = {
                            Text("Błąd",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground)
                        },
                        text = {
                            Text(loginError ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

           item{
               Button(
                   onClick = {
                       isLoading = true
                       loginError = null
                       if (email.isNotBlank() && password.isNotBlank()) {
                           if (isRegistering && password != confirmPassword) {
                               loginError = "Hasła się nie zgadzają"
                               isLoading = false
                               return@Button
                           }
                               if (!isNetworkAvailable(context)) {
                                   isLoading = false
                                   loginError = "Brak połączenia z internetem"
                                   return@Button
                               } else {
                                   val action =
                                       if (isRegistering) AuthRepository::registerUser else AuthRepository::loginUser
                                   action(email, password) { success, errorMsg ->
                                       isLoading = false
                                       if (success) {
                                           onLoginSuccess()
                                       } else {
                                           loginError = errorMsg
                                               ?: "Nie udało się ${if (isRegistering) "zarejestrować" else "zalogować"}"
                                       }
                                   }
                               }
                       } else {
                           loginError = "Proszę wprowadzić email i hasło"
                           isLoading = false
                       }
                   },
                   enabled = !isLoading,
                   modifier = Modifier.fillMaxWidth(0.85f)
               ) {
                   Text(text = if (isRegistering) "Zarejestruj się" else "Zaloguj się",
                       color = MaterialTheme.colorScheme.onSurface
                   )
               }

               Spacer(modifier = Modifier.height(8.dp))
           }

            item{
                Button(
                    onClick = {
                        isRegistering = !isRegistering
                        loginError = null
                    },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Text(
                        text = if (isRegistering) "Masz już konto? Zaloguj się" else "Nie masz konta? Zarejestruj się",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {

            TextButton(
                onClick  = {
                    onSkipLogin()
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .let {
                        if (isLoading) it.clickable(enabled = false) {} else it
                    }
            ) {
                Text("Kontynuuj bez logowania", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

