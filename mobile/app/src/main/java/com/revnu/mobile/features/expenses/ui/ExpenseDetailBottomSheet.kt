package com.revnu.mobile.features.expenses.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.revnu.mobile.R
import com.revnu.mobile.core.config.Constants
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseDetailBottomSheet : BottomSheetDialogFragment() {

    var onEdit: (() -> Unit)? = null

    companion object {
        fun newInstance(expense: ExpenseResponse): ExpenseDetailBottomSheet {
            return ExpenseDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble("amount", expense.amount)
                    putString("category", expense.categoryName ?: "UNCATEGORIZED")
                    putString("notes", expense.notes)
                    putString("created_at", expense.createdAt)
                    putString("file_id", expense.fileId)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_view_expense, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amount = arguments?.getDouble("amount", 0.0) ?: 0.0
        val category = arguments?.getString("category") ?: "—"
        val notes = arguments?.getString("notes")
        val createdAt = arguments?.getString("created_at") ?: ""
        val fileId = arguments?.getString("file_id")

        view.findViewById<TextView>(R.id.tvExpDetailAmount).text =
            "- ₱${String.format("%.2f", amount)}"
        view.findViewById<TextView>(R.id.tvExpDetailCategory).text = category.uppercase()
        view.findViewById<TextView>(R.id.tvExpDetailNotes).text =
            if (notes.isNullOrBlank()) "No notes" else notes

        val tvReceipt = view.findViewById<TextView>(R.id.tvExpDetailReceipt)
        val cardReceiptImage = view.findViewById<MaterialCardView>(R.id.cardReceiptImage)
        val ivReceiptImage = view.findViewById<ImageView>(R.id.ivReceiptImage)

        if (fileId != null) {
            tvReceipt.text = "Attached"
            tvReceipt.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
            cardReceiptImage.visibility = View.VISIBLE
            loadReceiptImage(fileId, ivReceiptImage)
        } else {
            tvReceipt.text = "None"
            cardReceiptImage.visibility = View.GONE
        }

        val tvDate = view.findViewById<TextView>(R.id.tvExpDetailDate)
        try {
            val raw = createdAt.substringBefore(".")
            val inFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outFmt = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
            tvDate.text = inFmt.parse(raw)?.let { outFmt.format(it) } ?: createdAt
        } catch (_: Exception) {
            tvDate.text = createdAt
        }

        view.findViewById<MaterialButton>(R.id.btnExpDetailEdit).setOnClickListener {
            dismiss()
            onEdit?.invoke()
        }
        view.findViewById<MaterialButton>(R.id.btnExpDetailClose).setOnClickListener { dismiss() }
    }

    private fun loadReceiptImage(fileId: String, imageView: ImageView) {
        val token = SessionManager(requireContext()).fetchAccessToken() ?: return
        val url = "${Constants.BASE_URL}files/$fileId"
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                val bytes = client.newCall(request).execute().body?.bytes() ?: return@launch
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    if (isAdded) imageView.setImageBitmap(bitmap)
                }
            } catch (_: Exception) {}
        }
    }
}
