package com.revnu.mobile.features.analytics.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.analytics.model.OpenAnalyticsData
import com.revnu.mobile.features.analytics.repository.AnalyticsRepository
import com.revnu.mobile.features.analytics.viewmodel.AnalyticsViewModel
import com.revnu.mobile.features.analytics.viewmodel.AnalyticsViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    companion object {
        private const val TAG = "AnalyticsFragment"
    }

    private lateinit var viewModel: AnalyticsViewModel

    // ── View refs ──────────────────────────────────────────────────────────────
    private lateinit var tvToday: TextView
    private lateinit var tvNetProfit: TextView
    private lateinit var tvNetProfitSub: TextView
    private lateinit var tvTotalSales: TextView
    private lateinit var tvSaleRecords: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvExpenseRecords: TextView
    private lateinit var tvOpsExpense: TextView
    private lateinit var tvPayrollExpense: TextView
    private lateinit var cardEodStatus: MaterialCardView
    private lateinit var ivLockIcon: ImageView
    private lateinit var tvEodStatus: TextView
    private lateinit var tvEodSub: TextView
    private lateinit var btnLockEod: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = AnalyticsViewModelFactory(
            AnalyticsRepository(RetrofitClient.apiService)
        )

        viewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        bindViews(view)
        setDateHeader()
        setupListeners()
        observeViewModel()
    }

    // ── Bind ───────────────────────────────────────────────────────────────────

    private fun bindViews(view: View) {
        tvToday          = view.findViewById(R.id.tvToday)
        tvNetProfit      = view.findViewById(R.id.tvNetProfit)
        tvNetProfitSub   = view.findViewById(R.id.tvNetProfitSub)
        tvTotalSales     = view.findViewById(R.id.tvTotalSales)
        tvSaleRecords    = view.findViewById(R.id.tvSaleRecords)
        tvTotalExpenses  = view.findViewById(R.id.tvTotalExpenses)
        tvExpenseRecords = view.findViewById(R.id.tvExpenseRecords)
        tvOpsExpense     = view.findViewById(R.id.tvOpsExpense)
        tvPayrollExpense = view.findViewById(R.id.tvPayrollExpense)
        cardEodStatus    = view.findViewById(R.id.cardEodStatus)
        ivLockIcon       = view.findViewById(R.id.ivLockIcon)
        tvEodStatus      = view.findViewById(R.id.tvEodStatus)
        tvEodSub         = view.findViewById(R.id.tvEodSub)
        btnLockEod       = view.findViewById(R.id.btnLockEod)
    }

    private fun setupListeners() {
        btnLockEod.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Lock End of Day?")
                .setMessage("Are you absolutely sure you want to lock today's records?\n\nYou will NOT be able to add or edit any sales or expenses for today once locked.")
                .setCancelable(false)
                .setPositiveButton("Yes, Lock EOD") { dialog, _ ->
                    viewModel.lockEod()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun setDateHeader() {
        tvToday.text = "Today, " +
                SimpleDateFormat("EEE dd MMMM", Locale.getDefault()).format(Date())
    }

    // ── Observe ────────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle stops collection when fragment goes to background
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AnalyticsViewModel.UiState.Loading -> showLoading()
                        is AnalyticsViewModel.UiState.Success -> {
                            showContent()
                            updateAnalytics(state.analytics)
                        }
                        is AnalyticsViewModel.UiState.Error -> {
                            showError(state.message)
                            Log.e(TAG, "UI error: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    // ── State helpers ──────────────────────────────────────────────────────────

    private fun showLoading() {
        // Optional: show a shimmer or progress indicator
        // For now just blank the numbers
        tvNetProfit.text      = "..."
        tvTotalSales.text     = "..."
        tvTotalExpenses.text  = "..."
    }

    private fun showContent() { /* views are already visible in the layout */ }

    private fun showError(message: String) {
        tvNetProfit.text    = "Error"
        tvNetProfitSub.text = message
        tvTotalSales.text   = "—"
        tvTotalExpenses.text = "—"
    }

    // ── Populate views ─────────────────────────────────────────────────────────

    private fun updateAnalytics(data: OpenAnalyticsData) {
        // Net profit (hero card)
        tvNetProfit.text = formatPeso(data.netProfit)
        // Dim color when a loss
        tvNetProfit.setTextColor(
            if (data.netProfit >= 0) Color.WHITE else Color.parseColor("#FECACA")
        )

        // Sales
        tvTotalSales.text  = formatPeso(data.totalSales)
        tvSaleRecords.text = "${data.saleRecordsCount} transaction${plural(data.saleRecordsCount)}"

        // Expenses (total = ops + payroll)
        tvTotalExpenses.text  = formatPeso(data.totalExpenses)
        tvExpenseRecords.text = "${data.expenseRecordsCount} entr${if (data.expenseRecordsCount != 1L) "ies" else "y"}"

        // Breakdown
        val opsExpense = data.totalExpenses - data.totalSalaries
        tvOpsExpense.text     = formatPeso(opsExpense)
        tvPayrollExpense.text = formatPeso(data.totalSalaries)

        // EOD status
        updateEodCard(data.isClosed)
    }
    private fun updateEodCard(isLocked: Boolean) {
        if (isLocked) {
            cardEodStatus.setCardBackgroundColor(Color.parseColor("#DCFCE7"))
            ivLockIcon.setColorFilter(Color.parseColor("#16A34A"))
            tvEodStatus.text = "EOD LOCKED"
            tvEodStatus.setTextColor(Color.parseColor("#16A34A"))
            tvEodSub.text = "Today's records are finalized"
            tvEodSub.setTextColor(Color.parseColor("#86EFAC"))
            btnLockEod.visibility = View.GONE
        } else {
            cardEodStatus.setCardBackgroundColor(Color.parseColor("#FEE2E2"))
            ivLockIcon.setColorFilter(Color.parseColor("#EF4444"))
            tvEodStatus.text = "EOD NOT LOCKED"
            tvEodStatus.setTextColor(Color.parseColor("#EF4444"))
            tvEodSub.text = "Today's records are still open"
            tvEodSub.setTextColor(Color.parseColor("#FCA5A5"))
            btnLockEod.visibility = View.VISIBLE
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun formatPeso(amount: Double) = "₱${String.format("%,.2f", amount)}"
    private fun plural(n: Long)           = if (n != 1L) "s" else ""
}