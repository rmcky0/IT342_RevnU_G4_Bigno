package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.SalaryResponse
import java.text.SimpleDateFormat
import java.util.Locale

class SalaryDetailBottomSheet : BottomSheetDialogFragment() {

    var onEdit: (() -> Unit)? = null

    companion object {
        fun newInstance(salary: SalaryResponse): SalaryDetailBottomSheet {
            return SalaryDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("staff_name", salary.staffName)
                    putString("payment_date", salary.paymentDate)
                    putString("status", salary.status)
                    putDouble("amount", salary.amount)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_view_payroll, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val staffName = arguments?.getString("staff_name") ?: ""
        val paymentDate = arguments?.getString("payment_date") ?: ""
        val status = arguments?.getString("status") ?: ""
        val amount = arguments?.getDouble("amount", 0.0) ?: 0.0

        view.findViewById<TextView>(R.id.tvPayDetailAvatar).text =
            staffName.firstOrNull()?.uppercase() ?: "?"
        view.findViewById<TextView>(R.id.tvPayDetailName).text = staffName
        view.findViewById<TextView>(R.id.tvPayDetailAmount).text =
            "₱${String.format("%.2f", amount)}"

        val tvDate = view.findViewById<TextView>(R.id.tvPayDetailDate)
        try {
            val inputFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val parsed = inputFmt.parse(paymentDate)
            tvDate.text = parsed?.let { outputFmt.format(it) } ?: paymentDate
        } catch (e: Exception) {
            tvDate.text = paymentDate
        }

        val tvStatus = view.findViewById<TextView>(R.id.tvPayDetailStatus)
        tvStatus.text = status
        when (status) {
            "OPEN" -> {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_green_soft)
                tvStatus.setTextColor(android.graphics.Color.parseColor("#059669"))
            }
            "PAID" -> {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_green_soft)
                tvStatus.setTextColor(android.graphics.Color.parseColor("#059669"))
            }
            else -> {
                tvStatus.setTextColor(android.graphics.Color.parseColor("#9CA3AF"))
            }
        }

        val btnEdit = view.findViewById<MaterialButton>(R.id.btnPayDetailEdit)
        if (status == "OPEN") {
            btnEdit.visibility = View.VISIBLE
            btnEdit.setOnClickListener {
                dismiss()
                onEdit?.invoke()
            }
        }

        view.findViewById<MaterialButton>(R.id.btnPayDetailClose).setOnClickListener { dismiss() }
    }
}
