package com.mik0sta.trackerdiplom.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mik0sta.trackerdiplom.auth.AuthManager

@Composable
fun SetupPinScreen(
    authManager: AuthManager,
    navController: NavController
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 - ввод PIN, 2 - подтверждение
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (step == 1) "Установите PIN-код" else "Подтвердите PIN-код",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = if (step == 1) pin else confirmPin,
            onValueChange = {
                if (step == 1) pin = it.take(4) else confirmPin = it.take(4)
                error = ""
            },
            label = { Text("4-значный PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when (step) {
                    1 -> {
                        if (pin.length == 4) {
                            step = 2
                        } else {
                            error = "PIN должен содержать 4 цифры"
                        }
                    }
                    2 -> {
                        if (pin == confirmPin) {
                            authManager.savePin(pin)
                            navController.popBackStack()
                        } else {
                            error = "PIN-коды не совпадают"
                            pin = ""
                            confirmPin = ""
                            step = 1
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (step == 1) "Продолжить" else "Подтвердить")
        }
    }
}