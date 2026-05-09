package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import java.util.*

class ConfirmDeleteBottomSheet : BottomSheetDialogFragment() {

    var onConfirm: (() -> Unit)? = null

    companion object {
        private const val ARG_TITLE   = "title"
        private const val ARG_MESSAGE = "message"
        fun newInstance(title: String, message: String) = ConfirmDeleteBottomSheet().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_confirm_delete, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvDeleteTitle).text   = arguments?.getString(ARG_TITLE)   ?: "Confirm Delete"
        view.findViewById<TextView>(R.id.tvDeleteMessage).text = arguments?.getString(ARG_MESSAGE) ?: "Are you sure?"

        view.findViewById<MaterialButton>(R.id.btnDeleteConfirm).setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.btnDeleteCancel).setOnClickListener { dismiss() }
    }
}