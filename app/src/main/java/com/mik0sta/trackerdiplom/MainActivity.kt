package com.mik0sta.trackerdiplom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mik0sta.trackerdiplom.auth.AuthManager
import com.mik0sta.trackerdiplom.ui.AppScreen
import com.mik0sta.trackerdiplom.ui.SettingsViewModel
import com.mik0sta.trackerdiplom.ui.auth.PinAuthScreen
import com.mik0sta.trackerdiplom.ui.auth.SetupPinScreen
import com.mik0sta.trackerdiplom.ui.theme.TrackerDiplomTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            TrackerDiplomTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = if (authManager.isPinSet()) "auth" else "setup_pin"
                ) {
                    composable("setup_pin") {
                        SetupPinScreen(
                            authManager = authManager,
                            navController = navController
                        )
                    }

                    composable("auth") {
                        PinAuthScreen(
                            authManager = authManager,
                            navController = navController,
                            onAuthSuccess = { navController.navigate("main") },
                            activity = this@MainActivity
                        )
                    }

                    composable("main") {
                        AppScreen(settingsViewModel)
                    }
                }
            }
        }
    }
}