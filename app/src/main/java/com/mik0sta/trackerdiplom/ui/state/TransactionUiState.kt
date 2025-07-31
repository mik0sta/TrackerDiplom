package com.mik0sta.trackerdiplom.ui

import com.mik0sta.trackerdiplom.model.Transaction
import java.time.Month

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val currentMonth: Month = Month.JANUARY,
    val filter: Boolean? = null,
    val searchQuery: String = "",
    val searchVisible: Boolean = false,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0
)
