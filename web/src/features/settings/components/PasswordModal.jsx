import React from "react";
import { X, Lock, Check } from "lucide-react";

const inputCls =
  "w-full px-4 py-2.5 bg-gray-50 rounded-xl border border-gray-200 focus:bg-white focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50 font-semibold text-[#1e1b4b] text-sm outline-none transition-all";

const Field = ({ label, name, value, onChange }) => (
  <div className="flex flex-col gap-1.5">
    <label className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">
      {label}
    </label>
    <input
      type="password"
      name={name}
      value={value}
      onChange={onChange}
      required
      className={inputCls}
    />
  </div>
);

const StrengthRule = ({ isValid, label }) => (
  <div className="flex items-center gap-1.5">
    <div
      className={`w-3.5 h-3.5 rounded-full border flex items-center justify-center shrink-0 transition-colors duration-200 ${
        isValid ? "bg-green-500 border-green-500" : "border-gray-300 bg-transparent"
      }`}
    >
      {isValid && <Check className="w-2 h-2 text-white" strokeWidth={3} />}
    </div>
    <span className={`text-[11px] font-medium transition-colors duration-200 ${isValid ? "text-gray-700" : "text-gray-400"}`}>
      {label}
    </span>
  </div>
);

export const PasswordModal = ({ passwordData, loading, onInputChange, onSubmit, onClose }) => {
  const pw = passwordData.newPassword;
  const rules = [
    { label: "8+ characters", isValid: pw.length >= 8 },
    { label: "1 uppercase", isValid: /[A-Z]/.test(pw) },
    { label: "1 lowercase", isValid: /[a-z]/.test(pw) },
    { label: "1 number", isValid: /\d/.test(pw) },
    { label: "1 special char", isValid: /[^A-Za-z\d]/.test(pw) },
  ];

  return (
    <div
      className="fixed inset-0 bg-[#1e1b4b]/40 backdrop-blur-[3px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center shrink-0">
              <Lock className="w-4.5 h-4.5 text-[#7c83fd]" />
            </div>
            <div>
              <p className="text-[13px] font-bold text-[#1e1b4b]">Change Password</p>
              <p className="text-[11px] text-gray-400 mt-0.5">Keep your account secure.</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-xl transition-colors"
          >
            <X className="w-4.5 h-4.5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={onSubmit} className="p-5 flex flex-col gap-4">
          <Field
            label="Current Password"
            name="currentPassword"
            value={passwordData.currentPassword}
            onChange={onInputChange}
          />
          <Field
            label="New Password"
            name="newPassword"
            value={passwordData.newPassword}
            onChange={onInputChange}
          />

          {/* Strength indicator */}
          {pw.length > 0 && (
            <div className="grid grid-cols-2 gap-y-2 gap-x-4 bg-gray-50 border border-gray-100 rounded-xl px-4 py-3">
              {rules.map((r) => (
                <StrengthRule key={r.label} isValid={r.isValid} label={r.label} />
              ))}
            </div>
          )}

          <Field
            label="Confirm New Password"
            name="confirmPassword"
            value={passwordData.confirmPassword}
            onChange={onInputChange}
          />

          <div className="flex gap-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 rounded-xl border border-gray-200 text-[13px] font-bold text-gray-500 hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 py-2.5 bg-[#7c83fd] text-white rounded-xl text-[13px] font-bold hover:bg-[#6b72f5] transition-colors disabled:opacity-50"
            >
              {loading ? "Updating…" : "Update Password"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
