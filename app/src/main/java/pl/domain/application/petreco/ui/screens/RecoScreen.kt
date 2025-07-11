package pl.domain.application.petreco.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.domain.application.petreco.ui.component.AppTopBar
import pl.domain.application.petreco.ui.component.BottomNavBar
import pl.domain.application.petreco.permissions.CameraPermissionRequest
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.domain.application.petreco.auth.AuthRepository
import pl.domain.application.petreco.data.viewmodel.RecoViewModel
import java.net.URLDecoder
import android.util.Base64
import android.view.ViewGroup
import androidx.compose.foundation.background
import pl.domain.application.petreco.ui.animation.startConfettiAnimation
import pl.domain.application.petreco.ui.theme.themedGradientBackground
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoScreen(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    activity: Activity,
    viewModel: RecoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val context = LocalContext.current
    val isFavorite by viewModel.isFavorite
    val userId = AuthRepository.getCurrentUserId() ?: ""
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val photoUriString = backStackEntry?.arguments?.getString("photoUri")?.let {
        URLDecoder.decode(it, "UTF-8")
    }

    LaunchedEffect(viewModel.currentImageUri.value, viewModel.recognizedAnimal.value) {
        if (viewModel.currentImageUri.value.isNotBlank() && viewModel.recognizedAnimal.value.isNotBlank()) {
            viewModel.checkIfFavorite(context, userId)
        } else {
            viewModel.setisFavorite(false)
        }
    }

    Log.d("ZDK", photoUriString.toString())
    LaunchedEffect(photoUriString) {
        if (!photoUriString.isNullOrEmpty()) {
            viewModel.setLoading(true)
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    if (photoUriString.startsWith("http://") || photoUriString.startsWith("https://")) {
                        val inputStream = java.net.URL(photoUriString).openStream()
                        BitmapFactory.decodeStream(inputStream)
                    } else if (photoUriString.startsWith("content://") || photoUriString.startsWith("file://")) {
                        val uri = Uri.parse(photoUriString)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        BitmapFactory.decodeStream(inputStream)
                    } else {
                        null
                    }
                }
                if (bitmap != null) {
                    viewModel.recognizeFromBitmap(context, bitmap, photoUriString, userId)
                } else {
                    viewModel.setrecognizedAnimal("Nie udało się załadować zdjęcia")
                    viewModel.setLoading(false)
                }
            } catch (e: Exception) {
                viewModel.setrecognizedAnimal("Nie udało się załadować zdjęcia")
                viewModel.setLoading(false)
            }
        }
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                val imageUri = bitmapToBase64(bitmap)
                viewModel.recognizeFromBitmap(context, bitmap, imageUri, userId)
            }
        }
    )


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val bitmap = uriToBitmap(uri, context)
                if (bitmap != null) {
                    val imageUri = uri.toString()
                    viewModel.recognizeFromBitmap(context, bitmap, imageUri, userId)
                } else {
                    viewModel.setrecognizedAnimal("Nie udało się wczytać zdjęcia")
                    viewModel.setrecognitionConfidence(null)
                    viewModel.setLoading(false)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            AppTopBar(
                isUserLoggedIn = isUserLoggedIn,
                onLoginClick = {
                    navController.navigate("login")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onLogoutClick = {
                    AuthRepository.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, isUserLoggedIn) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(brush = themedGradientBackground())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                if (viewModel.selectedBitmap.value != null) {
                    Button(
                        onClick = {
                            if (viewModel.isFavorite.value) {
                                viewModel.deleteFavorite(context, userId)
                            } else {
                                viewModel.saveFavorite(context, userId)
                            }
                        }
                    ) {
                        Text(if (viewModel.isFavorite.value) "Usuń z ulubionych" else "Dodaj do ulubionych")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                viewModel.selectedBitmap.value?.let{ bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Wybrane zdjęcie",
                        modifier = Modifier
                            .size(250.dp)
                            .padding(bottom = 16.dp)
                    )
                } ?: run {
                    Text(
                        text = "Brak wybranego zdjęcia",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Wybierz z galerii",
                                maxLines = 2,
                                softWrap = true
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        CameraPermissionRequest(
                            onPermissionGranted = {
                                Button(
                                    onClick = { cameraLauncher.launch(null) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Użyj aparatu",
                                        maxLines = 2,
                                        softWrap = true
                                    )
                                }
                            },
                            onPermissionDenied = {
                                Text("Brak uprawnień do kamery. Włącz je w ustawieniach.")
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                if (viewModel.showLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Text("Rozpoznawanie...", style = MaterialTheme.typography.bodyLarge)
                } else {
                    if(viewModel.recognizedAnimal.value != "null"){
                        Text(
                            text = "Rozpoznano: ${viewModel.recognizedAnimal.value}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    viewModel.recognitionConfidence.value?.let { confidence ->
                        val confidencePercent = (viewModel.recognitionConfidence.value ?: 0f) * 100
                        Text(
                            text = "Pewność: ${"%.2f".format(confidencePercent)}%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    LaunchedEffect(viewModel.recognizedAnimal.value) {
                        if (viewModel.recognizedAnimal.value != "null") {
                            val decorView = activity.window.decorView as ViewGroup
                            startConfettiAnimation(activity, decorView)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }
}

private fun uriToBitmap(uri: Uri, context: android.content.Context): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
