package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.StaffResponse

class StaffActionBottomSheet : BottomSheetDialogFragment() {

    var onView:   (() -> Unit)? = null
    var onEdit:   (() -> Unit)? = null
    var onDelete: (() -> Unit)? = null

    private lateinit var staff: StaffResponse

    companion object {
        private const val ARG = "staff"
        fun newInstance(staff: StaffResponse) = StaffActionBottomSheet().apply {
            arguments = Bundle().apply { putSerializable(ARG, staff) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        staff = arguments?.getSerializable(ARG) as StaffResponse
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_action_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvActionTitle).text = staff.fullname
        view.findViewById<TextView>(R.id.tvActionSubtitle).text = staff.position

        view.findViewById<LinearLayout>(R.id.layoutActionView).setOnClickListener   { dismiss(); onView?.invoke() }
        view.findViewById<LinearLayout>(R.id.layoutActionEdit).setOnClickListener   { dismiss(); onEdit?.invoke() }
        view.findViewById<LinearLayout>(R.id.layoutActionDelete).setOnClickListener { dismiss(); onDelete?.invoke() }
    }
}