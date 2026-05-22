import React from "react";
import { X, Wallet, ShieldCheck } from "lucide-react";

export const SalaryFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  formData,
  setFormData,
  staffList,
  loading,
  error,
  success,
  isEditing = false,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl relative animate-in zoom-in-95 duration-200 border border-gray-100 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-100 bg-gray-50/50 flex items-center justify-between">
          <div>
            <h3 className="text-lg font-bold text-[#1e1b4b]">
              {isEditing ? "Edit Salary Record" : "Record Salary Payment"}
            </h3>
            <p className="text-gray-500 text-xs mt-0.5">
              {isEditing ? "Update the amount or payment date." : "Process a payroll transaction."}
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
          {(error || success) && (
            <div
              className={`mb-5 p-3 text-xs font-medium rounded-lg border flex items-start gap-2 ${error ? "bg-red-50 text-red-600 border-red-100" : "bg-green-50 text-green-600 border-green-100"}`}
            >
              {error ? (
                <X className="w-4 h-4 shrink-0 mt-0.5" />
              ) : (
                <ShieldCheck className="w-4 h-4 shrink-0 mt-0.5" />
              )}
              {error || success}
            </div>
          )}

          <form onSubmit={onSubmit} className="space-y-4">
            {!isEditing && (
              <div className="space-y-1.5">
                <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                  Select Employee
                </label>
                <select
                  value={formData.staffId}
                  onChange={(e) =>
                    setFormData({ ...formData, staffId: e.target.value })
                  }
                  className="w-full px-3 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm appearance-none"
                  required
                >
                  <option value="">Choose...</option>
                  {staffList.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.fullname || "N/A"} ({s.position || "N/A"})
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div className="grid grid-cols-2 gap-4">
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
                  Payment Date
                </label>
                <input
                  type="date"
                  value={formData.paymentDate}
                  onChange={(e) =>
                    setFormData({ ...formData, paymentDate: e.target.value })
                  }
                  className="w-full px-3 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-medium text-[#1e1b4b] transition-all text-sm outline-none shadow-sm uppercase"
                  required
                />
              </div>
            </div>

            <div className="pt-4">
              <button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-center gap-2 py-2.5 bg-emerald-500 text-white rounded-lg font-semibold text-sm shadow-md shadow-emerald-100 hover:bg-emerald-600 transition-all disabled:opacity-50 hover:-translate-y-0.5 disabled:hover:translate-y-0"
              >
                {loading ? (
                  "Processing..."
                ) : isEditing ? (
                  <>
                    <Wallet className="w-4 h-4" /> Update Record
                  </>
                ) : (
                  <>
                    <Wallet className="w-4 h-4" /> Record Payment
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
