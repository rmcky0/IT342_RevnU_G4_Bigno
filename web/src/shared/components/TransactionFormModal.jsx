import React from "react";
import { X, Plus, Edit2 } from "lucide-react";

export const TransactionFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  formData,
  setFormData,
  categories = [],
  isEditing,
  loading,
  error,
  type = "Transaction",
  receiptFile,
  setReceiptFile,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl relative animate-in zoom-in-95 duration-200 border border-gray-100 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-100 bg-gray-50/50 flex items-center justify-between">
          <div>
            <h3 className="text-lg font-bold text-[#1e1b4b]">
              {isEditing ? `Edit ${type}` : `New ${type}`}
            </h3>
            <p className="text-gray-500 text-xs mt-0.5">
              Enter the {type.toLowerCase()} details below
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6">
          {error && (
            <div className="mb-5 p-3 bg-red-50 text-red-600 text-xs font-medium rounded-lg border border-red-100 flex items-start gap-2">
              <X className="w-4 h-4 shrink-0 mt-0.5" />
              {error}
            </div>
          )}

          <form onSubmit={onSubmit} className="space-y-5">
            <div className="space-y-1.5">
              <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                Amount
              </label>
              <div className="relative group">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 font-medium group-focus-within:text-[#7c83fd] transition-colors">
                  ₱
                </span>
                <input
                  type="number"
                  step="0.01"
                  value={formData.amount}
                  onChange={(e) =>
                    setFormData({ ...formData, amount: e.target.value })
                  }
                  className="w-full pl-8 pr-3 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
                  placeholder="0.00"
                  required
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                Category
              </label>
              <select
                value={formData.categoryId}
                onChange={(e) =>
                  setFormData({ ...formData, categoryId: e.target.value })
                }
                className="w-full px-3 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 text-sm text-[#1e1b4b] transition-all outline-none shadow-sm"
                required
              >
                <option value="">Select a category...</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="space-y-1.5">
              <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                Notes
              </label>
              <textarea
                value={formData.notes}
                onChange={(e) =>
                  setFormData({ ...formData, notes: e.target.value })
                }
                className="w-full px-3 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 text-sm text-[#1e1b4b] transition-all outline-none shadow-sm h-20 resize-none"
                placeholder="Optional details"
              />
            </div>

            {/* Conditionally render Receipt Upload for Expenses */}
            {setReceiptFile && (
              <div className="space-y-1.5">
                <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                  Receipt Attachment
                </label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => setReceiptFile(e.target.files[0])}
                  className="w-full text-xs text-gray-500 file:mr-4 file:py-2.5 file:px-4 file:rounded-md file:border-0 file:text-xs file:font-semibold file:bg-indigo-50 file:text-[#7c83fd] hover:file:bg-indigo-100 cursor-pointer border border-gray-200 rounded-lg bg-gray-50/50 shadow-sm"
                />
                {receiptFile && (
                  <p className="text-[11px] text-[#7c83fd] font-semibold mt-1 flex items-center gap-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-[#7c83fd]" />
                    {receiptFile.name}
                  </p>
                )}
              </div>
            )}

            <div className="pt-4">
              <button
                type="submit"
                disabled={loading || !formData.amount || !formData.categoryId}
                className="w-full flex items-center justify-center gap-2 py-2.5 bg-[#7c83fd] text-white rounded-lg font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] transition-all disabled:opacity-50 hover:-translate-y-0.5 disabled:hover:translate-y-0"
              >
                {loading ? (
                  "Saving..."
                ) : isEditing ? (
                  <>
                    <Edit2 className="w-4 h-4" /> Update Record
                  </>
                ) : (
                  <>
                    <Plus className="w-4 h-4" /> Save {type}
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
