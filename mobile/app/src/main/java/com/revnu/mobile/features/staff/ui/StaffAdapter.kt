package com.revnu.mobile.features.staff.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.model.StaffResponse
import java.text.SimpleDateFormat
import java.util.Locale

class StaffAdapter(
    private val onItemClick: (Any) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Any> = emptyList()
    private var isDirectoryMode = true

    fun setData(newList: List<Any>, isDirectory: Boolean) {
        isDirectoryMode = isDirectory
        items = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int) = if (isDirectoryMode) VIEW_STAFF else VIEW_SALARY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_STAFF -> {
                val view = inflater.inflate(R.layout.item_staff_card, parent, false)
                StaffViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_salary_card, parent, false)
                SalaryViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is StaffViewHolder  -> if (item is StaffResponse)  { holder.bind(item); holder.itemView.setOnClickListener { onItemClick(item) } }
            is SalaryViewHolder -> if (item is SalaryResponse) { holder.bind(item); holder.itemView.setOnClickListener { onItemClick(item) } }
        }
    }

    // ── Staff card ViewHolder ─────────────────────────────────────────────────

    class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvAvatar:   TextView = view.findViewById(R.id.tvStaffAvatar)
        private val tvName:     TextView = view.findViewById(R.id.tvEmpName)
        private val tvPosition: TextView = view.findViewById(R.id.tvEmpPosition)
        private val tvRate:     TextView = view.findViewById(R.id.tvEmpRate)

        fun bind(emp: StaffResponse) {
            tvAvatar.text   = emp.fullname.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            tvName.text     = emp.fullname
            tvPosition.text = emp.position
            tvRate.text     = "₱${String.format("%,.2f", emp.salaryRate)}"
        }
    }

    // ── Salary card ViewHolder ────────────────────────────────────────────────

    class SalaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvAvatar:   TextView = view.findViewById(R.id.tvSalaryAvatar)
        private val tvName:     TextView = view.findViewById(R.id.tvSalaryEmpName)
        private val tvDate:     TextView = view.findViewById(R.id.tvSalaryDate)
        private val tvAmount:   TextView = view.findViewById(R.id.tvSalaryAmount)
        private val tvStatus:   TextView = view.findViewById(R.id.tvSalaryStatus)

        fun bind(pay: SalaryResponse) {
            tvAvatar.text  = pay.employeeName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            tvName.text    = pay.employeeName ?: "Unknown Employee"
            tvDate.text    = formatDate(pay.paymentDate)
            tvAmount.text  = "₱${String.format("%,.2f", pay.amount)}"
            tvStatus.text  = pay.status?.name ?: "OPEN"
        }

        private fun formatDate(raw: String?): String {
            if (raw.isNullOrBlank()) return "—"
            return try {
                val input  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val output = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date   = input.parse(raw.substringBefore("T"))
                if (date != null) output.format(date) else raw
            } catch (e: Exception) { raw }
        }
    }

    companion object {
        private const val VIEW_STAFF  = 0
        private const val VIEW_SALARY = 1
    }
}