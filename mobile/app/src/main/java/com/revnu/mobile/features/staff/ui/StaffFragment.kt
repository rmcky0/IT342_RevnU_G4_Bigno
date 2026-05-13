package com.revnu.mobile.features.staff.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.revnu.mobile.R
import com.revnu.mobile.features.staff.model.SalaryResponse
import com.revnu.mobile.features.staff.model.StaffResponse
import com.revnu.mobile.features.staff.viewmodel.StaffViewModel
import kotlinx.coroutines.launch

class StaffFragment : Fragment(R.layout.fragment_staff) {

    private lateinit var viewModel: StaffViewModel
    private lateinit var adapter: StaffAdapter
    private lateinit var rvStaff: RecyclerView
    private var isDirectoryMode = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[StaffViewModel::class.java]
        rvStaff   = view.findViewById(R.id.rvStaff)

        setupRecyclerView()
        setupObservers(view)
        setupToggle(view)
        setupAddButton(view)

        viewModel.loadData(isDirectoryMode)
    }

    // ── RecyclerView ───────────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        adapter = StaffAdapter { item ->
            if (viewModel.isLocked.value) {
                Toast.makeText(requireContext(), "Records are locked for EOD", Toast.LENGTH_SHORT).show()
                return@StaffAdapter
            }
            when {
                isDirectoryMode && item is StaffResponse   -> showStaffActionSheet(item)
                !isDirectoryMode && item is SalaryResponse -> showPayrollActionSheet(item)
            }
        }
        rvStaff.layoutManager = LinearLayoutManager(requireContext())
        rvStaff.adapter = adapter
    }

    // ── Observers ─────────────────────────────────────────────────────────────

    private fun setupObservers(view: View) {
        val emptyLayout = view.findViewById<View>(R.id.layoutEmptyStaff)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.staffList.collect { list ->
                if (isDirectoryMode) {
                    adapter.setData(list, true)
                    emptyLayout.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.payrollList.collect { list ->
                if (!isDirectoryMode) {
                    adapter.setData(list, false)
                    emptyLayout.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { msg ->
                msg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.successMessage.collect { msg ->
                msg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearSuccess()
                }
            }
        }
    }

    // ── Toggle ────────────────────────────────────────────────────────────────

    private fun setupToggle(view: View) {
        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleStaffGroup)
        val btnAdd      = view.findViewById<MaterialButton>(R.id.btnAddRecord)
        val tvTitle     = view.findViewById<TextView>(R.id.tvStaffTitle)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isDirectoryMode = (checkedId == R.id.btnDirectory)
                updateUIForMode(tvTitle, btnAdd)
                viewModel.loadData(isDirectoryMode)
            }
        }
    }

    private fun setupAddButton(view: View) {
        view.findViewById<MaterialButton>(R.id.btnAddRecord).setOnClickListener {
            if (isDirectoryMode) showAddStaffSheet()
            else showAddPayrollSheet()
        }
    }

    private fun updateUIForMode(title: TextView, btn: MaterialButton) {
        if (isDirectoryMode) {
            title.text = "Team"
            btn.text   = "+ Add Staff"
        } else {
            title.text = "Payroll"
            btn.text   = "+ Record Salary"
        }
    }

    // ── Staff sheets ──────────────────────────────────────────────────────────

    private fun showStaffActionSheet(staff: StaffResponse) {
        StaffActionBottomSheet.newInstance(staff).apply {
            onView   = { showViewStaffSheet(staff) }
            onEdit   = { showEditStaffSheet(staff) }
            onDelete = { confirmDeleteStaff(staff) }
        }.show(parentFragmentManager, "StaffAction")
    }

    private fun showViewStaffSheet(staff: StaffResponse) {
        ViewStaffBottomSheet.newInstance(staff)
            .show(parentFragmentManager, "ViewStaff")
    }

    private fun showAddStaffSheet() {
        AddEditStaffBottomSheet.newInstance(null).apply {
            onSave = { fullname, position, salaryRate ->
                viewModel.addStaff(fullname, position, salaryRate)
            }
        }.show(parentFragmentManager, "AddStaff")
    }

    private fun showEditStaffSheet(staff: StaffResponse) {
        AddEditStaffBottomSheet.newInstance(staff).apply {
            onSave = { fullname, position, salaryRate ->
                viewModel.updateStaff(staff.id, fullname, position, salaryRate)
            }
        }.show(parentFragmentManager, "EditStaff")
    }

    private fun confirmDeleteStaff(staff: StaffResponse) {
        ConfirmDeleteBottomSheet.newInstance(
            title   = "Remove Staff Member",
            message = "Remove ${staff.fullname} from the directory? This cannot be undone."
        ).apply {
            onConfirm = { viewModel.deleteStaff(staff.id) }
        }.show(parentFragmentManager, "DeleteStaff")
    }

    // ── Payroll sheets ────────────────────────────────────────────────────────

    private fun showPayrollActionSheet(salary: SalaryResponse) {
        PayrollActionBottomSheet.newInstance(salary).apply {
            onView   = { showViewPayrollSheet(salary) }
            onEdit   = { showEditPayrollSheet(salary) }
            onDelete = { confirmDeletePayroll(salary) }
        }.show(parentFragmentManager, "PayrollAction")
    }

    private fun showViewPayrollSheet(salary: SalaryResponse) {
        ViewPayrollBottomSheet.newInstance(salary)
            .show(parentFragmentManager, "ViewPayroll")
    }

    private fun showAddPayrollSheet() {
        AddEditPayrollBottomSheet.newInstance(null, viewModel.staffList.value).apply {
            onSave = { staffId, amount, paymentDate ->
                viewModel.addSalary(staffId, amount, paymentDate)
            }
        }.show(parentFragmentManager, "AddPayroll")
    }

    private fun showEditPayrollSheet(salary: SalaryResponse) {
        AddEditPayrollBottomSheet.newInstance(salary, viewModel.staffList.value).apply {
            onSave = { staffId, amount, paymentDate ->
                viewModel.updateSalary(salary.id, staffId, amount, paymentDate)
            }
        }.show(parentFragmentManager, "EditPayroll")
    }

    private fun confirmDeletePayroll(salary: SalaryResponse) {
        ConfirmDeleteBottomSheet.newInstance(
            title   = "Delete Salary Record",
            message = "Delete this salary record for ${salary.employeeName}? This cannot be undone."
        ).apply {
            onConfirm = { viewModel.deleteSalary(salary.id) }
        }.show(parentFragmentManager, "DeletePayroll")
    }
}