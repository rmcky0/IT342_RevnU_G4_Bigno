import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSales } from "./hooks/useSales";
import { useAnalytics } from "../analytics/hooks/useAnalytics";

import { SharedKPIs } from "../../shared/components/SharedKPIs";
import { DeleteConfirmModal } from "../../shared/components/DeleteConfirmModal";
import { ViewRecordModal } from "../../shared/components/ViewRecordModal";
import { TransactionFormModal } from "../../shared/components/TransactionFormModal";
import { SharedTable } from "../../shared/components/SharedTable";
import { SharedControlBar } from "../../shared/components/SharedControlBar";
import { EODLockConfirmModal } from "../../shared/components/EODLockConfirmModal";

import {
  ShieldCheck,
  ShoppingCart,
  BarChart2,
  ChevronDown,
  Archive,
} from "lucide-react";

export const Sales = () => {
  const navigate = useNavigate();
  const {
    sales,
    paginatedSales,
    currentPage,
    totalPages,
    handleNextPage,
    handlePrevPage,
    loading,
    showForm,
    setShowForm,
    formData,
    setFormData,
    categories,
    searchTerm,
    setSearchTerm,
    editingId,
    totalSales,
    totalRecords,
    handleSubmit,
    handleEdit,
    handleDelete,
    resetForm,
    sortConfig,
    requestSort,
  } = useSales();

  const {
    data: analyticsData,
    isLocked,
    lockLoading,
    handleLockRecords,
    openSalesTotal,
    openExpensesTotal,
    openTotalSalaries,
    openNetProfit,
    openSaleRecordsCount,
    openExpenseRecordsCount,
  } = useAnalytics();

  const [showKPIs, setShowKPIs] = useState(true);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [itemToView, setItemToView] = useState(null);
  const [showLockModal, setShowLockModal] = useState(false);

  const handleWheel = (e) => {
    if (e.deltaY > 10 && showKPIs) setShowKPIs(false);
  };

  const confirmLock = async () => {
    await handleLockRecords();
    setShowLockModal(false);
  };

  const todayDisplay = new Date().toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });

  return (
    <div
      className="flex flex-col flex-1 h-full max-h-full gap-3 overflow-hidden text-[#1e1b4b]"
      onWheel={handleWheel}
    >
      {/* ── Header ── */}
      <div className="shrink-0 flex items-center justify-between gap-3 flex-wrap">
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-9 h-9 bg-[#7c83fd] rounded-xl flex items-center justify-center shrink-0">
            <ShoppingCart className="w-4.5 h-4.5 text-white" />
          </div>
          <h2 className="text-lg font-bold tracking-tight truncate">Sales</h2>
          <span className="shrink-0 text-[10px] font-bold text-[#7c83fd] bg-[#f0f1ff] border border-[#d6d9ff] rounded-full px-2.5 py-0.5 uppercase tracking-wider">
            {totalRecords} records
          </span>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          {isLocked ? (
            <span className="inline-flex items-center gap-1.5 text-[12px] font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 rounded-xl px-3.5 py-2 select-none">
              <ShieldCheck className="w-3.5 h-3.5" />
              EOD Finalized
            </span>
          ) : (
            <button
              onClick={() => setShowLockModal(true)}
              disabled={lockLoading || sales.length === 0}
              title={sales.length === 0 ? "Add at least one sale to lock EOD" : undefined}
              className="inline-flex items-center gap-1.5 text-[13px] font-bold text-white bg-[#7c83fd] hover:bg-[#6b72f5] rounded-xl px-3.5 py-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ShieldCheck className="w-3.5 h-3.5" />
              {lockLoading ? "Locking…" : "Lock EOD"}
            </button>
          )}

          <button
            onClick={() => navigate("/archived")}
            className="inline-flex items-center gap-1.5 text-[13px] font-bold text-gray-500 bg-white hover:bg-gray-50 hover:text-[#1e1b4b] border border-gray-200 rounded-xl px-3.5 py-2 transition-colors"
          >
            <Archive className="w-3.5 h-3.5" />
            Archived
          </button>
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

      {/* ── KPI cards ── */}
      <SharedKPIs
        showKPIs={showKPIs}
        total={totalSales}
        count={totalRecords}
        todayDisplay={todayDisplay}
        type="sales"
      />

      {/* ── Control bar ── */}
      <SharedControlBar
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        currentPage={currentPage}
        totalPages={totalPages}
        handlePrevPage={handlePrevPage}
        handleNextPage={handleNextPage}
        onAddClick={() => setShowForm(true)}
        isLocked={isLocked}
        type="sales"
      />

      {/* ── Table ── */}
      <SharedTable
        data={paginatedSales}
        loading={loading}
        searchTerm={searchTerm}
        onClearSearch={() => setSearchTerm("")}
        sortConfig={sortConfig}
        requestSort={requestSort}
        onView={setItemToView}
        onEdit={handleEdit}
        onDelete={setItemToDelete}
        isLocked={isLocked}
        type="sales"
      />

      {/* ── Modals ── */}
      <DeleteConfirmModal
        item={itemToDelete}
        onClose={() => setItemToDelete(null)}
        onConfirm={async (id) => {
          await handleDelete(id);
          setItemToDelete(null);
        }}
        type="sale"
      />

      <ViewRecordModal
        isOpen={!!itemToView}
        onClose={() => setItemToView(null)}
        item={itemToView}
        title="Sale record details"
        amountColor="text-[#7c83fd]"
      />

      <TransactionFormModal
        isOpen={showForm}
        onClose={resetForm}
        onSubmit={handleSubmit}
        formData={formData}
        setFormData={setFormData}
        categories={categories}
        isEditing={!!editingId}
        loading={loading}
        type="Sale"
      />

      <EODLockConfirmModal
        isOpen={showLockModal}
        onClose={() => setShowLockModal(false)}
        onConfirm={confirmLock}
        lockLoading={lockLoading}
        openSalesTotal={openSalesTotal}
        openExpensesTotal={openExpensesTotal}
        openTotalSalaries={openTotalSalaries}
        openNetProfit={openNetProfit}
        openSaleRecordsCount={openSaleRecordsCount}
        openExpenseRecordsCount={openExpenseRecordsCount}
      />
    </div>
  );
};
