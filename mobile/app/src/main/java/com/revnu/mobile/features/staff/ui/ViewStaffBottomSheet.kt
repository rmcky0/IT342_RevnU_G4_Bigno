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


class ViewStaffBottomSheet : BottomSheetDialogFragment() {

    private lateinit var staff: StaffResponse

    companion object {
        private const val ARG = "staff"
        fun newInstance(staff: StaffResponse) = ViewStaffBottomSheet().apply {
            arguments = Bundle().apply { putSerializable(ARG, staff) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        staff = arguments?.getSerializable(ARG) as StaffResponse
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_view_staff, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val initial = staff.fullname.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        view.findViewById<TextView>(R.id.tvDetailAvatar).text    = initial
        view.findViewById<TextView>(R.id.tvDetailName).text      = staff.fullname
        view.findViewById<TextView>(R.id.tvDetailPosition).text  = staff.position
        view.findViewById<TextView>(R.id.tvDetailRate).text      = "₱${String.format("%,.2f", staff.salaryRate)}"

        view.findViewById<MaterialButton>(R.id.btnDetailClose).setOnClickListener { dismiss() }
    }
}
