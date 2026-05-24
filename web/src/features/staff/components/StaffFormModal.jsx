import React from "react";
import { X, Plus, Edit2, Users } from "lucide-react";

const Field = ({ label, children }) => (
  <div className="space-y-1.5">
    <label className="block text-[10px] font-bold text-gray-400 uppercase tracking-widest">
      {label}
    </label>
    {children}
  </div>
);

const inputCls =
  "w-full px-3.5 py-2.5 bg-gray-50 rounded-xl border border-gray-200 focus:border-[#7c83fd] focus:bg-white focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none placeholder:text-gray-300 placeholder:font-normal";

export const StaffFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  formData,
  setFormData,
  isEditing,
  loading,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/40 backdrop-blur-[3px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-200">

        {/* Header */}
        <div className="px-6 py-5 border-b border-gray-100 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center">
              <Users className="w-4.5 h-4.5 text-[#7c83fd]" />
            </div>
            <div>
              <h3 className="text-base font-bold text-[#1e1b4b] leading-tight">
                {isEditing ? "Edit Staff Profile" : "Add New Staff"}
              </h3>
              <p className="text-[11px] text-gray-400 mt-0.5">
                {isEditing ? "Update employee information." : "Enter employee details below."}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-xl transition-colors"
          >
            <X className="w-4.5 h-4.5" />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={onSubmit} className="p-6 space-y-4">
          <Field label="Full Name">
            <input
              type="text"
              value={formData.fullname}
              onChange={(e) => setFormData({ ...formData, fullname: e.target.value })}
              className={inputCls}
              placeholder="e.g. Juan Dela Cruz"
              required
            />
          </Field>

          <div className="grid grid-cols-2 gap-3">
            <Field label="Position">
              <input
                type="text"
                value={formData.position}
                onChange={(e) => setFormData({ ...formData, position: e.target.value })}
                className={inputCls}
                placeholder="e.g. Cashier"
                required
              />
            </Field>

            <Field label="Salary Rate">
              <div className="relative group">
                <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400 text-sm font-medium group-focus-within:text-[#7c83fd] transition-colors select-none">
                  ₱
                </span>
                <input
                  type="number"
                  step="0.01"
                  value={formData.salaryRate}
                  onChange={(e) => setFormData({ ...formData, salaryRate: e.target.value })}
                  className={`${inputCls} pl-8`}
                  placeholder="0.00"
                  required
                />
              </div>
            </Field>
          </div>

          <div className="flex gap-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 rounded-xl border border-gray-200 text-gray-500 text-sm font-semibold hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 bg-[#7c83fd] text-white rounded-xl font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] active:scale-[0.98] transition-all disabled:opacity-50"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Saving...
                </span>
              ) : isEditing ? (
                <><Edit2 className="w-4 h-4" /> Update Profile</>
              ) : (
                <><Plus className="w-4 h-4" /> Add Staff</>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
