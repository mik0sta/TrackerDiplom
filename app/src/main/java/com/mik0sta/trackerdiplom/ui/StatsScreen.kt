package com.mik0sta.trackerdiplom.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.mik0sta.trackerdiplom.model.Transaction
import com.github.tehras.charts.piechart.PieChartData.Slice
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@Composable
fun StatsScreen(viewModel: TransactionViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val filteredTransactions = uiState.transactions.filter {
        YearMonth.from(LocalDate.parse(it.date)) == currentYearMonth
    }

    val incomeTransactions = filteredTransactions.filter { it.amount > 0 }
    val expenseTransactions = filteredTransactions.filter { it.amount < 0 }

    val incomeTotal = incomeTransactions.sumOf { it.amount }
    val expenseTotal = expenseTransactions.sumOf { it.amount }

    val incomeSlices = getLabeledSlices(incomeTransactions)
    val expenseSlices = getLabeledSlices(expenseTransactions)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Заголовок с навигацией по месяцам
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Предыдущий месяц")
            }

            Text(
                text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Следующий месяц")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Общая сводка
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryCard(
                title = "Доходы",
                amount = incomeTotal,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            SummaryCard(
                title = "Расходы",
                amount = expenseTotal,
                color = Color(0xFFF44336),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            SummaryCard(
                title = "Баланс",
                amount = incomeTotal + expenseTotal,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        // Секция доходов
        CategorySection(
            title = "Доходы по категориям",
            total = incomeTotal,
            slices = incomeSlices,
            positive = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Секция расходов
        CategorySection(
            title = "Расходы по категориям",
            total = expenseTotal,
            slices = expenseSlices,
            positive = false
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${if (amount >= 0) "+" else ""}${"%,.2f".format(amount)} ₽",
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}

@Composable
private fun CategorySection(
    title: String,
    total: Double,
    slices: List<LabeledSlice>,
    positive: Boolean
) {


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,

                )

                Text(
                    text = String.format("%,.2f ₽", total),
                    style = MaterialTheme.typography.bodyLarge,

                )
            }

            if (slices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                PieChart(
                    pieChartData = PieChartData(slices = slices.map { Slice(it.value, it.color) }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                PieChartLegend(slices)
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Нет данных",
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PieChartLegend(slices: List<LabeledSlice>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        slices.sortedByDescending { it.amount }.forEach { slice ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(slice.color, shape = MaterialTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = slice.label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "%,.2f ₽ (%.0f%%)".format(slice.amount, slice.value * 100),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            LinearProgressIndicator(
                progress = slice.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = slice.color,
                trackColor = slice.color.copy(alpha = 0.2f)
            )
        }
    }
}

fun getLabeledSlices(transactions: List<Transaction>): List<LabeledSlice> {
    val total = transactions.sumOf { kotlin.math.abs(it.amount) }
    if (total == 0.0) return emptyList()

    val grouped = transactions.groupBy { it.category }.mapValues { entry ->
        entry.value.sumOf { kotlin.math.abs(it.amount) }
    }

    val colors = listOf(
        Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6), Color(0xFFFFB74D),
        Color(0xFFBA68C8), Color(0xFFFF8A65), Color(0xFFA1887F), Color(0xFF4DB6AC),
        Color(0xFFDCE775), Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF4FC3F7)
    )

    return grouped.entries.mapIndexed { index, entry ->
        LabeledSlice(
            label = entry.key,
            amount = entry.value,
            value = (entry.value / total).toFloat(),
            color = colors[index % colors.size]
        )
    }
}


data class LabeledSlice(
    val label: String,
    val value: Float,  // для графика — доля в общем объёме (0..1)
    val amount: Double, // сумма в рублях (например, 1234.56)
    val color: Color
)
