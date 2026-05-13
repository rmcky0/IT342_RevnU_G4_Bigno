package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.StaffResponse
import java.math.BigDecimal


class AddEditStaffBottomSheet : BottomSheetDialogFragment() {

    var onSave: ((fullname: String, position: String, salaryRate: BigDecimal) -> Unit)? = null

    private var staff: StaffResponse? = null
    private val isEdit get() = staff != null

    companion object {
        private const val ARG = "staff"
        fun newInstance(staff: StaffResponse?) = AddEditStaffBottomSheet().apply {
            arguments = Bundle().apply { staff?.let { putSerializable(ARG, it) } }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        staff = arguments?.getSerializable(ARG) as? StaffResponse
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_add_edit_staff, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvTitle  = view.findViewById<TextView>(R.id.tvFormTitle)
        val etName   = view.findViewById<TextInputEditText>(R.id.etStaffName)
        val etPos    = view.findViewById<TextInputEditText>(R.id.etStaffPosition)
        val etRate   = view.findViewById<TextInputEditText>(R.id.etStaffRate)
        val btnSave  = view.findViewById<MaterialButton>(R.id.btnFormSave)
        val btnClose = view.findViewById<MaterialButton>(R.id.btnFormClose)

        tvTitle.text = if (isEdit) "Edit Staff Member" else "Add Staff Member"
        btnSave.text = if (isEdit) "Save Changes"      else "Add Staff"

        // Pre-fill if editing
        staff?.let {
            etName.setText(it.fullname)
            etPos.setText(it.position)
            etRate.setText(it.salaryRate.toPlainString())
        }

        btnSave.setOnClickListener {
            val name     = etName.text?.toString()?.trim() ?: ""
            val position = etPos.text?.toString()?.trim()  ?: ""
            val rateStr  = etRate.text?.toString()?.trim() ?: ""

            // Simple validation
            if (name.isEmpty())  { view.findViewById<TextInputLayout>(R.id.tilStaffName).error = "Required"; return@setOnClickListener }
            if (position.isEmpty()) { view.findViewById<TextInputLayout>(R.id.tilStaffPosition).error = "Required"; return@setOnClickListener }
            val rate = rateStr.toBigDecimalOrNull()
            if (rate == null || rate <= BigDecimal.ZERO) {
                view.findViewById<TextInputLayout>(R.id.tilStaffRate).error = "Enter a valid amount"
                return@setOnClickListener
            }

            onSave?.invoke(name, position, rate)
            dismiss()
        }

        btnClose.setOnClickListener { dismiss() }
    }
}