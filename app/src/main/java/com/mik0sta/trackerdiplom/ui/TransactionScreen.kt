package com.mik0sta.trackerdiplom.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mik0sta.trackerdiplom.model.Transaction
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun TransactionScreen(viewModel: TransactionViewModel = hiltViewModel(),
                      settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var dialogVisible by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    val categories by settingsViewModel.categories.collectAsState()

    val monthName = uiState.currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val capitalizedMonth = monthName.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

    Scaffold(
        topBar = {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "previous")
                    }
                    Text(
                        text = capitalizedMonth,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "next")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = uiState.filter == null,
                        onClick = { viewModel.setFilter(null) },
                        label = { Text("Все") }
                    )
                    FilterChip(
                        selected = uiState.filter == true,
                        onClick = { viewModel.setFilter(true) },
                        label = { Text("Доходы") }
                    )
                    FilterChip(
                        selected = uiState.filter == false,
                        onClick = { viewModel.setFilter(false) },
                        label = { Text("Расходы") }
                    )
                    IconButton(onClick = { viewModel.toggleSearchVisibility() }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                }

                if (uiState.searchVisible) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Поиск по названию или категории") }
                    )
                }
            }
        },
        floatingActionButton = { // FAB
            FloatingActionButton(onClick = {
                editingTransaction = null
                dialogVisible = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {

            // Общая сводка
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryCard(
                    title = "Доходы",
                    amount = uiState.totalIncome,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                SummaryCard(
                    title = "Расходы",
                    amount = uiState.totalExpense,
                    color = Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                SummaryCard(
                    title = "Баланс",
                    amount = uiState.totalIncome + uiState.totalExpense,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(uiState.filteredTransactions, key = { it.id }) { tx ->
                    TransactionItem(
                        tx = tx,
                        onClick = {
                            editingTransaction = tx
                            dialogVisible = true
                        },
                        onSwipeToDelete = { viewModel.delete(tx) }
                    )
                }
            }
        }

        if (dialogVisible) {
            AddEditTransactionDialog(
                initial = editingTransaction,
                onDismiss = { dialogVisible = false },
                onSave = {
                    if (editingTransaction == null) {
                        viewModel.add(it)
                    } else {
                        viewModel.update(it)
                    }
                    dialogVisible = false
                },
                onDelete = {
                    editingTransaction?.let { viewModel.delete(it) }
                },
                categories = categories
            )
        }
    }
}

@Composable
fun TransactionItem(
    tx: Transaction,
    onClick: () -> Unit,
    onSwipeToDelete: () -> Unit
) {
    val arrowIcon = if (tx.amount > 0) {
        Icons.Default.KeyboardArrowUp
    } else {
        Icons.Default.KeyboardArrowDown
    }
    val arrowColor = if (tx.amount > 0) {
        Color(0xFF4CAF50) // Зеленый
    } else {
        Color(0xFFF44336) // Красный
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .pointerInput(Unit) {  // свайп для удаления
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) {
                        onSwipeToDelete()
                    }
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${tx.amount} ₽", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(tx.category, style = MaterialTheme.typography.bodySmall)
                Text(tx.date, style = MaterialTheme.typography.bodySmall)
            }
            Icon(
                imageVector = arrowIcon,
                contentDescription = if (tx.amount > 0) "Доход" else "Расход",
                tint = arrowColor,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}