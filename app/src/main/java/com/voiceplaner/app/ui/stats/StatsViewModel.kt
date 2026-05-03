package com.voiceplaner.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voiceplaner.app.data.db.CategorySum
import com.voiceplaner.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _month = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
    val selectedMonth: StateFlow<String> = _month

    val monthlyIncome: StateFlow<Long?> = _month.flatMapLatest { repository.getMonthlyIncome(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val monthlyExpense: StateFlow<Long?> = _month.flatMapLatest { repository.getMonthlyExpense(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val categoryExpenses: StateFlow<List<CategorySum>> = _month.flatMapLatest { repository.getCategoryExpenses(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectMonth(yearMonth: String) { _month.value = yearMonth }
}
