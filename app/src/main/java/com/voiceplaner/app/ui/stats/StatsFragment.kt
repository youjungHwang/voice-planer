package com.voiceplaner.app.ui.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.voiceplaner.app.databinding.FragmentStatsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()

    private val chartColors = listOf(
        Color.parseColor("#FF6B6B"), Color.parseColor("#4ECDC4"),
        Color.parseColor("#45B7D1"), Color.parseColor("#96CEB4"),
        Color.parseColor("#FFEAA7"), Color.parseColor("#DDA0DD")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            legend.isEnabled = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.monthlyIncome.collect { binding.tvIncome.text = "수입: ${fmt(it ?: 0)}" }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.monthlyExpense.collect { binding.tvExpense.text = "지출: ${fmt(it ?: 0)}" }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryExpenses.collect { categories ->
                if (categories.isEmpty()) { binding.pieChart.visibility = View.GONE; return@collect }
                binding.pieChart.visibility = View.VISIBLE
                val entries = categories.map { PieEntry(it.total.toFloat(), it.category) }
                val dataSet = PieDataSet(entries, "카테고리").apply { colors = chartColors }
                binding.pieChart.data = PieData(dataSet)
                binding.pieChart.invalidate()
            }
        }
    }

    private fun fmt(amount: Long) = NumberFormat.getNumberInstance(Locale.KOREA).format(amount) + "원"

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
