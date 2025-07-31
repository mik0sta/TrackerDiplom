package com.mik0sta.trackerdiplom.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.mik0sta.trackerdiplom.auth.AuthManager
import com.mik0sta.trackerdiplom.data.CategoryRepository
import com.mik0sta.trackerdiplom.data.TransactionRepository
import com.mik0sta.trackerdiplom.data.local.AppDatabase
import com.mik0sta.trackerdiplom.data.local.CategoryDao
import com.mik0sta.trackerdiplom.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "transactions.db"
        )
            .build()

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideRepository(dao: TransactionDao): TransactionRepository =
        TransactionRepository(dao)

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepository(categoryDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthManager(@ApplicationContext context: Context): AuthManager {
        return AuthManager(context)
    }
}
