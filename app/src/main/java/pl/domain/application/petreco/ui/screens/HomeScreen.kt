package pl.domain.application.petreco.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import pl.domain.application.petreco.data.api.RetrofitApi
import pl.domain.application.petreco.data.model.Animal
import pl.domain.application.petreco.data.utils.TokenProvider
import pl.domain.application.petreco.data.utils.fetchAnimals
import pl.domain.application.petreco.ui.component.AppTopBar
import pl.domain.application.petreco.ui.component.BottomNavBar
import pl.domain.application.petreco.ui.component.ZooAnimalCard
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import pl.domain.application.petreco.auth.AuthRepository
import pl.domain.application.petreco.ui.theme.themedGradientBackground
import pl.domain.application.petreco.ui.viewmodel.HomeViewModel
import java.net.URLEncoder
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    activity: Activity,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Log.d("TAG", "isUserLoggedIn: $isUserLoggedIn")

    val allAnimal by viewModel.animals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (!isNetworkAvailable(activity)) {
            viewModel.setError("Brak połączenia z internetem")
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { lastVisible ->
                if (lastVisible >= allAnimal.lastIndex) {
                    viewModel.loadMore()
                }
            }
    }

    Scaffold(
        topBar  = {
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
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = themedGradientBackground()
                    )
                    .padding(paddingValues)
            ) {
                when {
                    isLoading && allAnimal.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onBackground)
                    }

                    else -> LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        state = listState
                    ) {
                        items(allAnimal) { animal ->
                            ZooAnimalCard(
                                animal = animal,
                                modifier = Modifier.clickable {
                                    if (isUserLoggedIn) {
                                        val photoUrl = animal.photos.firstOrNull()?.medium
                                        if (photoUrl != null) {
                                            val encodedPhotoUrl = URLEncoder.encode(photoUrl, "UTF-8")
                                            navController.navigate("reco?photoUri=$encodedPhotoUrl")
                                        }
                                    } else {
                                        Toast.makeText(activity, "Zaloguj się, aby rozpoznać zwierzę", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.setError(null) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.setError(null) }) {
                            Text("OK", color = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    title = {
                        Text("Błąd", color = MaterialTheme.colorScheme.onBackground)
                    },
                    text = {
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.onBackground)
                    },
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.background,
                    textContentColor = MaterialTheme.colorScheme.background
                )
            }
        }
    )
}
