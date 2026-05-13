package com.revnu.mobile.features.sales.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.revnu.mobile.features.sales.model.SaleResponse
import com.revnu.mobile.features.sales.viewmodel.SalesViewModel
import kotlinx.coroutines.launch

class SalesFragment : Fragment(R.layout.fragment_sales) {

    private lateinit var viewModel: SalesViewModel
    private lateinit var adapter: SalesAdapter

    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var rvSales: RecyclerView
    private lateinit var tvTotalRevenue: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SalesViewModel::class.java]

        bindViews(view)
        setupRecyclerView()
        setupListeners(view)
        setupObservers()

        viewModel.loadSales()
    }

    private fun bindViews(view: View) {
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        rvSales = view.findViewById(R.id.rvSales)
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue)
    }

    private fun setupListeners(view: View) {
        val btnAddSale = view.findViewById<MaterialButton>(R.id.btnAddSale)
        val btnLockRecords = view.findViewById<MaterialButton>(R.id.btnLockRecords)

        btnAddSale.setOnClickListener {
            showAddSaleModal()
        }

        btnLockRecords.setOnClickListener {
            showStrictLockConfirmation()
        }
    }

    private fun setupRecyclerView() {
        // Pass the click behavior into the adapter
        adapter = SalesAdapter { clickedSale ->
            handleSaleClick(clickedSale)
        }
        rvSales.layoutManager = LinearLayoutManager(requireContext())
        rvSales.adapter = adapter
    }


    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sales.collect { salesList ->
                // 1. Filter the list first
                val activeSales = salesList.filter { it.status == "OPEN" }
                // 2. Pass only the active sales to the adapter
                adapter.updateData(activeSales)

                // Toggle Empty State
                if (activeSales.isEmpty()) {
                    layoutEmptyState.visibility = View.VISIBLE
                    rvSales.visibility = View.GONE
                } else {
                    layoutEmptyState.visibility = View.GONE
                    rvSales.visibility = View.VISIBLE
                }

                // Update the Big Total Revenue Card
                val total = activeSales.sumOf { it.amount }
                tvTotalRevenue.text = "₱${String.format("%.2f", total)}"
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEodLocked.collect { isLocked ->
                if (isLocked) {
                    val btnAddSale = view?.findViewById<MaterialButton>(R.id.btnAddSale)
                    val btnLockRecords = view?.findViewById<MaterialButton>(R.id.btnLockRecords)

                    // Disable the buttons and change their appearance to look "locked"
                    btnAddSale?.isEnabled = false
                    btnAddSale?.alpha = 0.5f

                    btnLockRecords?.isEnabled = false
                    btnLockRecords?.text = "Locked"
                    btnLockRecords?.alpha = 0.5f
                }
            }
        }
    }
    private fun showStrictLockConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Lock End of Day?")
            .setMessage("Are you absolutely sure you want to lock today's sales records? \n\nYou will NOT be able to add or edit any more sales for today once locked.")
            .setCancelable(false)
            .setPositiveButton("Yes, Lock EOD") { dialog, _ ->
                viewModel.lockEod()
                Toast.makeText(requireContext(), "EOD Locked Successfully.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun showAddSaleModal() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_sale, null)
        dialog.setContentView(view)

        val etAmount = view.findViewById<TextInputEditText>(R.id.etSaleAmount)
        val etNotes = view.findViewById<TextInputEditText>(R.id.etSaleNotes)
        val etTags = view.findViewById<TextInputEditText>(R.id.etSaleTags)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveSale)

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val notes = etNotes.text.toString()
            val tagsText = etTags.text.toString()

            if (amountText.isNotBlank()) {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                viewModel.addSale(amount, notes, tagsList)
                dialog.dismiss()
            } else {
                etAmount.error = "Amount is required"
            }
        }

        dialog.show()
    }

    private fun handleSaleClick(sale: SaleResponse) {
        // 1. Check if the day is already locked!
        if (viewModel.isEodLocked.value) {
            Toast.makeText(requireContext(), "Cannot modify records after EOD is locked.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Show a quick Action Menu
        val options = arrayOf("Edit Record", "Delete Record")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Manage Sale: ₱${String.format("%.2f", sale.amount)}")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Edit chosen
                        showEditSaleModal(sale)
                    }
                    1 -> {
                        // Delete chosen - ask for confirmation first!
                        confirmDelete(sale)
                    }
                }
            }
            .show()
    }

    private fun showEditSaleModal(saleToEdit: SaleResponse) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_sale, null)
        dialog.setContentView(view)

        // Find the views
        val tvTitle = view.findViewById<TextView>(R.id.tvSheetTitle) // Optional: change "New Sale" to "Edit Sale" if you give it an ID
        val etAmount = view.findViewById<TextInputEditText>(R.id.etSaleAmount)
        val etNotes = view.findViewById<TextInputEditText>(R.id.etSaleNotes)
        val etTags = view.findViewById<TextInputEditText>(R.id.etSaleTags)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveSale)

        // Pre-fill the data
        tvTitle.setText("Edit Sale")
        etAmount.setText(saleToEdit.amount.toString())
        etNotes.setText(saleToEdit.description ?: "")

        // Join the tags list back into a comma-separated string
        val tagsString = saleToEdit.tags?.joinToString(", ") ?: ""
        etTags.setText(tagsString)

        btnSave.text = "Update Record"

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val notes = etNotes.text.toString()
            val tagsText = etTags.text.toString()

            if (amountText.isNotBlank()) {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                viewModel.editSale(saleToEdit.id, amount, tagsList,notes )

                dialog.dismiss()
            } else {
                etAmount.error = "Amount is required"
            }
        }

        dialog.show()
    }
    private fun confirmDelete(sale: SaleResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Record?")
            .setMessage("Are you sure you want to delete this sale for ₱${String.format("%.2f", sale.amount)}? This cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                // Assume your SaleResponse has an 'id' field
                viewModel.deleteSale(sale.id)
            }
            .show()
    }
}