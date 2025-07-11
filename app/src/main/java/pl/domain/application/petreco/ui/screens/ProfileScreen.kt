package pl.domain.application.petreco.ui.screens

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pl.domain.application.petreco.ui.component.AppTopBar
import pl.domain.application.petreco.ui.component.BottomNavBar
import pl.domain.application.petreco.auth.AuthRepository
import pl.domain.application.petreco.data.viewmodel.ProfileViewModel
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.net.URL
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    activity: Activity
) {
    val user = remember { AuthRepository.getCurrentUser() }
    val userId = user?.uid ?: "unknown_user_id"
    val email = user?.email ?: "Nieznany użytkownik"

    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application, userId))
    val recognitions by viewModel.recognitions.collectAsState()


    Scaffold(
        topBar = {
            AppTopBar(
                isUserLoggedIn = AuthRepository.isUserLoggedIn(),
                onLoginClick = { navController.navigate("login") },
                onProfileClick = { /* jesteś na profilu */ },
                onLogoutClick = {
                    AuthRepository.logout()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, isLoggedIn = true) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item{
                    Text(
                        text = "Twój profil",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "Email: $email")
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Twoje rozpoznania:", style = MaterialTheme.typography.titleMedium)
                }


                recognitions.forEach { recognition ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (recognition.imageUri.startsWith("http")) {
                                    AsyncImage(
                                        model = recognition.imageUri,
                                        contentDescription = recognition.recognizedAnimal,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                } else {
                                    val bitmap = decodeImageUri(recognition.imageUri)
                                    bitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = recognition.recognizedAnimal,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Rozpoznanie: ${recognition.recognizedAnimal}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color =MaterialTheme.colorScheme.onPrimary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Pewność: ${"%.2f".format(recognition.confidence ?: 0f)}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color =MaterialTheme.colorScheme.onPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                IconButton(
                                    onClick = {
                                        viewModel.deleteFavourite(context, recognition)
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Usuń rozpoznanie",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                item{
                Spacer(modifier = Modifier.height(48.dp))
            }

            }
        }
    }
}

@Composable
fun decodeImageUri(uriString: String): Bitmap? {
    val context = LocalContext.current

    return remember(uriString) {
        try {
            when {
                uriString.startsWith("data:image") || uriString.length > 100 -> {
                    val pureBase64 = uriString.substringAfter(",")
                    val decoded = Base64.decode(pureBase64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                }
                uriString.startsWith("content://") || uriString.startsWith("file://") -> {
                    val uri = Uri.parse(uriString)
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
                uriString.startsWith("http") -> {
                    val stream = URL(uriString).openStream()
                    BitmapFactory.decodeStream(stream)
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

