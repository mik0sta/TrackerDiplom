package com.mik0sta.trackerdiplom.data

import com.mik0sta.trackerdiplom.data.local.TransactionDao
import com.mik0sta.trackerdiplom.data.local.toEntity
import com.mik0sta.trackerdiplom.data.local.toModel
import com.mik0sta.trackerdiplom.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dao: TransactionDao
) {
    fun getAll(): Flow<List<Transaction>> = dao.getAll().map { list ->
        list.map { it.toModel() }
    }

    suspend fun insert(transaction: Transaction) {
        dao.insert(transaction.toEntity())
    }

    suspend fun update(transaction: Transaction) {
        dao.update(transaction.toEntity())
    }

    suspend fun delete(transaction: Transaction) {
        dao.delete(transaction.toEntity())
    }
}
