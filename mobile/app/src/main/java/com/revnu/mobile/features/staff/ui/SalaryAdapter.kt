package com.revnu.mobile.features.staff.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.SalaryResponse
import java.text.SimpleDateFormat
import java.util.Locale

class SalaryAdapter(
    private val onItemClick: (SalaryResponse) -> Unit
) : RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder>() {

    private var salaryList = listOf<SalaryResponse>()

    fun submitList(newList: List<SalaryResponse>) {
        salaryList = newList
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): SalaryResponse = salaryList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_salary, parent, false)
        return SalaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalaryViewHolder, position: Int) {
        val salary = salaryList[position]
        holder.bind(salary)
        holder.itemView.setOnClickListener { onItemClick(salary) }
    }

    override fun getItemCount() = salaryList.size

    class SalaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStaffName: TextView = itemView.findViewById(R.id.tvSalaryStaffName)
        private val tvPaymentDate: TextView = itemView.findViewById(R.id.tvPaymentDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvSalaryAmount)
        private val chipCard: MaterialCardView = itemView.findViewById(R.id.chipSalaryStatus)
        private val tvStatusText: TextView = itemView.findViewById(R.id.tvStatusText)

        fun bind(salary: SalaryResponse) {
            tvStaffName.text = salary.staffName
            tvAmount.text = "₱${String.format("%,.2f", salary.amount)}"

            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                val dateObj = inputFormat.parse(salary.paymentDate)
                tvPaymentDate.text = dateObj?.let { outputFormat.format(it) } ?: salary.paymentDate
            } catch (_: Exception) {
                tvPaymentDate.text = salary.paymentDate
            }

            tvStatusText.text = salary.status.uppercase()
            if (salary.status == "OPEN") {
                chipCard.setCardBackgroundColor(Color.parseColor("#E0E7FF"))
                tvStatusText.setTextColor(Color.parseColor("#6366F1"))
            } else {
                chipCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                tvStatusText.setTextColor(Color.parseColor("#9CA3AF"))
            }
        }
    }
}
