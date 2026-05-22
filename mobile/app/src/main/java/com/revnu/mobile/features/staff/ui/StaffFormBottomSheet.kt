package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.StaffRequest
import com.revnu.mobile.features.staff.model.StaffResponse

class StaffFormBottomSheet : BottomSheetDialogFragment() {

    var onSave: ((StaffRequest) -> Unit)? = null

    companion object {
        private const val ARG_STAFF = "staff_to_edit"

        fun newInstance(staffToEdit: StaffResponse? = null): StaffFormBottomSheet {
            return StaffFormBottomSheet().apply {
                arguments = Bundle().apply {
                    staffToEdit?.let {
                        putString("edit_id", it.id)
                        putString("edit_name", it.fullname)
                        putString("edit_position", it.position)
                        putDouble("edit_rate", it.salaryRate)
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_add_edit_staff, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvFormTitle)
        val etName = view.findViewById<TextInputEditText>(R.id.etStaffName)
        val etPosition = view.findViewById<TextInputEditText>(R.id.etStaffPosition)
        val etRate = view.findViewById<TextInputEditText>(R.id.etStaffRate)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnFormSave)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnFormClose)

        val isEdit = arguments?.containsKey("edit_id") == true
        if (isEdit) {
            tvTitle.text = "Edit Staff Member"
            btnSave.text = "Save Changes"
            etName.setText(arguments?.getString("edit_name"))
            etPosition.setText(arguments?.getString("edit_position"))
            etRate.setText(arguments?.getDouble("edit_rate", 0.0).let {
                if (it == 0.0) "" else it.toString()
            })
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val position = etPosition.text.toString().trim()
            val rate = etRate.text.toString().trim().toDoubleOrNull()

            if (name.isEmpty() || position.isEmpty() || rate == null || rate <= 0) {
                if (name.isEmpty()) etName.error = "Required"
                if (position.isEmpty()) etPosition.error = "Required"
                if (rate == null || rate <= 0) etRate.error = "Enter a valid rate"
                return@setOnClickListener
            }

            onSave?.invoke(StaffRequest(fullname = name, position = position, salaryRate = rate))
            dismiss()
        }

        btnCancel.setOnClickListener { dismiss() }
    }
}
