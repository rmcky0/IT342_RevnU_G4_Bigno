import React from "react";
import { X, Users, Wallet, ChevronRight } from "lucide-react";

export const StaffViewModal = ({
  staffToView,
  onClose,
  salaries,
  navigate,
}) => {
  if (!staffToView) return null;

  const recentSalaries = (salaries || [])
    .filter((s) => s.staffId === staffToView.id)
    .sort((a, b) => new Date(b.paymentDate) - new Date(a.paymentDate))
    .slice(0, 3);

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl relative border border-gray-100 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-100 bg-gray-50/50 flex items-center justify-between">
          <h3 className="text-lg font-bold text-[#1e1b4b]">Staff Overview</h3>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-5">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-100 flex items-center justify-center text-[#7c83fd] font-bold text-lg shadow-sm shrink-0">
              {staffToView.fullname ? (
                staffToView.fullname.charAt(0).toUpperCase()
              ) : (
                <Users className="w-5 h-5" />
              )}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-lg font-bold text-[#1e1b4b] leading-tight break-all">
                {staffToView.fullname}
              </p>
              <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mt-1 break-all">
                {staffToView.position}
              </p>
            </div>
          </div>

          <div>
            <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider mb-1">
              Base Salary Rate
            </p>
            <p className="text-xl font-black text-[#7c83fd]">
              ₱
              {parseFloat(staffToView.salaryRate || 0).toLocaleString(
                undefined,
                { minimumFractionDigits: 2 },
              )}
            </p>
          </div>

          <div>
            <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider mb-2">
              Recent Payroll
            </p>
            <div className="space-y-2">
              {recentSalaries.length === 0 ? (
                <p className="text-sm text-gray-500 italic bg-gray-50 p-3 rounded-lg border border-gray-100">
                  No payroll records found.
                </p>
              ) : (
                recentSalaries.map((s, i) => (
                  <div
                    key={i}
                    className="flex justify-between items-center bg-gray-50 p-2.5 rounded-lg border border-gray-100"
                  >
                    <div className="flex items-center gap-2">
                      <Wallet className="w-4 h-4 text-emerald-500" />
                      <span className="text-sm font-semibold text-[#1e1b4b]">
                        {new Date(s.paymentDate).toLocaleDateString(undefined, {
                          month: "short",
                          day: "numeric",
                          year: "numeric",
                        })}
                      </span>
                    </div>
                    <span className="text-sm font-bold text-gray-700">
                      ₱
                      {parseFloat(s.amount || 0).toLocaleString(undefined, {
                        minimumFractionDigits: 2,
                      })}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="pt-2">
            <button
              onClick={() => navigate(`/staff/${staffToView.id}`)}
              className="w-full flex items-center justify-center gap-2 py-2.5 bg-indigo-50 text-[#7c83fd] rounded-lg font-semibold text-sm hover:bg-indigo-100 transition-colors"
            >
              View Full Profile <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
