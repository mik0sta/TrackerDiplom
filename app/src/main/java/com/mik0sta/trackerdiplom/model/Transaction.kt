package com.mik0sta.trackerdiplom.model

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String //
)


