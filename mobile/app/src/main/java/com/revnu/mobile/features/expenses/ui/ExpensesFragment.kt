package com.revnu.mobile.features.expenses.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.entry.ui.AddRecordActivity
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import com.revnu.mobile.features.expenses.repository.ExpensesRepository
import com.revnu.mobile.features.expenses.viewmodel.ExpensesViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ExpensesFragment : Fragment(R.layout.fragment_expenses) {
    private lateinit var viewModel: ExpensesViewModel
    private lateinit var adapter: ExpensesAdapter

    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvTotalExpenses: TextView

    private var pendingUploadExpenseId: String? = null

    private val editRecordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) viewModel.loadExpenses()
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageSelected(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = ExpensesRepository(RetrofitClient.apiService)
                @Suppress("UNCHECKED_CAST")
                return ExpensesViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ExpensesViewModel::class.java]

        bindViews(view)
        setupRecyclerView()
        setupListeners(view)
        setupObservers(view)

        viewModel.loadExpenses()
    }

    private fun bindViews(view: View) {
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        rvExpenses = view.findViewById(R.id.rvExpenses)
        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses)
    }

    private fun setupListeners(view: View) {
        view.findViewById<MaterialButton>(R.id.btnLockRecords).setOnClickListener {
            showLockConfirmation()
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpensesAdapter { clickedExpense ->
            showExpenseDetail(clickedExpense)
        }
        rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        rvExpenses.adapter = adapter

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private val paint = Paint().apply { color = Color.parseColor("#EF4444") }

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = adapter.getItemAt(position)
                adapter.notifyItemChanged(position)
                confirmDelete(expense)
            }

            override fun onChildDraw(c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isActive: Boolean) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = vh.itemView
                    c.drawRect(
                        itemView.right + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                    )
                }
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvExpenses)
    }

    private fun setupObservers(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.expenses.collect { expensesList ->
                val activeExpenses = expensesList.filter { it.status == "OPEN" }
                adapter.updateData(activeExpenses)
                layoutEmptyState.visibility = if (activeExpenses.isEmpty()) View.VISIBLE else View.GONE
                rvExpenses.visibility = if (activeExpenses.isEmpty()) View.GONE else View.VISIBLE
                tvTotalExpenses.text = "₱${String.format("%.2f", activeExpenses.sumOf { it.amount })}"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEodLocked.collect { isLocked ->
                if (isLocked) {
                    val btn = view.findViewById<MaterialButton>(R.id.btnLockRecords)
                    btn?.isEnabled = false
                    btn?.text = "Locked"
                    btn?.alpha = 0.5f
                }
            }
        }
    }

    private fun showLockConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Lock End of Day?")
            .setMessage("Are you absolutely sure you want to lock today's expense records?\n\nYou will NOT be able to add or edit any more expenses for today once locked.")
            .setCancelable(false)
            .setPositiveButton("Yes, Lock EOD") { dialog, _ ->
                viewModel.lockEod()
                Toast.makeText(requireContext(), "EOD Locked Successfully.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showExpenseDetail(expense: ExpenseResponse) {
        val sheet = ExpenseDetailBottomSheet.newInstance(expense)
        sheet.onEdit = { showEditExpenseModal(expense) }
        sheet.show(childFragmentManager, "expense_detail")
    }

    private fun showEditExpenseModal(expenseToEdit: ExpenseResponse) {
        val intent = Intent(requireContext(), AddRecordActivity::class.java).apply {
            putExtra(AddRecordActivity.EXTRA_IS_EDIT_MODE, true)
            putExtra(AddRecordActivity.EXTRA_RECORD_ID, expenseToEdit.id)
            putExtra(AddRecordActivity.EXTRA_RECORD_TYPE, "expense")
            putExtra(AddRecordActivity.EXTRA_AMOUNT, expenseToEdit.amount)
            putExtra(AddRecordActivity.EXTRA_DESCRIPTION, expenseToEdit.notes ?: "")
            putExtra(AddRecordActivity.EXTRA_CATEGORY_ID, expenseToEdit.categoryId)
            putExtra(AddRecordActivity.EXTRA_CATEGORY_NAME, expenseToEdit.categoryName ?: "")
        }
        editRecordLauncher.launch(intent)
    }

    private fun confirmDelete(expense: ExpenseResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Record?")
            .setMessage("Are you sure you want to delete this expense? This cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteExpense(expense.id) }
            .show()
    }

    private fun handleImageSelected(uri: Uri) {
        val expenseId = pendingUploadExpenseId ?: return
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File(requireContext().cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { inputStream?.copyTo(it) }

            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            Toast.makeText(requireContext(), "Uploading receipt...", Toast.LENGTH_SHORT).show()
            viewModel.uploadReceipt(expenseId, filePart)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
        } finally {
            pendingUploadExpenseId = null
        }
    }

    fun refresh() {
        if (::viewModel.isInitialized) viewModel.forceRefresh()
    }
}
