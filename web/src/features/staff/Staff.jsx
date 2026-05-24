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

import {
  Users,
  BarChart2,
  ChevronDown,
  Wallet,
} from "lucide-react";

const TAB_CONFIG = [
  { key: "directory", label: "Directory", icon: Users },
  { key: "payroll", label: "Payout History", icon: Wallet },
];

export const Staff = () => {
  const navigate = useNavigate();
  const {
    activeTab,
    setActiveTab,
    staffList,
    loading,
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
    allStaff,
  } = useStaff();

  const {
    salaries,
    salaryTotalPages,
    loading: salaryLoading,
    showSalaryForm,
    setShowSalaryForm,
    salaryFormData,
    setSalaryFormData,
    editingId: editingSalaryId,
    handleSalarySubmit,
    handleEditSalary,
    handleDeleteSalary,
    resetSalaryForm,
    loadSalaryHistory,
  } = useSalary();

  const [showKPIs, setShowKPIs] = useState(true);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [salaryToDelete, setSalaryToDelete] = useState(null);
  const [staffToView, setStaffToView] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const ITEMS_PER_PAGE = 8;

  const handleWheel = (e) => {
    if (e.deltaY > 10 && showKPIs) setShowKPIs(false);
  };

  const totalMonthlyPayroll = allStaff.reduce(
    (sum, s) => sum + (parseFloat(s.salaryRate) || 0),
    0,
  );

  const filteredStaff = useMemo(() => {
    if (!searchTerm) return staffList;
    const q = searchTerm.toLowerCase();
    return staffList.filter(
      (s) =>
        (s.fullname || "").toLowerCase().includes(q) ||
        (s.position || "").toLowerCase().includes(q),
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

  const recordCount =
    activeTab === "directory" ? staffList.length : salaries?.length || 0;

  return (
    <div
      className="flex flex-col flex-1 h-full max-h-full gap-3 overflow-hidden text-[#1e1b4b]"
      onWheel={handleWheel}
    >
      {/* ── Header ── */}
      <div className="shrink-0 flex items-center justify-between gap-3 flex-wrap">
        {/* Left: icon + title + badge */}
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
            <Users className="w-4.5 h-4.5 text-white" />
          </div>
          <h2 className="text-lg font-bold tracking-tight truncate">Team &amp; Payroll</h2>
          <span className="shrink-0 text-[10px] font-bold text-[#7c83fd] bg-[#f0f1ff] border border-[#d6d9ff] rounded-full px-2.5 py-0.5 uppercase tracking-wider">
            {recordCount} records
          </span>
        </div>

        {/* Right: tab switcher */}
        <div className="flex items-center shrink-0 bg-gray-100 rounded-xl p-1 gap-1">
          {TAB_CONFIG.map(({ key, label, icon: Icon }) => (
            <button
              key={key}
              onClick={() => setActiveTab(key)}
              className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-[12px] font-bold transition-all whitespace-nowrap ${
                activeTab === key
                  ? "bg-white text-[#7c83fd] shadow-sm"
                  : "text-gray-400 hover:text-gray-600"
              }`}
            >
              <Icon className="w-3.5 h-3.5" />
              {label}
            </button>
          ))}
        </div>
      </div>

      {/* ── Show-metrics toggle ── */}
      {!showKPIs && (
        <div className="shrink-0 flex justify-center animate-in fade-in slide-in-from-top-1 duration-200">
          <button
            onClick={() => setShowKPIs(true)}
            className="inline-flex items-center gap-1.5 text-[10px] font-bold uppercase tracking-widest text-gray-400 bg-white hover:text-[#7c83fd] border border-gray-200 rounded-full px-4 py-1.5 transition-colors"
          >
            <BarChart2 className="w-3 h-3" />
            Show metrics
            <ChevronDown className="w-3 h-3" />
          </button>
        </div>
      )}

      {/* ── KPIs ── */}
      <StaffKPIs
        showKPIs={showKPIs}
        staffCount={allStaff.length}
        totalMonthlyPayroll={totalMonthlyPayroll}
      />

      {/* ── Control bar ── */}
      <SharedControlBar
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        currentPage={currentPage}
        totalPages={currentTotalPages}
        handlePrevPage={() => setCurrentPage((p) => Math.max(1, p - 1))}
        handleNextPage={() => setCurrentPage((p) => Math.min(currentTotalPages, p + 1))}
        onAddClick={() =>
          activeTab === "directory" ? setShowStaffForm(true) : setShowSalaryForm(true)
        }
        type={activeTab === "directory" ? "staff" : "salary"}
      />

      {/* ── Tables ── */}
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
          onEdit={handleEditSalary}
          onDelete={(salary) => setSalaryToDelete(salary)}
        />
      )}

      {/* ── Modals ── */}
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

      <DeleteConfirmModal
        item={salaryToDelete}
        onClose={() => setSalaryToDelete(null)}
        onConfirm={async (id) => {
          await handleDeleteSalary(id);
          setSalaryToDelete(null);
        }}
        type="salary record"
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
        isEditing={!!editingSalaryId}
      />
    </div>
  );
};
