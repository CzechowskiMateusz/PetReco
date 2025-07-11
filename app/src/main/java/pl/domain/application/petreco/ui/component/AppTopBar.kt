package pl.domain.application.petreco.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CenterAlignedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isUserLoggedIn: Boolean,
    onLoginClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var menuExpanded = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                "PetReco",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profil użytkownika",
                modifier = Modifier
                    .padding(8.dp)
                    .size(29.dp)
                    .clickable { menuExpanded.value = true }
            )
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
            ) {
                if (isUserLoggedIn) {
                    DropdownMenuItem(
                        text = { Text("Mój profil", color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded.value = false
                            onProfileClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Wyloguj się", color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded.value = false
                            onLogoutClick()
                        }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text("Zaloguj się", color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded.value = false
                            onLoginClick()
                        }
                    )
                }
            }
        }
    )
}
