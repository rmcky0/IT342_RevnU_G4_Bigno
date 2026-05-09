import React from "react";
import { X, Plus, Edit2 } from "lucide-react";

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
    <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-md w-full shadow-2xl relative animate-in zoom-in-95 duration-200 border border-gray-100 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-100 bg-gray-50/50 flex items-center justify-between">
          <div>
            <h3 className="text-lg font-bold text-[#1e1b4b]">
              {isEditing ? "Edit Staff Profile" : "Add New Staff"}
            </h3>
            <p className="text-gray-500 text-xs mt-0.5">
              Enter employee details below.
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
          <form onSubmit={onSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                Full Name
              </label>
              <input
                type="text"
                value={formData.fullname}
                onChange={(e) =>
                  setFormData({ ...formData, fullname: e.target.value })
                }
                className="w-full px-4 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
                placeholder="e.g. Juan Dela Cruz"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                  Position
                </label>
                <input
                  type="text"
                  value={formData.position}
                  onChange={(e) =>
                    setFormData({ ...formData, position: e.target.value })
                  }
                  className="w-full px-4 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-medium text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
                  placeholder="e.g. Cashier"
                  required
                />
              </div>
              <div className="space-y-1.5">
                <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                  Salary Rate
                </label>
                <div className="relative group">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 font-medium group-focus-within:text-[#7c83fd] transition-colors">
                    ₱
                  </span>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.salaryRate}
                    onChange={(e) =>
                      setFormData({ ...formData, salaryRate: e.target.value })
                    }
                    className="w-full pl-8 pr-3 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
                    placeholder="0.00"
                    required
                  />
                </div>
              </div>
            </div>

            <div className="pt-4">
              <button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-center gap-2 py-2.5 bg-[#7c83fd] text-white rounded-lg font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] transition-all disabled:opacity-50 hover:-translate-y-0.5 disabled:hover:translate-y-0"
              >
                {loading ? (
                  "Saving..."
                ) : isEditing ? (
                  <>
                    <Edit2 className="w-4 h-4" /> Update Profile
                  </>
                ) : (
                  <>
                    <Plus className="w-4 h-4" /> Add Staff
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
