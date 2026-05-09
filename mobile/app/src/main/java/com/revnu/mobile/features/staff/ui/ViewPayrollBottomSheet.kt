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


class ViewPayrollBottomSheet : BottomSheetDialogFragment() {

    private lateinit var salary: SalaryResponse

    companion object {
        private const val ARG = "salary"
        fun newInstance(salary: SalaryResponse) = ViewPayrollBottomSheet().apply {
            arguments = Bundle().apply { putSerializable(ARG, salary) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        salary = arguments?.getSerializable(ARG) as SalaryResponse
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_view_payroll, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val initial = salary.employeeName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        view.findViewById<TextView>(R.id.tvPayDetailAvatar).text  = initial
        view.findViewById<TextView>(R.id.tvPayDetailName).text    = salary.employeeName ?: "Unknown"
        view.findViewById<TextView>(R.id.tvPayDetailDate).text    = formatDate(salary.paymentDate)
        view.findViewById<TextView>(R.id.tvPayDetailAmount).text  = "₱${String.format("%,.2f", salary.amount)}"
        view.findViewById<TextView>(R.id.tvPayDetailStatus).text  = salary.status?.name ?: "OPEN"

        view.findViewById<MaterialButton>(R.id.btnPayDetailClose).setOnClickListener { dismiss() }
    }

    private fun formatDate(raw: String?): String {
        if (raw.isNullOrBlank()) return "—"
        return try {
            val input  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            output.format(input.parse(raw.substringBefore("T")) ?: return raw)
        } catch (e: Exception) { raw }
    }
}
