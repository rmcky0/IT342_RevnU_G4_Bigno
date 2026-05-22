import React from "react";
import { Wallet, Lock, Unlock, Pencil, Trash2 } from "lucide-react";

export const PayrollTable = ({ data, loading, searchTerm, onEdit, onDelete }) => {
  return (
    <table className="w-full text-left border-collapse whitespace-nowrap">
      <thead className="bg-gradient-to-r from-gray-100 to-gray-200 text-gray-600 sticky top-0 z-10 shadow-sm">
        <tr>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase">
            Payment Date
          </th>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase">
            Employee
          </th>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase text-center">
            Status
          </th>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase text-right">
            Amount
          </th>
          <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase text-right">
            Actions
          </th>
        </tr>
      </thead>
      <tbody className="divide-y divide-gray-50 relative">
        {loading ? (
          Array.from({ length: 6 }).map((_, idx) => (
            <tr key={`skel-sal-${idx}`} className="animate-pulse">
              <td className="px-6 py-3.5">
                <div className="h-3.5 bg-gray-100 rounded w-28"></div>
              </td>
              <td className="px-6 py-3.5">
                <div className="h-3.5 bg-gray-100 rounded w-32"></div>
              </td>
              <td className="px-6 py-3.5 flex justify-center">
                <div className="h-5 bg-gray-100 rounded-md w-16"></div>
              </td>
              <td className="px-6 py-3.5 flex justify-end">
                <div className="h-4 bg-gray-100 rounded-md w-20 mt-1"></div>
              </td>
            </tr>
          ))
        ) : data.length === 0 ? (
          <tr>
            <td colSpan="5" className="px-6 py-16 text-center">
              <div className="flex flex-col items-center justify-center max-w-sm mx-auto animate-in fade-in zoom-in-95 duration-300">
                <div className="w-16 h-16 bg-gray-50 rounded-2xl flex items-center justify-center mb-4 shadow-inner border border-white">
                  <Wallet className="w-8 h-8 text-gray-400" />
                </div>
                <h3 className="text-lg font-bold text-[#1e1b4b] mb-1">
                  No payroll records
                </h3>
                <p className="text-sm text-gray-500 mb-5">
                  {searchTerm
                    ? "No records match your search."
                    : "No salaries have been recorded yet."}
                </p>
              </div>
            </td>
          </tr>
        ) : (
          data.map((salary) => (
            <tr
              key={salary.id}
              className="group hover:bg-gray-50/50 transition-colors"
            >
              <td className="px-6 py-3.5 text-sm font-medium text-gray-600">
                {salary.paymentDate
                  ? new Date(salary.paymentDate).toLocaleDateString(undefined, {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    })
                  : "N/A"}
              </td>
              <td className="px-6 py-3.5 text-sm font-bold text-[#1e1b4b] truncate max-w-50">
                {salary.staffName || `Staff ID: ${salary.staffId}`}
              </td>

              {/* NEW: Display the Status from your DTO */}
              <td className="px-6 py-3.5">
                <div className="flex justify-center">
                  {salary.status === "CLOSED" ? (
                    <span className="flex items-center gap-1 px-2.5 py-1 bg-gray-100 text-gray-600 text-[10px] font-bold uppercase rounded-md shadow-sm">
                      <Lock className="w-3 h-3" /> Locked
                    </span>
                  ) : (
                    <span className="flex items-center gap-1 px-2.5 py-1 bg-emerald-50 text-emerald-600 text-[10px] font-bold uppercase rounded-md shadow-sm border border-emerald-100">
                      <Unlock className="w-3 h-3" /> Open
                    </span>
                  )}
                </div>
              </td>

              <td className="px-6 py-3.5 text-sm font-bold text-gray-700 text-right">
                ₱
                {parseFloat(salary.amount || 0).toLocaleString(undefined, {
                  minimumFractionDigits: 2,
                })}
              </td>
              <td className="px-6 py-3.5 text-right">
                {salary.status !== "CLOSED" ? (
                  <div className="flex items-center justify-end gap-2">
                    <button
                      onClick={() => onEdit?.(salary)}
                      className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-lg transition-colors"
                      title="Edit"
                    >
                      <Pencil className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onDelete?.(salary)}
                      className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                      title="Delete"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                ) : (
                  <span className="text-gray-300 text-xs">—</span>
                )}
              </td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
};
