package com.mik0sta.trackerdiplom.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.*
import com.mik0sta.trackerdiplom.ui.settings.SettingsScreen

@Composable
fun AppScreen(
    settingsViewModel: SettingsViewModel,
    onLogout: () -> Unit = {}
) {

    val navController = rememberNavController()
    val items = listOf(
        Screen.Transactions,
        Screen.Stats,
        Screen.Settings,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.name,
                        onClick = {
                            navController.navigate(screen.name) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Transactions -> Icons.Default.List
                                    Screen.Stats -> Icons.Default.Info
                                    Screen.Settings -> Icons.Default.Settings
                                },
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Transactions.name,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Transactions.name) { TransactionScreen() }
            composable(Screen.Stats.name) { StatsScreen() }
            composable(Screen.Settings.name) { SettingsScreen(settingsViewModel, onLogout = onLogout)}
        }
    }
}