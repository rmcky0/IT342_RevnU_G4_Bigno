package com.revnu.mobile.features.staff.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.SalaryRequest
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.model.StaffResponse
import java.util.Calendar

class SalaryFormBottomSheet : BottomSheetDialogFragment() {

    var onSave: ((SalaryRequest) -> Unit)? = null

    private var staffList: List<StaffResponse> = emptyList()

    companion object {
        fun newInstance(
            staffList: List<StaffResponse>,
            salaryToEdit: SalaryResponse? = null
        ): SalaryFormBottomSheet {
            return SalaryFormBottomSheet().apply {
                this.staffList = staffList
                arguments = Bundle().apply {
                    salaryToEdit?.let {
                        putString("edit_id", it.id)
                        putString("edit_staff_id", it.staffId)
                        putDouble("edit_amount", it.amount)
                        putString("edit_date", it.paymentDate)
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_add_edit_payroll, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvPayFormTitle)
        val spinner = view.findViewById<Spinner>(R.id.spinnerEmployee)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etPayAmount)
        val etDate = view.findViewById<TextInputEditText>(R.id.etPayDate)
        val tilDate = view.findViewById<TextInputLayout>(R.id.tilPayDate)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnPayFormSave)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnPayFormClose)

        val isEdit = arguments?.containsKey("edit_id") == true

        // Populate staff spinner
        val staffNames = staffList.map { it.fullname }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, staffNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        if (isEdit) {
            tvTitle.text = "Edit Salary Record"
            btnSave.text = "Save Changes"
            val editStaffId = arguments?.getString("edit_staff_id")
            val staffIndex = staffList.indexOfFirst { it.id == editStaffId }
            if (staffIndex >= 0) spinner.setSelection(staffIndex)
            etAmount.setText(arguments?.getDouble("edit_amount", 0.0).let {
                if (it == 0.0) "" else it.toString()
            })
            etDate.setText(arguments?.getString("edit_date") ?: "")
            spinner.isEnabled = false // staff cannot be changed on edit
        }

        // Date picker
        val openDatePicker = {
            val cal = Calendar.getInstance()
            val existing = etDate.text.toString()
            if (existing.isNotEmpty()) {
                val parts = existing.split("-")
                if (parts.size == 3) {
                    cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                }
            }
            DatePickerDialog(requireContext(), { _, year, month, day ->
                etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        etDate.setOnClickListener { openDatePicker() }
        tilDate.setEndIconOnClickListener { openDatePicker() }

        btnSave.setOnClickListener {
            val selectedIndex = spinner.selectedItemPosition
            if (selectedIndex < 0 || selectedIndex >= staffList.size) {
                return@setOnClickListener
            }
            val staffId = staffList[selectedIndex].id
            val amount = etAmount.text.toString().trim().toDoubleOrNull()
            val date = etDate.text.toString().trim()

            if (amount == null || amount <= 0) {
                etAmount.error = "Enter a valid amount"
                return@setOnClickListener
            }
            if (date.isEmpty()) {
                etDate.error = "Select a payment date"
                return@setOnClickListener
            }

            onSave?.invoke(SalaryRequest(staffId = staffId, amount = amount, paymentDate = date))
            dismiss()
        }

        btnCancel.setOnClickListener { dismiss() }
    }
}
