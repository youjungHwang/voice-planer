package com.voiceplaner.app.data.repository

import com.voiceplaner.app.data.db.AppDatabase
import com.voiceplaner.app.data.db.CategorySum
import com.voiceplaner.app.data.db.TransactionEntity
import com.voiceplaner.app.data.model.Transaction
import com.voiceplaner.app.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(private val db: AppDatabase) {

    fun getAllTransactions(): Flow<List<Transaction>> =
        db.transactionDao().getAllTransactions().map { it.map(TransactionEntity::toDomain) }

    fun getTransactionsByMonth(yearMonth: String): Flow<List<Transaction>> =
        db.transactionDao().getTransactionsByMonth(yearMonth).map { it.map(TransactionEntity::toDomain) }

    fun getMonthlyIncome(yearMonth: String): Flow<Long?> =
        db.transactionDao().getMonthlyIncome(yearMonth)

    fun getMonthlyExpense(yearMonth: String): Flow<Long?> =
        db.transactionDao().getMonthlyExpense(yearMonth)

    fun getCategoryExpenses(yearMonth: String): Flow<List<CategorySum>> =
        db.transactionDao().getCategoryExpenses(yearMonth)

    suspend fun insert(transaction: Transaction) =
        db.transactionDao().insert(transaction.toEntity())

    suspend fun delete(transaction: Transaction) =
        db.transactionDao().delete(transaction.toEntity())
}

private fun TransactionEntity.toDomain() = Transaction(
    id = id, date = date,
    type = if (type == "income") TransactionType.INCOME else TransactionType.EXPENSE,
    amount = amount, description = description, category = category
)

private fun Transaction.toEntity() = TransactionEntity(
    id = id, date = date,
    type = if (type == TransactionType.INCOME) "income" else "expense",
    amount = amount, description = description, category = category
)
