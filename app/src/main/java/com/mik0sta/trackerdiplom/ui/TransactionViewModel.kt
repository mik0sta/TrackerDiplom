package com.mik0sta.trackerdiplom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mik0sta.trackerdiplom.data.TransactionRepository
import com.mik0sta.trackerdiplom.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = repository.getAll().stateIn(viewModelScope,
        SharingStarted.Eagerly, emptyList())
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _filter = MutableStateFlow<Boolean?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _searchVisible = MutableStateFlow(false)

    val uiState = combine(_transactions, _currentMonth, _filter, _searchQuery, _searchVisible)
    { all, month, filter, search, searchVisible ->


        val filteredByMonth = all.filter {
            val date = LocalDate.parse(it.date)
            YearMonth.from(date) == month
        }

        val filteredByType = when (filter) {
            true -> filteredByMonth.filter { it.amount > 0 }
            false -> filteredByMonth.filter { it.amount < 0 }
            null -> filteredByMonth
        }

        val filteredBySearch = if (search.isBlank()) filteredByType else filteredByType.filter {
            it.title.contains(search, ignoreCase = true) ||
                    it.category.contains(search, ignoreCase = true)
        }

        val income = filteredByMonth.filter { it.amount > 0 }.sumOf { it.amount }
        val expense = filteredByMonth.filter { it.amount < 0 }.sumOf { it.amount }

        TransactionUiState(
            transactions = all,
            currentMonth = month.month,
            filteredTransactions = filteredBySearch,
            filter = filter,
            searchQuery = search,
            searchVisible = searchVisible,
            totalIncome = income,
            totalExpense = expense
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, TransactionUiState())

    fun add(tx: Transaction) = viewModelScope.launch {
        repository.insert(tx)
    }

    fun update(tx: Transaction) = viewModelScope.launch {
        repository.update(tx)
    }

    fun delete(tx: Transaction) = viewModelScope.launch {
        repository.delete(tx)
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun setFilter(f: Boolean?) {
        _filter.value = f
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun toggleSearchVisibility() {
        _searchVisible.value = !_searchVisible.value
    }
}