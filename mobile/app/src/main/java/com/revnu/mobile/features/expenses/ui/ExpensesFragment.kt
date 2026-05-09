package com.revnu.mobile.features.expenses.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.revnu.mobile.R
import com.revnu.mobile.features.expenses.model.ExpenseResponse
import com.revnu.mobile.features.expenses.viewmodel.ExpensesViewModel
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment(R.layout.fragment_expenses) {

    private lateinit var viewModel: ExpensesViewModel
    private lateinit var adapter: ExpensesAdapter

    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvTotalExpenses: TextView
    private var selectedReceiptUri: Uri? = null

    // UI elements inside the dialog that need to be updated by the picker
    private var ivReceiptPreview: ImageView? = null
    private var tvReceiptName: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ExpensesViewModel::class.java]

        bindViews(view)
        setupRecyclerView()
        setupListeners(view)
        setupObservers()

        viewModel.loadExpenses()
    }

    private fun bindViews(view: View) {
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        rvExpenses = view.findViewById(R.id.rvExpenses)
        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses)
    }
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedReceiptUri = uri
            ivReceiptPreview?.setImageURI(uri)
            ivReceiptPreview?.visibility = View.VISIBLE
            tvReceiptName?.visibility = View.VISIBLE
        }
    }
    private fun setupListeners(view: View) {
        val btnAddExpense = view.findViewById<MaterialButton>(R.id.btnAddExpense)
        val btnLockRecords = view.findViewById<MaterialButton>(R.id.btnLockRecords)

        btnAddExpense.setOnClickListener { showExpenseModal(null) }

        btnLockRecords.setOnClickListener {
            showStrictLockConfirmation()
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpensesAdapter { clickedExpense ->
            handleExpenseClick(clickedExpense)
        }
        rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        rvExpenses.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.expenses.collect { expenseList ->
                val activeExpenses = expenseList.filter { it.status == "ACTIVE" }
                adapter.updateData(activeExpenses)

                if (activeExpenses.isEmpty()) {
                    layoutEmptyState.visibility = View.VISIBLE
                    rvExpenses.visibility = View.GONE
                } else {
                    layoutEmptyState.visibility = View.GONE
                    rvExpenses.visibility = View.VISIBLE
                }

                val total = activeExpenses.sumOf { it.amount }
                tvTotalExpenses.text = "₱${String.format("%.2f", total)}"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEodLocked.collect { isLocked ->
                if (isLocked) {
                    val btnAddExpense = view?.findViewById<MaterialButton>(R.id.btnAddExpense)
                    val btnLockRecords = view?.findViewById<MaterialButton>(R.id.btnLockRecords)

                    btnAddExpense?.isEnabled = false
                    btnAddExpense?.alpha = 0.5f

                    btnLockRecords?.isEnabled = false
                    btnLockRecords?.text = "Locked"
                    btnLockRecords?.alpha = 0.5f
                }
            }
        }
    }

    private fun handleExpenseClick(expense: ExpenseResponse) {
        if (viewModel.isEodLocked.value) {
            Toast.makeText(requireContext(), "Cannot modify records after EOD is locked.", Toast.LENGTH_SHORT).show()
            return
        }

        val options = arrayOf("Edit Record", "Delete Record")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Manage Expense")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showExpenseModal(expense)
                    1 -> confirmDelete(expense)
                }
            }
            .show()
    }

    private fun confirmDelete(expense: ExpenseResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Record?")
            .setMessage("Are you sure you want to delete this expense? This cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteExpense(expense.id) }
            .show()
    }

    private fun showStrictLockConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Lock End of Day?")
            .setMessage("Are you absolutely sure you want to lock today's records? \n\nYou will NOT be able to add or edit any more expenses for today once locked.")
            .setCancelable(false)
            .setPositiveButton("Yes, Lock EOD") { dialog, _ ->
                viewModel.lockEod()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Passing null means "Add New", passing an object means "Edit"
    private fun showExpenseModal(expenseToEdit: ExpenseResponse?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_expense, null)
        dialog.setContentView(view)

        // Reset the URI every time the modal opens
        selectedReceiptUri = null

        val etAmount = view.findViewById<TextInputEditText>(R.id.etSaleAmount)
        val etNotes = view.findViewById<TextInputEditText>(R.id.etSaleNotes)
        val etTags = view.findViewById<TextInputEditText>(R.id.etSaleTags)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveSale)

        val btnAttachReceipt = view.findViewById<MaterialButton>(R.id.btnAttachReceipt)
        ivReceiptPreview = view.findViewById(R.id.ivReceiptPreview)
        tvReceiptName = view.findViewById(R.id.tvReceiptName)

        // Launch the gallery when clicked
        btnAttachReceipt.setOnClickListener {
            pickImageLauncher.launch("image/*") // Only show images
        }

        if (expenseToEdit != null) {

            etAmount.setText(expenseToEdit.amount.toString())
            etNotes.setText(expenseToEdit.description ?: "")
            etTags.setText(expenseToEdit.tags?.joinToString(", ") ?: "")
            btnSave.text = "Update Record"

            // If editing and it has a file, show a placeholder
            if (expenseToEdit.fileId != null) {
                tvReceiptName?.text = "Receipt attached"
                tvReceiptName?.visibility = View.VISIBLE
            }
        }

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val notes = etNotes.text.toString()
            val tagsText = etTags.text.toString()

            if (amountText.isNotBlank()) {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                // Pass the selected URI to the ViewModel
                if (expenseToEdit == null) {
                    viewModel.addExpense(requireContext(), amount, tagsList,notes,  selectedReceiptUri)
                } else {
                    viewModel.editExpense(expenseToEdit.id, amount, tagsList, notes, selectedReceiptUri)
                }
                dialog.dismiss()
            } else {
                etAmount.error = "Amount is required"
            }
        }
        dialog.show()
    }
}