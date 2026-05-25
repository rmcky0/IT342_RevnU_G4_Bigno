package com.revnu.mobile.features.sales.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.revnu.mobile.R
import com.revnu.mobile.features.sales.model.SaleResponse
import java.text.SimpleDateFormat
import java.util.Locale

class SalesAdapter(
    private var salesList: List<SaleResponse> = emptyList(),
    private val onSaleClicked: (SaleResponse) -> Unit 
) : RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    fun updateData(newList: List<SaleResponse>) {
        this.salesList = newList.filter { it.status == "OPEN" }
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): SaleResponse = salesList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sale, parent, false)
        return SalesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val sale = salesList[position]
        holder.bind(sale)

        holder.itemView.setOnClickListener {
            onSaleClicked(sale)
        }
    }

    override fun getItemCount(): Int = salesList.size

    class SalesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)

        fun bind(sale: SaleResponse) {
            try {
                val rawDateString = sale.createdAt.substringBefore(".")
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())

                val dateObj = inputFormat.parse(rawDateString)

                if (dateObj != null) {
                    tvDateTime.text = outputFormat.format(dateObj)
                } else {
                    tvDateTime.text = sale.createdAt
                }
            } catch (e: Exception) {
                tvDateTime.text = sale.createdAt
            }

            tvAmount.text = "₱${String.format("%.2f", sale.amount)}"

            tvNotes.text = if (sale.notes.isNullOrBlank()) "No notes" else sale.notes

            tvCategory.text = if (sale.categoryName.isNullOrBlank()) "UNCATEGORIZED" else sale.categoryName


        }
    }
}