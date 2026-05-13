import React from "react";
import { X } from "lucide-react";

export const PasswordModal = ({
  passwordData,
  loading,
  onInputChange,
  onSubmit,
  onClose,
}) => (
  <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
    <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl relative border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-200">
      <div className="px-6 py-5 border-b border-gray-100 bg-gray-50/50 flex items-center justify-between">
        <div>
          <h3 className="text-lg font-bold text-[#1e1b4b]">Change Password</h3>
          <p className="text-gray-500 text-xs mt-0.5">
            Ensure your account stays secure.
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
          {[
            { name: "currentPassword", label: "Current Password" },
            { name: "newPassword", label: "New Password" },
            { name: "confirmPassword", label: "Confirm New Password" },
          ].map(({ name, label }) => (
            <div key={name} className="space-y-1.5">
              <label className="block text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                {label}
              </label>
              <input
                type="password"
                name={name}
                value={passwordData[name]}
                onChange={onInputChange}
                required
                className="w-full px-4 py-2.5 bg-white rounded-lg border border-gray-200 focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] transition-all text-sm outline-none shadow-sm"
              />
            </div>
          ))}

          <div className="pt-4">
            <button
              type="submit"
              disabled={loading}
              className="w-full flex items-center justify-center gap-2 py-2.5 bg-[#7c83fd] text-white rounded-lg font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] transition-all disabled:opacity-50 hover:-translate-y-0.5 disabled:hover:translate-y-0"
            >
              {loading ? "Updating..." : "Update Password"}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
);
