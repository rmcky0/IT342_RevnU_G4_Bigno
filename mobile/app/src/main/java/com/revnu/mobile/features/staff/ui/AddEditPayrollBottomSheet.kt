package com.revnu.mobile.features.staff.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.model.StaffResponse
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddEditPayrollBottomSheet : BottomSheetDialogFragment() {

    var onSave: ((staffId: String, amount: BigDecimal, paymentDate: String) -> Unit)? = null

    private var salary:    SalaryResponse?   = null
    private var staffList: List<StaffResponse> = emptyList()
    private val isEdit get() = salary != null

    // Selected state
    private var selectedStaffId = ""
    private var selectedDate    = ""

    companion object {
        private const val ARG_SALARY = "salary"
        private const val ARG_STAFF  = "staff_list_ids"
        fun newInstance(salary: SalaryResponse?, staffList: List<StaffResponse>) =
            AddEditPayrollBottomSheet().apply {
                this.staffList = staffList
                arguments = Bundle().apply { salary?.let { putSerializable(ARG_SALARY, it) } }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        salary = arguments?.getSerializable(ARG_SALARY) as? SalaryResponse
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_add_edit_payroll, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvTitle    = view.findViewById<TextView>(R.id.tvPayFormTitle)
        val spinner    = view.findViewById<Spinner>(R.id.spinnerEmployee)
        val etAmount   = view.findViewById<TextInputEditText>(R.id.etPayAmount)
        val etDate     = view.findViewById<TextInputEditText>(R.id.etPayDate)
        val tilDate    = view.findViewById<TextInputLayout>(R.id.tilPayDate)
        val btnSave    = view.findViewById<MaterialButton>(R.id.btnPayFormSave)
        val btnClose   = view.findViewById<MaterialButton>(R.id.btnPayFormClose)

        tvTitle.text = if (isEdit) "Edit Salary Record" else "Record Salary"
        btnSave.text = if (isEdit) "Save Changes"       else "Record Salary"

        // ── Spinner setup ──
        val names   = staffList.map { it.fullname }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedStaffId = staffList[pos].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // ── Date picker ──
        val today = Calendar.getInstance()
        selectedDate = "${today.get(Calendar.YEAR)}-${String.format("%02d", today.get(Calendar.MONTH)+1)}-${String.format("%02d", today.get(
            Calendar.DAY_OF_MONTH))}"
        etDate.setText(formatDate(selectedDate))
        etDate.isFocusable = false
        tilDate.setEndIconOnClickListener { showDatePicker(etDate) }
        etDate.setOnClickListener          { showDatePicker(etDate) }

        // ── Pre-fill if editing ──
        salary?.let { sal ->
            etAmount.setText(sal.amount.toPlainString())
            selectedDate = sal.paymentDate ?: selectedDate
            etDate.setText(formatDate(selectedDate))
            val idx = staffList.indexOfFirst { it.id == sal.staffId?.toString() }
            if (idx >= 0) spinner.setSelection(idx)
        }

        // ── Save ──
        btnSave.setOnClickListener {
            val amtStr = etAmount.text?.toString()?.trim() ?: ""
            val amt    = amtStr.toBigDecimalOrNull()

            if (selectedStaffId.isEmpty()) { Toast.makeText(requireContext(), "Select an employee", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (amt == null || amt <= BigDecimal.ZERO) {
                view.findViewById<TextInputLayout>(R.id.tilPayAmount).error = "Enter a valid amount"
                return@setOnClickListener
            }
            if (selectedDate.isEmpty()) { Toast.makeText(requireContext(), "Select a payment date", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            onSave?.invoke(selectedStaffId, amt, selectedDate)
            dismiss()
        }

        btnClose.setOnClickListener { dismiss() }
    }

    private fun showDatePicker(etDate: TextInputEditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            selectedDate = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", day)}"
            etDate.setText(formatDate(selectedDate))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun formatDate(raw: String): String {
        return try {
            val input  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            output.format(input.parse(raw) ?: return raw)
        } catch (e: Exception) { raw }
    }
}
