package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.StaffResponse

class StaffDetailBottomSheet : BottomSheetDialogFragment() {

    var onEdit: (() -> Unit)? = null
    var onDelete: (() -> Unit)? = null

    companion object {
        fun newInstance(staff: StaffResponse): StaffDetailBottomSheet {
            return StaffDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("name", staff.fullname)
                    putString("position", staff.position)
                    putDouble("rate", staff.salaryRate)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_view_staff, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name") ?: ""
        val position = arguments?.getString("position") ?: ""
        val rate = arguments?.getDouble("rate", 0.0) ?: 0.0

        view.findViewById<TextView>(R.id.tvDetailAvatar).text = name.firstOrNull()?.uppercase() ?: "?"
        view.findViewById<TextView>(R.id.tvDetailName).text = name
        view.findViewById<TextView>(R.id.tvDetailPosition).text = position
        view.findViewById<TextView>(R.id.tvDetailRate).text = "₱${String.format("%.2f", rate)} / day"

        view.findViewById<MaterialButton>(R.id.btnDetailEdit).setOnClickListener {
            dismiss()
            onEdit?.invoke()
        }
        view.findViewById<MaterialButton>(R.id.btnDetailClose).setOnClickListener { dismiss() }
    }
}
