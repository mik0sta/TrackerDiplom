package com.mik0sta.trackerdiplom.data.local

import com.mik0sta.trackerdiplom.data.local.TransactionEntity
import com.mik0sta.trackerdiplom.model.Transaction

fun TransactionEntity.toModel() = Transaction(
    id = id,
    title = title,
    amount = amount,
    category = category,
    date = date
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    title = title,
    amount = amount,
    category = category,
    date = date
)
