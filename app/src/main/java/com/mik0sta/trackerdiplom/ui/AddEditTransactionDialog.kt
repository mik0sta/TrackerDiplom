package com.mik0sta.trackerdiplom.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mik0sta.trackerdiplom.data.Category
import com.mik0sta.trackerdiplom.model.Transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import android.app.DatePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionDialog(
    initial: Transaction? = null,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: (() -> Unit)? = null,
    categories: List<Category>
) {
    var title by remember { mutableStateOf(TextFieldValue(initial?.title ?: "")) }
    var amount by remember { mutableStateOf(TextFieldValue(initial?.amount?.let { kotlin.math.abs(it).toString() } ?: "")) }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var isIncome by remember { mutableStateOf(initial?.amount?.let { it >= 0 } ?: false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var selectedDate by remember { mutableStateOf(initial?.date?.let { LocalDate.parse(it) } ?: LocalDate.now()) }

    // Для DatePickerDialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.text.toDoubleOrNull() ?: return@TextButton
                val finalAmount = if (isIncome) amt else -amt
                onSave(
                    Transaction(
                        id = initial?.id ?: 0,
                        title = title.text,
                        amount = finalAmount,
                        category = category,
                        date = selectedDate.toString()
                    )
                )
                onDismiss()
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Row {
                if (initial != null && onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        }
                    ) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) { Text("Отмена") }
            }
        },
        title = { Text(if (initial == null) "Добавить транзакцию" else "Редактировать") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    )
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Сумма") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isIncome) "Доход" else "Расход",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isIncome,
                        onCheckedChange = { isIncome = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF4CAF50),
                            checkedTrackColor = Color(0xFFA5D6A7),
                            uncheckedThumbColor = Color(0xFFF44336),
                            uncheckedTrackColor = Color(0xFFEF9A9A)
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Кнопка выбора даты
                Text("Дата:", style = MaterialTheme.typography.labelMedium)
                TextButton(onClick = { datePickerDialog.show() }) {
                    Text(selectedDate.format(formatter))
                }

                Text(
                    "Категория:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                FlowRow(
                    maxItemsInEachRow = 3,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            onClick = { category = cat.name },
                            label = { Text(cat.name) },
                            selected = category == cat.name
                        )
                    }
                }
            }
        }
    )
}
