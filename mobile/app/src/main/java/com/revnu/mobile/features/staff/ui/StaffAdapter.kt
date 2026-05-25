package com.revnu.mobile.features.staff.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.StaffResponse

class StaffAdapter(
    private val onItemShortClick: (StaffResponse) -> Unit,
    private val onItemLongClick: (StaffResponse) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    private var staffList = listOf<StaffResponse>()

    fun submitList(newList: List<StaffResponse>) {
        staffList = newList
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): StaffResponse = staffList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]
        holder.bind(staff)

        holder.itemView.setOnClickListener {
            onItemShortClick(staff)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(staff)
            true
        }
    }

    override fun getItemCount() = staffList.size

    class StaffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvStaffName)
        private val tvPosition: TextView = itemView.findViewById(R.id.tvStaffPosition)
        private val tvRate: TextView = itemView.findViewById(R.id.tvStaffRate)

        fun bind(staff: StaffResponse) {
            tvName.text = staff.fullname
            tvPosition.text = staff.position.uppercase()
            tvRate.text = "₱${String.format("%.2f", staff.salaryRate)} / day"
        }
    }
}