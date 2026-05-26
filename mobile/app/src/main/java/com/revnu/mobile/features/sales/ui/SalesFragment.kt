package com.revnu.mobile.features.sales.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import com.revnu.mobile.features.sales.model.SaleResponse
import com.revnu.mobile.features.sales.repository.SalesRepository
import com.revnu.mobile.features.sales.viewmodel.SalesViewModel
import kotlinx.coroutines.launch

class SalesFragment : Fragment(R.layout.fragment_sales) {

    private lateinit var viewModel: SalesViewModel
    private lateinit var adapter: SalesAdapter

    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var rvSales: RecyclerView
    private lateinit var tvTotalRevenue: TextView

    private val editRecordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) viewModel.forceRefresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = SalesRepository(RetrofitClient.apiService)
                @Suppress("UNCHECKED_CAST")
                return SalesViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SalesViewModel::class.java]

        bindViews(view)
        setupRecyclerView()
        setupListeners(view)
        setupObservers(view)

        viewModel.loadSales()
    }

    private fun bindViews(view: View) {
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        rvSales = view.findViewById(R.id.rvSales)
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue)
    }

    private fun setupListeners(view: View) {
        view.findViewById<MaterialButton>(R.id.btnLockRecords).setOnClickListener {
            showStrictLockConfirmation()
        }
    }

    private fun setupRecyclerView() {
        adapter = SalesAdapter { clickedSale ->
            if (viewModel.isEodLocked.value) {
                Toast.makeText(requireContext(), "Cannot modify records after EOD is locked.", Toast.LENGTH_SHORT).show()
                return@SalesAdapter
            }
            showSaleDetail(clickedSale)
        }
        rvSales.layoutManager = LinearLayoutManager(requireContext())
        rvSales.adapter = adapter

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private val paint = Paint().apply { color = Color.parseColor("#EF4444") }

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if (viewModel.isEodLocked.value) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val sale = adapter.getItemAt(position)
                adapter.notifyItemChanged(position)
                confirmDelete(sale)
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
        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvSales)
    }

    private fun setupObservers(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sales.collect { salesList ->
                val activeSales = salesList.filter { it.status == "OPEN" }
                adapter.updateData(activeSales)
                layoutEmptyState.visibility = if (activeSales.isEmpty()) View.VISIBLE else View.GONE
                rvSales.visibility = if (activeSales.isEmpty()) View.GONE else View.VISIBLE
                tvTotalRevenue.text = "₱${String.format("%.2f", activeSales.sumOf { it.amount })}"
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteError.collect { error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    viewModel.clearDeleteError()
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
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSaleDetail(sale: SaleResponse) {
        val sheet = SaleDetailBottomSheet.newInstance(sale)
        sheet.onEdit = { showEditSaleModal(sale) }
        sheet.show(childFragmentManager, "sale_detail")
    }

    private fun showEditSaleModal(saleToEdit: SaleResponse) {
        val intent = Intent(requireContext(), AddRecordActivity::class.java).apply {
            putExtra(AddRecordActivity.EXTRA_IS_EDIT_MODE, true)
            putExtra(AddRecordActivity.EXTRA_RECORD_ID, saleToEdit.id)
            putExtra(AddRecordActivity.EXTRA_RECORD_TYPE, "sale")
            putExtra(AddRecordActivity.EXTRA_AMOUNT, saleToEdit.amount)
            putExtra(AddRecordActivity.EXTRA_DESCRIPTION, saleToEdit.notes ?: "")
            putExtra(AddRecordActivity.EXTRA_CATEGORY_ID, saleToEdit.categoryId)
            putExtra(AddRecordActivity.EXTRA_CATEGORY_NAME, saleToEdit.categoryName ?: "")
        }
        editRecordLauncher.launch(intent)
    }

    private fun confirmDelete(sale: SaleResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Record?")
            .setMessage("Are you sure you want to delete this sale for ₱${String.format("%.2f", sale.amount)}? This cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteSale(sale.id) }
            .show()
    }

    fun refresh() {
        if (::viewModel.isInitialized) viewModel.forceRefresh()
    }
}
