package com.mik0sta.trackerdiplom.ui.settings


import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.items
import androidx.core.content.FileProvider
import com.mik0sta.trackerdiplom.model.Transaction
import com.mik0sta.trackerdiplom.ui.SettingsViewModel
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLogout: () -> Unit = {}
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val categories by viewModel.categories.collectAsState(emptyList())

    var newCategory by remember { mutableStateOf("") }




    Column(Modifier.padding(16.dp)) {
        Text("Настройки", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Тёмная тема")
            Switch(checked = isDarkTheme, onCheckedChange = { viewModel.toggleTheme() })
        }

        Spacer(Modifier.height(24.dp))

        Text("Категории", style = MaterialTheme.typography.titleMedium)

        LazyColumn(Modifier.weight(1f)) {
            items(categories) { category ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(category.name)
                    IconButton(onClick = { viewModel.removeCategory(category) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить категорию")
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCategory,
                onValueChange = { newCategory = it },
                label = { Text("Новая категория") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newCategory.isNotBlank()) {
                            viewModel.addCategory(newCategory.trim())
                            newCategory = ""
                        }
                    }
                ),


                )
            Button(
                onClick = {
                    if (newCategory.isNotBlank()) {
                        viewModel.addCategory(newCategory.trim())
                        newCategory = ""
                    }
                },
                enabled = newCategory.isNotBlank()
            ) {
                Text("+")
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text("Выйти")
        }

        val context = LocalContext.current
        val transactionList by remember { mutableStateOf(listOf<Transaction>()) } // или получай из ViewModel

        Button(
            onClick = {
                exportAndShareCsv(context, transactionList)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Экспортировать данные в CSV")
        }
    }
}


fun exportAndShareCsv(context: Context, transactions: List<Transaction>) {
    val fileName = "transactions_export.csv"
    val file = File(context.cacheDir, fileName)

    try {
        val writer = FileWriter(file)
        writer.append("ID,Name,Amount,Date,Category,Type\n")
        for (t in transactions) {
            writer.append("${t.id},${t.title},${t.amount},${Date(t.date)},${t.category},${if (t.amount > 0) "Income" else "Expense"}\n")
        }
        writer.flush()
        writer.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Поделиться CSV-файлом"))

    } catch (e: IOException) {
        Toast.makeText(context, "Ошибка при экспорте: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

