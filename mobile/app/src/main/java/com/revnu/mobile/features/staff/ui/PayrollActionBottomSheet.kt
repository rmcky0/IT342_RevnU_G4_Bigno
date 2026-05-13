package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.SalaryResponse


class PayrollActionBottomSheet : BottomSheetDialogFragment() {

    var onView:   (() -> Unit)? = null
    var onEdit:   (() -> Unit)? = null
    var onDelete: (() -> Unit)? = null

    private lateinit var salary: SalaryResponse

    companion object {
        private const val ARG = "salary"
        fun newInstance(salary: SalaryResponse) = PayrollActionBottomSheet().apply {
            arguments = Bundle().apply { putSerializable(ARG, salary) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        salary = arguments?.getSerializable(ARG) as SalaryResponse
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_action_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvActionTitle).text    = salary.employeeName ?: "Salary Record"
        view.findViewById<TextView>(R.id.tvActionSubtitle).text = "₱${String.format("%,.2f", salary.amount)}"

        view.findViewById<LinearLayout>(R.id.layoutActionView).setOnClickListener   { dismiss(); onView?.invoke() }
        view.findViewById<LinearLayout>(R.id.layoutActionEdit).setOnClickListener   { dismiss(); onEdit?.invoke() }
        view.findViewById<LinearLayout>(R.id.layoutActionDelete).setOnClickListener { dismiss(); onDelete?.invoke() }
    }
}
