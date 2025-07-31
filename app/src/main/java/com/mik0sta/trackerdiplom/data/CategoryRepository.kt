package com.mik0sta.trackerdiplom.data

import com.mik0sta.trackerdiplom.data.local.CategoryDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    val categories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }
}
