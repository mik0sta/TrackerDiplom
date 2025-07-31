package com.mik0sta.trackerdiplom.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.mik0sta.trackerdiplom.auth.AuthManager

@Composable
fun PinAuthScreen(
    authManager: AuthManager,
    navController: NavController,
    onAuthSuccess: () -> Unit,
    activity: FragmentActivity
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var showBiometricPrompt by remember { mutableStateOf(false) }

    // Показываем биометрическую аутентификацию при первом открытии экрана
    LaunchedEffect(Unit) {
        if (authManager.isBiometricAvailable()) {
            showBiometricPrompt = true
        }
    }

    if (showBiometricPrompt) {
        authManager.initBiometricAuth(
            activity = activity,
            onSuccess = onAuthSuccess,
            onError = { errorMsg ->
                showBiometricPrompt = false
                error = errorMsg
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (authManager.isBiometricAvailable()) {
            IconButton(
                onClick = { showBiometricPrompt = true },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Вход по отпечатку",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text("Или введите PIN-код", modifier = Modifier.padding(8.dp))
        }

        // ... остальной код экрана ввода PIN ...
    }
}