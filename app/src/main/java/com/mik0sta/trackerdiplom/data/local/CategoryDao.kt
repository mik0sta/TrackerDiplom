package com.mik0sta.trackerdiplom.data.local

import androidx.room.*
import com.mik0sta.trackerdiplom.data.Category

import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)
}
