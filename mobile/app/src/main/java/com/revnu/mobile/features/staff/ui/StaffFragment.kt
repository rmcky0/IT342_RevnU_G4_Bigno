package com.revnu.mobile.features.staff.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.model.StaffResponse
import com.revnu.mobile.features.staff.repository.SalaryRepository
import com.revnu.mobile.features.staff.repository.StaffRepository
import com.revnu.mobile.features.staff.viewmodel.SalaryViewModel
import com.revnu.mobile.features.staff.viewmodel.StaffViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StaffFragment : Fragment(R.layout.fragment_staff) {

    private lateinit var staffViewModel: StaffViewModel
    private lateinit var salaryViewModel: SalaryViewModel

    private lateinit var staffAdapter: StaffAdapter
    private lateinit var salaryAdapter: SalaryAdapter

    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: ImageButton

    private lateinit var cardDirectorySummary: MaterialCardView
    private lateinit var tvStaffCount: TextView
    private lateinit var tvSectionLabel: TextView

    private lateinit var payrollHeader: View
    private lateinit var tvCurrentMonth: TextView
    private lateinit var tvTotalPayroll: TextView
    private lateinit var tvPayrollCount: TextView
    private lateinit var btnPrevMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton

    private lateinit var layoutEmptyDirectory: View
    private lateinit var layoutEmptyPayroll: View

    private var isDirectoryTab = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModels()
        bindViews(view)
        setupAdapters()
        setupSwipeToDelete()
        setupListeners()
        setupObservers()

        staffViewModel.loadStaff()
        salaryViewModel.loadPayroll()
    }

    private fun initViewModels() {
        val staffFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                StaffViewModel(StaffRepository(RetrofitClient.apiService)) as T
        }
        staffViewModel = ViewModelProvider(this, staffFactory)[StaffViewModel::class.java]

        val salaryFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SalaryViewModel(SalaryRepository(RetrofitClient.apiService)) as T
        }
        salaryViewModel = ViewModelProvider(this, salaryFactory)[SalaryViewModel::class.java]
    }

    private fun bindViews(view: View) {
        tabLayout = view.findViewById(R.id.tabLayoutStaff)
        recyclerView = view.findViewById(R.id.rvSharedStaff)
        fabAdd = view.findViewById(R.id.fabAddStaffRecord)

        cardDirectorySummary = view.findViewById(R.id.cardDirectorySummary)
        tvStaffCount = view.findViewById(R.id.tvStaffCount)
        tvSectionLabel = view.findViewById(R.id.tvSectionLabel)

        payrollHeader = view.findViewById(R.id.payrollHeader)
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth)
        tvTotalPayroll = view.findViewById(R.id.tvTotalPayroll)
        tvPayrollCount = view.findViewById(R.id.tvPayrollCount)
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth)
        btnNextMonth = view.findViewById(R.id.btnNextMonth)

        layoutEmptyDirectory = view.findViewById(R.id.layoutEmptyDirectory)
        layoutEmptyPayroll = view.findViewById(R.id.layoutEmptyPayroll)
    }

    private fun setupAdapters() {
        staffAdapter = StaffAdapter(
            onItemShortClick = { staff -> showStaffDetail(staff) },
            onItemLongClick = { staff -> showStaffDetail(staff) }
        )
        salaryAdapter = SalaryAdapter { salary -> showSalaryDetail(salary) }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = staffAdapter
    }

    private fun setupSwipeToDelete() {
        val redPaint = Paint().apply { color = Color.parseColor("#EF4444") }

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val pos = viewHolder.adapterPosition
                if (pos == RecyclerView.NO_POSITION) return 0
                if (!isDirectoryTab) {
                    val salary = salaryAdapter.getItemAt(pos)
                    if (salary.status != "OPEN") return 0
                }
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) return
                if (isDirectoryTab) {
                    val staff = staffAdapter.getItemAt(position)
                    staffAdapter.notifyItemChanged(position)
                    confirmDeleteStaff(staff)
                } else {
                    val salary = salaryAdapter.getItemAt(position)
                    salaryAdapter.notifyItemChanged(position)
                    confirmDeleteSalary(salary)
                }
            }

            override fun onChildDraw(c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isActive: Boolean) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = vh.itemView
                    c.drawRect(
                        itemView.right + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), redPaint
                    )
                }
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    private fun setupListeners() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isDirectoryTab = (tab?.position == 0)
                switchTab()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        fabAdd.setOnClickListener {
            if (isDirectoryTab) showAddEditStaffModal(null)
            else showAddEditSalaryModal(null)
        }

        btnPrevMonth.setOnClickListener { shiftMonth(-1) }
        btnNextMonth.setOnClickListener { shiftMonth(1) }
    }

    private fun switchTab() {
        if (isDirectoryTab) {
            recyclerView.adapter = staffAdapter
            cardDirectorySummary.visibility = View.VISIBLE
            payrollHeader.visibility = View.GONE
            tvSectionLabel.text = "Team Members"
            val list = staffViewModel.staffList.value
            updateDirectoryEmpty(list.isEmpty())
            layoutEmptyPayroll.visibility = View.GONE
        } else {
            recyclerView.adapter = salaryAdapter
            cardDirectorySummary.visibility = View.GONE
            payrollHeader.visibility = View.VISIBLE
            tvSectionLabel.text = "Salary Records"
            val list = salaryViewModel.filteredPayroll.value
            updatePayrollEmpty(list.isEmpty())
            layoutEmptyDirectory.visibility = View.GONE
        }
    }

    private fun shiftMonth(delta: Int) {
        val current = salaryViewModel.currentFilterMonth.value
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(current) ?: return
        } catch (_: Exception) { return }
        cal.add(Calendar.MONTH, delta)
        salaryViewModel.setMonthFilter(sdf.format(cal.time))
    }

    private fun updateDirectoryEmpty(isEmpty: Boolean) {
        layoutEmptyDirectory.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun updatePayrollEmpty(isEmpty: Boolean) {
        layoutEmptyPayroll.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    staffViewModel.staffList.collect { list ->
                        staffAdapter.submitList(list)
                        tvStaffCount.text = list.size.toString()
                        if (isDirectoryTab) updateDirectoryEmpty(list.isEmpty())
                    }
                }

                launch {
                    salaryViewModel.filteredPayroll.collect { list ->
                        salaryAdapter.submitList(list)
                        tvTotalPayroll.text = "₱${String.format("%,.2f", list.sumOf { it.amount })}"
                        tvPayrollCount.text = list.size.toString()
                        if (!isDirectoryTab) updatePayrollEmpty(list.isEmpty())
                    }
                }

                launch {
                    salaryViewModel.currentFilterMonth.collect { yearMonth ->
                        tvCurrentMonth.text = formatMonthLabel(yearMonth)
                    }
                }
            }
        }
    }

    private fun formatMonthLabel(yearMonth: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val out = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            out.format(sdf.parse(yearMonth) ?: return yearMonth)
        } catch (_: Exception) { yearMonth }
    }

    // ── Staff actions ──────────────────────────────────────────────────────────

    private fun showStaffDetail(staff: StaffResponse) {
        val sheet = StaffDetailBottomSheet.newInstance(staff)
        sheet.onEdit = { showAddEditStaffModal(staff) }
        sheet.show(childFragmentManager, "staff_detail")
    }

    private fun confirmDeleteStaff(staff: StaffResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove ${staff.fullname}?")
            .setMessage("Are you sure you want to remove this staff member?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Remove") { _, _ -> staffViewModel.deleteStaff(staff.id) }
            .show()
    }

    private fun showAddEditStaffModal(staffToEdit: StaffResponse?) {
        val sheet = StaffFormBottomSheet.newInstance(staffToEdit)
        sheet.onSave = { request ->
            if (staffToEdit == null) staffViewModel.addStaff(request)
            else staffViewModel.updateStaff(staffToEdit.id, request)
        }
        sheet.show(childFragmentManager, "staff_form")
    }

    // ── Salary actions ─────────────────────────────────────────────────────────

    private fun showSalaryDetail(salary: SalaryResponse) {
        val sheet = SalaryDetailBottomSheet.newInstance(salary)
        if (salary.status == "OPEN") {
            sheet.onEdit = { showAddEditSalaryModal(salary) }
        }
        sheet.show(childFragmentManager, "salary_detail")
    }

    private fun confirmDeleteSalary(salary: SalaryResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Record?")
            .setMessage("This payout of ₱${String.format("%.2f", salary.amount)} for ${salary.staffName} will be permanently deleted.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> salaryViewModel.deleteSalary(salary.id) }
            .show()
    }

    private fun showAddEditSalaryModal(salaryToEdit: SalaryResponse?) {
        val currentStaff = staffViewModel.staffList.value
        val sheet = SalaryFormBottomSheet.newInstance(currentStaff, salaryToEdit)
        sheet.onSave = { request ->
            if (salaryToEdit == null) salaryViewModel.addSalary(request)
            else salaryViewModel.updateSalary(salaryToEdit.id, request)
        }
        sheet.show(childFragmentManager, "salary_form")
    }
}
