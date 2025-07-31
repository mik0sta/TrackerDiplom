package com.mik0sta.trackerdiplom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mik0sta.trackerdiplom.data.Category
import com.mik0sta.trackerdiplom.data.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<Category>> =
        repository.categories.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            val currentCategories = repository.categories.first()
            if (currentCategories.isEmpty()) {
                val defaultCategories = listOf(
                    Category(name = "Продукты"),
                    Category(name = "Еда вне дома"),
                    Category(name = "Такси"),
                    Category(name = "Транспорт"),
                    Category(name = "Развлечения"),
                    Category(name = "Дом"),
                    Category(name = "Подарки"),
                    Category(name = "Другое"),
                )
                defaultCategories.forEach {
                    repository.insertCategory(it)
                }
            }
        }
    }

    fun addCategory(categoryName: String) {
        viewModelScope.launch {
            repository.insertCategory(Category(name = categoryName))
        }
    }

    fun removeCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}
