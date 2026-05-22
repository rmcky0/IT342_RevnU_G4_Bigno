import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useExpenses } from "./hooks/useExpenses";
import { useAnalytics } from "../analytics/hooks/useAnalytics";
import { SharedKPIs } from "../../shared/components/SharedKPIs";
import { DeleteConfirmModal } from "../../shared/components/DeleteConfirmModal";
import { ViewRecordModal } from "../../shared/components/ViewRecordModal";
import { TransactionFormModal } from "../../shared/components/TransactionFormModal";
import { SharedTable } from "../../shared/components/SharedTable";
import { SharedControlBar } from "../../shared/components/SharedControlBar";
import { EODLockConfirmModal } from "../../shared/components/EODLockConfirmModal";
import { ReceiptLightbox } from "../../shared/components/ReceiptLightbox";

import {
  Receipt,
  ShieldCheck,
  Archive,
  BarChart2,
  ChevronDown,
  AlertCircle,
} from "lucide-react";

export const Expenses = () => {
  const navigate = useNavigate();
  const {
    expenses,
    paginatedExpenses,
    currentPage,
    totalPages,
    handleNextPage,
    handlePrevPage,
    error,
    success,
    loading,
    showForm,
    setShowForm,
    formData,
    setFormData,
    categories,
    receiptFile,
    setReceiptFile,
    searchTerm,
    setSearchTerm,
    editingId,
    totalExpenses,
    totalRecords,
    handleSubmit,
    handleEdit,
    handleDelete,
    resetForm,
    sortConfig,
    requestSort,
  } = useExpenses();

  const { data: analyticsData, isLocked, lockLoading, lockError, handleLockRecords } =
    useAnalytics();

  const [showKPIs, setShowKPIs] = useState(true);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [itemToView, setItemToView] = useState(null);
  const [showLockModal, setShowLockModal] = useState(false);
  const [receiptLightboxId, setReceiptLightboxId] = useState(null);

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
      className="flex flex-col flex-1 h-full max-h-full space-y-4 overflow-hidden text-[#1e1b4b]"
      onWheel={handleWheel}
    >
      {/* ── Header ─────────────────────────────────────────────────────────── */}
      <div className="shrink-0 flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-gradient-to-br from-[#8f9df7] to-[#7c83fd] rounded-lg shadow-md shadow-indigo-200">
            <Receipt className="w-5 h-5 text-white" />
          </div>
          <h2 className="text-2xl font-bold tracking-tight">Expenses</h2>
          <span className="px-2.5 py-0.5 bg-white text-[#7c83fd] text-xs font-bold rounded-md border border-gray-100 shadow-sm ml-2">
            {totalRecords} active records
          </span>
        </div>

        <div className="flex items-center gap-2">
          {isLocked ? (
            <div className="flex items-center gap-1.5 px-4 py-2 bg-emerald-500/10 text-emerald-600 rounded-lg font-bold text-sm border border-emerald-500/20 shadow-sm cursor-default select-none">
              <ShieldCheck className="w-4 h-4" /> EOD Finalized
            </div>
          ) : (
            <button
              onClick={() => setShowLockModal(true)}
              disabled={lockLoading || expenses.length === 0}
              className="flex items-center gap-1.5 px-4 py-2 bg-[#7c83fd] text-white rounded-lg font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] hover:-translate-y-0.5 transition-all disabled:opacity-60 disabled:hover:translate-y-0"
            >
              <ShieldCheck className="w-4 h-4" />
              {lockLoading ? "Locking..." : "Lock EOD"}
            </button>
          )}

          <button
            onClick={() => navigate("/archived")}
            className="flex items-center gap-1.5 px-4 py-2 bg-white text-gray-600 rounded-lg font-semibold text-sm border border-gray-200 shadow-sm hover:bg-gray-50 hover:text-[#1e1b4b] transition-all"
          >
            <Archive className="w-4 h-4" /> Archived
          </button>
        </div>
      </div>

      {/* ── Banners ────────────────────────────────────────────────────────── */}
      {lockError && (
        <div className="shrink-0 px-4 py-3 rounded-lg text-sm font-medium shadow-sm animate-in fade-in slide-in-from-top-2 flex items-center gap-2 bg-red-50 text-red-600 border border-red-200">
          <AlertCircle className="w-4 h-4 shrink-0" /> {lockError}
        </div>
      )}

      {!showKPIs && (
        <div className="shrink-0 flex justify-center animate-in fade-in slide-in-from-top-1">
          <button
            onClick={() => setShowKPIs(true)}
            className="flex items-center gap-1.5 px-4 py-1.5 bg-white rounded-full shadow-sm border border-gray-200 text-gray-400 hover:text-[#7c83fd] hover:border-indigo-100 transition-all text-xs font-bold uppercase tracking-wider group"
          >
            <BarChart2 className="w-3.5 h-3.5 group-hover:scale-110 transition-transform" />{" "}
            Show Metrics <ChevronDown className="w-3.5 h-3.5" />
          </button>
        </div>
      )}

      {/* ── Components ─────────────────────────────────────────────────────── */}
      <SharedKPIs
        showKPIs={showKPIs}
        total={analyticsData.totalExpenses}
        count={totalRecords}
        todayDisplay={todayDisplay}
        type="expenses"
      />
      <SharedControlBar
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        currentPage={currentPage}
        totalPages={totalPages}
        handlePrevPage={handlePrevPage}
        handleNextPage={handleNextPage}
        onAddClick={() => setShowForm(true)}
        isLocked={isLocked}
        type="expenses"
      />
      <SharedTable
        data={paginatedExpenses}
        loading={loading}
        searchTerm={searchTerm}
        onClearSearch={() => setSearchTerm("")}
        sortConfig={sortConfig}
        requestSort={requestSort}
        onView={setItemToView}
        onEdit={handleEdit}
        onDelete={setItemToDelete}
        onViewReceipt={setReceiptLightboxId}
        isLocked={isLocked}
        type="expenses"
      />

      {/* ── Modals ─────────────────────────────────────────────────────────── */}
      <ViewRecordModal
        isOpen={!!itemToView}
        onClose={() => setItemToView(null)}
        item={itemToView}
        title="Expense Details"
        amountColor="text-red-500"
        onViewReceipt={setReceiptLightboxId}
      />
      <DeleteConfirmModal
        item={itemToDelete}
        onClose={() => setItemToDelete(null)}
        onConfirm={async (id) => {
          await handleDelete(id);
          setItemToDelete(null);
        }}
        type="expense"
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
        error={error}
        type="Expense"
        receiptFile={receiptFile}
        setReceiptFile={setReceiptFile}
      />

      <ReceiptLightbox
        fileId={receiptLightboxId}
        onClose={() => setReceiptLightboxId(null)}
      />

      <EODLockConfirmModal
        isOpen={showLockModal}
        onClose={() => setShowLockModal(false)}
        onConfirm={confirmLock}
        lockLoading={lockLoading}
      />
    </div>
  );
};
