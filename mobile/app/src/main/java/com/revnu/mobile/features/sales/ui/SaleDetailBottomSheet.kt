package com.revnu.mobile.features.sales.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.features.sales.model.SaleResponse
import java.text.SimpleDateFormat
import java.util.Locale

class SaleDetailBottomSheet : BottomSheetDialogFragment() {

    var onEdit: (() -> Unit)? = null

    companion object {
        fun newInstance(sale: SaleResponse): SaleDetailBottomSheet {
            return SaleDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble("amount", sale.amount)
                    putString("category", sale.categoryName ?: "UNCATEGORIZED")
                    putString("notes", sale.notes)
                    putString("created_at", sale.createdAt)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_view_sale, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amount = arguments?.getDouble("amount", 0.0) ?: 0.0
        val category = arguments?.getString("category") ?: "—"
        val notes = arguments?.getString("notes")
        val createdAt = arguments?.getString("created_at") ?: ""

        view.findViewById<TextView>(R.id.tvSaleDetailAmount).text =
            "₱${String.format("%.2f", amount)}"
        view.findViewById<TextView>(R.id.tvSaleDetailCategory).text = category.uppercase()
        view.findViewById<TextView>(R.id.tvSaleDetailNotes).text =
            if (notes.isNullOrBlank()) "No notes" else notes

        val tvDate = view.findViewById<TextView>(R.id.tvSaleDetailDate)
        try {
            val raw = createdAt.substringBefore(".")
            val inFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outFmt = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
            tvDate.text = inFmt.parse(raw)?.let { outFmt.format(it) } ?: createdAt
        } catch (e: Exception) {
            tvDate.text = createdAt
        }

        view.findViewById<MaterialButton>(R.id.btnSaleDetailEdit).setOnClickListener {
            dismiss()
            onEdit?.invoke()
        }
        view.findViewById<MaterialButton>(R.id.btnSaleDetailClose).setOnClickListener { dismiss() }
    }
}
