package com.voiceplaner.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date LIKE :yearMonth || '%' ORDER BY date DESC")
    fun getTransactionsByMonth(yearMonth: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income' AND date LIKE :yearMonth || '%'")
    fun getMonthlyIncome(yearMonth: String): Flow<Long?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND date LIKE :yearMonth || '%'")
    fun getMonthlyExpense(yearMonth: String): Flow<Long?>

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'expense' AND date LIKE :yearMonth || '%' GROUP BY category")
    fun getCategoryExpenses(yearMonth: String): Flow<List<CategorySum>>
}

data class CategorySum(val category: String, val total: Long)
