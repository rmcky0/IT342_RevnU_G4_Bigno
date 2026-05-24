import React from "react";
import { X, Plus, Edit2, ShoppingCart, Receipt } from "lucide-react";

const inputCls =
  "w-full px-3.5 py-2.5 bg-gray-50 rounded-xl border border-gray-200 focus:border-[#7c83fd] focus:bg-white focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none placeholder:text-gray-300 placeholder:font-normal";

const Field = ({ label, children }) => (
  <div className="space-y-1.5">
    <label className="block text-[10px] font-bold text-gray-400 uppercase tracking-widest">
      {label}
    </label>
    {children}
  </div>
);

export const TransactionFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  formData,
  setFormData,
  categories = [],
  isEditing,
  loading,
  type = "Transaction",
  receiptFile,
  setReceiptFile,
}) => {
  if (!isOpen) return null;

  const isExpense = type === "Expense";
  const Icon = isExpense ? Receipt : ShoppingCart;
  const iconBg = isExpense ? "bg-red-50" : "bg-[#7c83fd]/10";
  const iconColor = isExpense ? "text-red-500" : "text-[#7c83fd]";
  const submitBg = isExpense
    ? "bg-red-500 hover:bg-red-600 shadow-red-100"
    : "bg-[#7c83fd] hover:bg-[#6b72f5] shadow-indigo-100";

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/40 backdrop-blur-[3px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-md w-full border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-200 shadow-2xl">

        {/* Header */}
        <div className="px-6 py-5 border-b border-gray-100 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className={`w-9 h-9 rounded-xl ${iconBg} flex items-center justify-center shrink-0`}>
              <Icon className={`w-4.5 h-4.5 ${iconColor}`} />
            </div>
            <div>
              <h3 className="text-base font-bold text-[#1e1b4b] leading-tight">
                {isEditing ? `Edit ${type}` : `New ${type}`}
              </h3>
              <p className="text-[11px] text-gray-400 mt-0.5">
                Enter the {type.toLowerCase()} details below
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-xl transition-colors"
            aria-label="Close"
          >
            <X className="w-4.5 h-4.5" />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={onSubmit} className="p-6 space-y-4">
          <Field label="Amount">
            <div className="relative group">
              <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400 text-sm font-medium group-focus-within:text-[#7c83fd] transition-colors select-none">
                ₱
              </span>
              <input
                type="number"
                step="0.01"
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                className={`${inputCls} pl-8`}
                placeholder="0.00"
                required
              />
            </div>
          </Field>

          <Field label="Category">
            <select
              value={formData.categoryId}
              onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
              className={`${inputCls} appearance-none`}
              required
            >
              <option value="">Select a category…</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
          </Field>

          <Field label="Notes">
            <textarea
              value={formData.notes}
              onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
              className={`${inputCls} resize-none h-[72px]`}
              placeholder="Optional details"
            />
          </Field>

          {/* Receipt upload — expenses only */}
          {setReceiptFile && (
            <Field label="Receipt attachment">
              <input
                type="file"
                accept="image/*"
                onChange={(e) => setReceiptFile(e.target.files[0])}
                className="w-full text-[12px] text-gray-500 bg-gray-50 border border-gray-200 rounded-xl cursor-pointer file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-[11px] file:font-bold file:bg-[#eef0ff] file:text-[#7c83fd] hover:file:bg-[#e5e7ff] transition-colors"
              />
              {receiptFile && (
                <p className="mt-1.5 flex items-center gap-1.5 text-[11px] text-[#7c83fd] font-medium">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#7c83fd] shrink-0" />
                  {receiptFile.name}
                </p>
              )}
            </Field>
          )}

          <div className="flex gap-2 pt-1">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 rounded-xl border border-gray-200 text-gray-500 text-sm font-semibold hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading || !formData.amount || !formData.categoryId}
              className={`flex-1 flex items-center justify-center gap-2 py-2.5 text-white rounded-xl text-sm font-semibold shadow-md active:scale-[0.98] transition-all disabled:opacity-50 disabled:cursor-not-allowed ${submitBg}`}
            >
              {loading ? (
                <>
                  <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Saving…
                </>
              ) : isEditing ? (
                <><Edit2 className="w-3.5 h-3.5" /> Update record</>
              ) : (
                <><Plus className="w-3.5 h-3.5" /> Save {type.toLowerCase()}</>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
