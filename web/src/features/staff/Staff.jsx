import React, { useState, useMemo, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useStaff } from "./hooks/useStaff";
import { useSalary } from "./hooks/useSalary";
import { StaffKPIs } from "./components/StaffKPIs";
import { StaffTable } from "./components/StaffTable";
import { PayrollTable } from "./components/PayrollTable";
import { StaffViewModal } from "./components/StaffViewModal";
import { DeleteConfirmModal } from "../../shared/components/DeleteConfirmModal";
import { StaffFormModal } from "./components/StaffFormModal";
import { SalaryFormModal } from "./components/SalaryFormModal";
import { SharedControlBar } from "../../shared/components/SharedControlBar";

import { Users, ShieldCheck, X, ChevronDown, BarChart2 } from "lucide-react";

export const Staff = () => {
  const navigate = useNavigate();
  const {
    activeTab,
    setActiveTab,
    staffList,
    loading,
    error,
    success,
    showStaffForm,
    setShowStaffForm,
    staffFormData,
    setStaffFormData,
    searchTerm,
    setSearchTerm,
    editingId,
    handleStaffSubmit,
    handleEditStaff,
    handleDeleteStaff,
    resetStaffForm,
  } = useStaff();

  const {
    salaries,
    salaryTotalPages,
    loading: salaryLoading,
    error: salaryError,
    success: salarySuccess,
    showSalaryForm,
    setShowSalaryForm,
    salaryFormData,
    setSalaryFormData,
    handleSalarySubmit,
    resetSalaryForm,
    loadSalaryHistory,
  } = useSalary();

  const [showKPIs, setShowKPIs] = useState(true);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [staffToView, setStaffToView] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const ITEMS_PER_PAGE = 8;

  const handleWheel = (e) => {
    if (e.deltaY > 10 && showKPIs) setShowKPIs(false);
  };

  const totalMonthlyPayroll = staffList.reduce(
    (sum, s) => sum + (parseFloat(s.salaryRate) || 0),
    0,
  );

  const filteredStaff = useMemo(() => {
    if (!searchTerm) return staffList;
    const lowerSearch = searchTerm.toLowerCase();
    return staffList.filter(
      (s) =>
        (s.fullname || "").toLowerCase().includes(lowerSearch) ||
        (s.position || "").toLowerCase().includes(lowerSearch),
    );
  }, [staffList, searchTerm]);

  const totalStaffPages = Math.ceil(filteredStaff.length / ITEMS_PER_PAGE) || 1;
  const paginatedStaff = useMemo(() => {
    const start = (currentPage - 1) * ITEMS_PER_PAGE;
    return filteredStaff.slice(start, start + ITEMS_PER_PAGE);
  }, [filteredStaff, currentPage]);

  const currentTotalPages =
    activeTab === "directory" ? totalStaffPages : salaryTotalPages;

  useEffect(() => {
    setCurrentPage(1);
  }, [activeTab, searchTerm]);

  useEffect(() => {
    if (activeTab === "payroll") {
      loadSalaryHistory(currentPage - 1, ITEMS_PER_PAGE, false);
    }
  }, [currentPage, activeTab]);

  return (
    <div
      className="flex flex-col flex-1 h-full max-h-full space-y-4 overflow-hidden text-[#1e1b4b]"
      onWheel={handleWheel}
    >
      {/* ── Header ─────────────────────────────────────────────────────────── */}
      <div className="shrink-0 flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-gradient-to-br from-[#8f9df7] to-[#7c83fd] rounded-lg shadow-md shadow-indigo-200">
            <Users className="w-5 h-5 text-white" />
          </div>
          <h2 className="text-2xl font-bold tracking-tight">Team & Payroll</h2>
          <span className="px-2.5 py-0.5 bg-white text-[#7c83fd] text-xs font-bold rounded-md border border-gray-100 shadow-sm ml-2">
            {activeTab === "directory"
              ? staffList.length
              : salaries?.length || 0}{" "}
            records
          </span>
        </div>

        {/* Tab Switcher */}
        <div className="flex items-center bg-white p-1 rounded-xl shadow-sm border border-gray-100">
          <button
            onClick={() => setActiveTab("directory")}
            className={`px-5 py-1.5 rounded-lg font-semibold text-sm transition-all ${activeTab === "directory" ? "bg-indigo-50 text-[#7c83fd] shadow-sm" : "text-gray-500 hover:text-gray-700"}`}
          >
            Directory
          </button>
          <button
            onClick={() => setActiveTab("payroll")}
            className={`px-5 py-1.5 rounded-lg font-semibold text-sm transition-all ${activeTab === "payroll" ? "bg-indigo-50 text-[#7c83fd] shadow-sm" : "text-gray-500 hover:text-gray-700"}`}
          >
            Payroll History
          </button>
        </div>
      </div>

      {/* ── Notifications ──────────────────────────────────────────────────── */}
      {(success || error) && (
        <div
          className={`shrink-0 px-4 py-3 rounded-lg text-sm font-medium shadow-sm animate-in fade-in flex items-center gap-2 ${success ? "bg-green-50 text-green-700 border-green-200" : "bg-red-50 text-red-600 border-red-200"}`}
        >
          {success ? (
            <ShieldCheck className="w-4 h-4" />
          ) : (
            <X className="w-4 h-4" />
          )}
          {success || error}
        </div>
      )}

      {!showKPIs && (
        <div className="shrink-0 flex justify-center animate-in fade-in slide-in-from-top-1">
          <button
            onClick={() => setShowKPIs(true)}
            className="flex items-center gap-1.5 px-4 py-1.5 bg-white rounded-full shadow-sm border border-gray-200 text-gray-400 hover:text-[#7c83fd] transition-all text-xs font-bold uppercase tracking-wider group"
          >
            <BarChart2 className="w-3.5 h-3.5 group-hover:scale-110 transition-transform" />{" "}
            Show Metrics <ChevronDown className="w-3.5 h-3.5" />
          </button>
        </div>
      )}

      {/* ── Extracted Components ───────────────────────────────────────────── */}
      <StaffKPIs
        showKPIs={showKPIs}
        staffCount={staffList.length}
        totalMonthlyPayroll={totalMonthlyPayroll}
      />

      <SharedControlBar
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        currentPage={currentPage}
        totalPages={currentTotalPages}
        handlePrevPage={() => setCurrentPage((p) => Math.max(1, p - 1))}
        handleNextPage={() =>
          setCurrentPage((p) => Math.min(currentTotalPages, p + 1))
        }
        onAddClick={() =>
          activeTab === "directory"
            ? setShowStaffForm(true)
            : setShowSalaryForm(true)
        }
        type={activeTab === "directory" ? "staff" : "salary"}
      />

      {/* ── Conditional Table Rendering ────────────────────────────────────── */}
      <div className="flex-1 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0 overflow-hidden relative">
        <div className="overflow-x-auto flex-1">
          {activeTab === "directory" ? (
            <StaffTable
              data={paginatedStaff}
              loading={loading}
              searchTerm={searchTerm}
              onView={(staff) => setStaffToView(staff)}
              onEdit={(staff) => handleEditStaff(staff)}
              onDelete={(staff) => setItemToDelete(staff)}
              navigate={navigate}
            />
          ) : (
            <PayrollTable
              data={salaries}
              loading={salaryLoading}
              searchTerm={searchTerm}
            />
          )}
        </div>
      </div>

      <StaffViewModal
        staffToView={staffToView}
        onClose={() => setStaffToView(null)}
        salaries={salaries}
        navigate={navigate}
      />

      <DeleteConfirmModal
        item={itemToDelete}
        onClose={() => setItemToDelete(null)}
        onConfirm={async (id) => {
          await handleDeleteStaff(id);
          setItemToDelete(null);
        }}
        type="staff member"
      />

      <StaffFormModal
        isOpen={showStaffForm}
        onClose={resetStaffForm}
        onSubmit={handleStaffSubmit}
        formData={staffFormData}
        setFormData={setStaffFormData}
        isEditing={!!editingId}
        loading={loading}
      />

      <SalaryFormModal
        isOpen={showSalaryForm}
        onClose={resetSalaryForm}
        onSubmit={handleSalarySubmit}
        formData={salaryFormData}
        setFormData={setSalaryFormData}
        staffList={staffList}
        loading={salaryLoading}
        error={salaryError}
        success={salarySuccess}
      />
    </div>
  );
};
