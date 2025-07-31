package com.mik0sta.trackerdiplom.data.local

import com.mik0sta.trackerdiplom.data.local.CategoryDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mik0sta.trackerdiplom.data.Category
import com.mik0sta.trackerdiplom.data.local.TransactionEntity

@Database(entities = [TransactionEntity::class, Category::class],
    version = 2,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
}
