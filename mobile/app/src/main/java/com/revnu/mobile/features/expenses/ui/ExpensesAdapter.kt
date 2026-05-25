package com.revnu.mobile.features.expenses.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.revnu.mobile.R
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import java.text.SimpleDateFormat
import java.util.Locale

class ExpensesAdapter(
    private var expensesList: List<ExpenseResponse> = emptyList(),
    private val onExpenseClicked: (ExpenseResponse) -> Unit
) : RecyclerView.Adapter<ExpensesAdapter.ExpensesViewHolder>() {

    fun updateData(newList: List<ExpenseResponse>) {
        this.expensesList = newList.filter { it.status == "OPEN" }
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): ExpenseResponse = expensesList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpensesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpensesViewHolder, position: Int) {
        val expense = expensesList[position]
        holder.bind(expense)

        holder.itemView.setOnClickListener {
            onExpenseClicked(expense)
        }
    }

    override fun getItemCount(): Int = expensesList.size

    class ExpensesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val ivReceiptIcon: ImageView = itemView.findViewById(R.id.ivReceiptIcon)

        fun bind(expense: ExpenseResponse) {
            try {
                val rawDateString = expense.createdAt.substringBefore(".")
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())

                val dateObj = inputFormat.parse(rawDateString)

                if (dateObj != null) {
                    tvDateTime.text = outputFormat.format(dateObj)
                } else {
                    tvDateTime.text = expense.createdAt
                }
            } catch (e: Exception) {
                tvDateTime.text = expense.createdAt
            }

            tvAmount.text = "- ₱${String.format("%.2f", expense.amount)}"

            tvNotes.text = if (expense.notes.isNullOrBlank()) "No notes" else expense.notes

            val displayCategory = expense.categoryName ?: "UNCATEGORIZED"
            tvCategory.text = displayCategory.uppercase()

            if (expense.fileId != null) {
                ivReceiptIcon.visibility = View.VISIBLE
            } else {
                ivReceiptIcon.visibility = View.GONE
            }
        }
    }
}