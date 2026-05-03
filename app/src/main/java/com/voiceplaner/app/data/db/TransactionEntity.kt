package com.voiceplaner.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val type: String,
    val amount: Long,
    val description: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)
