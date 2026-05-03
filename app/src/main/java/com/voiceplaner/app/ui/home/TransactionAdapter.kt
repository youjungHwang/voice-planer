package com.voiceplaner.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.voiceplaner.app.data.model.Transaction
import com.voiceplaner.app.data.model.TransactionType
import com.voiceplaner.app.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val b: ItemTransactionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Transaction) {
            b.tvDescription.text = item.description
            b.tvCategory.text = item.category
            b.tvDate.text = item.date
            val formatted = NumberFormat.getNumberInstance(Locale.KOREA).format(item.amount) + "원"
            if (item.type == TransactionType.INCOME) {
                b.tvAmount.text = "+$formatted"
                b.tvAmount.setTextColor(b.root.context.getColor(android.R.color.holo_blue_dark))
            } else {
                b.tvAmount.text = "-$formatted"
                b.tvAmount.setTextColor(b.root.context.getColor(android.R.color.holo_red_dark))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(old: Transaction, new: Transaction) = old.id == new.id
            override fun areContentsTheSame(old: Transaction, new: Transaction) = old == new
        }
    }
}
