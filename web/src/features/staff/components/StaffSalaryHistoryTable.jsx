import React from "react";
import { Inbox } from "lucide-react";

export const StaffSalaryHistoryTable = ({ fullname, history = [] }) => {
  const sortedHistory = [...history].sort(
    (a, b) => new Date(b.paymentDate) - new Date(a.paymentDate),
  );

  return (
    <div className="flex-1 bg-white rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex flex-col min-h-0 overflow-hidden">
      <div className="overflow-x-auto flex-1">
        <table className="w-full text-left border-collapse whitespace-nowrap">
          <thead className="bg-gray-50 sticky top-0 z-10">
            <tr>
              <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100">
                Payment Date
              </th>
              <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100 text-right">
                Amount
              </th>
              <th className="px-5 py-3 text-[10px] font-bold text-gray-400 uppercase tracking-widest border-b border-gray-100">
                Logged At
              </th>
            </tr>
          </thead>
          <tbody>
            {sortedHistory.length === 0 ? (
              <tr>
                <td colSpan={3} className="px-5 py-16 text-center">
                  <div className="flex flex-col items-center justify-center gap-3 animate-in fade-in zoom-in-95 duration-300">
                    <div className="w-14 h-14 bg-indigo-50 rounded-2xl flex items-center justify-center">
                      <Inbox className="w-7 h-7 text-[#7c83fd]/40" />
                    </div>
                    <div>
                      <p className="text-[13px] font-bold text-[#1e1b4b] mb-0.5">No payouts yet</p>
                      <p className="text-[11px] text-gray-400">
                        {fullname} hasn't received any recorded payouts.
                      </p>
                    </div>
                  </div>
                </td>
              </tr>
            ) : (
              sortedHistory.map((salary) => (
                <tr
                  key={salary.id}
                  className="hover:bg-[#f8f9ff] transition-colors border-b border-gray-50 last:border-0"
                >
                  <td className="px-5 py-3.5 text-[13px] font-bold text-[#1e1b4b]">
                    {salary.paymentDate
                      ? new Date(salary.paymentDate).toLocaleDateString("en-PH", {
                          year: "numeric",
                          month: "long",
                          day: "numeric",
                        })
                      : "N/A"}
                  </td>
                  <td className="px-5 py-3.5 text-[13px] font-bold text-emerald-500 text-right tabular-nums">
                    ₱{parseFloat(salary.amount || 0).toLocaleString("en-PH", { minimumFractionDigits: 2 })}
                  </td>
                  <td className="px-5 py-3.5 text-[11px] text-gray-400 font-medium">
                    {salary.createdAt
                      ? new Date(salary.createdAt).toLocaleDateString("en-PH", {
                          year: "numeric",
                          month: "short",
                          day: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })
                      : "—"}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
