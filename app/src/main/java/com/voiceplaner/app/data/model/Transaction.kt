package com.voiceplaner.app.data.model

data class Transaction(
    val id: Long = 0,
    val date: String,
    val type: TransactionType,
    val amount: Long,
    val description: String,
    val category: String
)

enum class TransactionType { INCOME, EXPENSE }
