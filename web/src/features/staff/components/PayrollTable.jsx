import React, { useMemo } from "react";
import { Wallet, Lock, Unlock, Pencil, Trash2, CalendarDays } from "lucide-react";

function groupByMonth(data) {
  const groups = {};
  (data || []).forEach((salary) => {
    const d = salary.paymentDate ? new Date(salary.paymentDate) : null;
    const key = d
      ? d.toLocaleDateString(undefined, { month: "long", year: "numeric" })
      : "Unknown";
    if (!groups[key]) groups[key] = [];
    groups[key].push(salary);
  });
  return Object.entries(groups);
}

const StatusBadge = ({ status }) =>
  status === "CLOSED" ? (
    <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-gray-100 text-gray-500 text-[10px] font-bold uppercase rounded-full border border-gray-200">
      <Lock className="w-2.5 h-2.5" /> Locked
    </span>
  ) : (
    <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-emerald-50 text-emerald-600 text-[10px] font-bold uppercase rounded-full border border-emerald-200">
      <Unlock className="w-2.5 h-2.5" /> Open
    </span>
  );

export const PayrollTable = ({ data, loading, searchTerm, onEdit, onDelete }) => {
  const grouped = useMemo(() => groupByMonth(data), [data]);

  if (loading) {
    return (
      <div className="flex-1 overflow-auto rounded-xl border border-gray-100 bg-white">
        <table className="w-full text-left border-collapse whitespace-nowrap">
          <thead className="bg-gray-50 sticky top-0 z-10">
            <tr>
              {["Date", "Employee", "Status", "Amount", ""].map((h, i) => (
                <th
                  key={i}
                  className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100"
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50">
            {Array.from({ length: 6 }).map((_, idx) => (
              <tr key={idx} className="animate-pulse">
                <td className="px-5 py-3.5">
                  <div className="h-3 bg-gray-100 rounded w-24" />
                </td>
                <td className="px-5 py-3.5">
                  <div className="h-3 bg-gray-100 rounded w-32" />
                </td>
                <td className="px-5 py-3.5">
                  <div className="h-4 bg-gray-100 rounded-full w-14 mx-auto" />
                </td>
                <td className="px-5 py-3.5 text-right">
                  <div className="h-3 bg-gray-100 rounded w-16 ml-auto" />
                </td>
                <td className="px-5 py-3.5">
                  <div className="h-4 bg-gray-100 rounded w-12 ml-auto" />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center py-20 rounded-xl border border-gray-100 bg-white animate-in fade-in zoom-in-95 duration-300">
        <div className="w-14 h-14 bg-indigo-50 rounded-2xl flex items-center justify-center mb-4 shadow-inner">
          <Wallet className="w-7 h-7 text-[#7c83fd]" />
        </div>
        <p className="text-base font-bold text-[#1e1b4b] mb-1">No payroll records</p>
        <p className="text-sm text-gray-400">
          {searchTerm ? "No records match your search." : "No salaries have been recorded yet."}
        </p>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-auto rounded-xl border border-gray-100 bg-white">
      <table className="w-full text-left border-collapse whitespace-nowrap">
        <thead className="bg-gray-50 sticky top-0 z-10">
          <tr>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 w-40">
              Date
            </th>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100">
              Employee
            </th>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 text-center">
              Status
            </th>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 text-right">
              Payout
            </th>
            <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 text-right w-24">
              Actions
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-50">
          {grouped.map(([month, rows]) => (
            <React.Fragment key={month}>
              {/* Month separator row */}
              <tr className="bg-[#f8f9ff]">
                <td colSpan={5} className="px-5 py-2">
                  <div className="flex items-center gap-1.5">
                    <CalendarDays className="w-3 h-3 text-[#7c83fd]" />
                    <span className="text-[11px] font-bold text-[#7c83fd] uppercase tracking-widest">
                      {month}
                    </span>
                  </div>
                </td>
              </tr>

              {rows.map((salary) => (
                <tr
                  key={salary.id}
                  className="group hover:bg-[#f8f9ff] transition-colors duration-150"
                >
                  <td className="px-5 py-3 text-sm text-gray-500 font-medium">
                    {salary.paymentDate
                      ? new Date(salary.paymentDate).toLocaleDateString(undefined, {
                          month: "short",
                          day: "numeric",
                          year: "numeric",
                        })
                      : "N/A"}
                  </td>
                  <td className="px-5 py-3">
                    <span className="text-sm font-bold text-[#1e1b4b] truncate block max-w-45">
                      {salary.staffName || `Staff #${salary.staffId}`}
                    </span>
                  </td>
                  <td className="px-5 py-3 text-center">
                    <StatusBadge status={salary.status} />
                  </td>
                  <td className="px-5 py-3 text-sm font-bold text-gray-800 text-right tabular-nums">
                    ₱{parseFloat(salary.amount || 0).toLocaleString(undefined, {
                      minimumFractionDigits: 2,
                    })}
                  </td>
                  <td className="px-5 py-3 text-right">
                    {salary.status !== "CLOSED" ? (
                      <div className="inline-flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                        <button
                          onClick={() => onEdit?.(salary)}
                          className="p-1.5 rounded-lg text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 transition-colors"
                          title="Edit"
                        >
                          <Pencil className="w-3.5 h-3.5" />
                        </button>
                        <button
                          onClick={() => onDelete?.(salary)}
                          className="p-1.5 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                          title="Delete"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    ) : (
                      <span className="text-gray-200 text-xs select-none">—</span>
                    )}
                  </td>
                </tr>
              ))}
            </React.Fragment>
          ))}
        </tbody>
      </table>
    </div>
  );
};
