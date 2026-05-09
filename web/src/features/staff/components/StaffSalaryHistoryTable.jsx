import React from "react";
import { Inbox } from "lucide-react";

export const StaffSalaryHistoryTable = ({ fullname, history = [] }) => {
  const sortedHistory = [...history].sort(
    (a, b) => new Date(b.paymentDate) - new Date(a.paymentDate),
  );

  return (
    <div className="flex-1 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0 overflow-hidden relative">
      <div className="overflow-x-auto flex-1">
        <table className="w-full text-left border-collapse whitespace-nowrap">
          <thead className="bg-gradient-to-r from-gray-100 to-gray-200 text-gray-600 sticky top-0 z-10 shadow-sm">
            <tr>
              <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase">
                Payment Date
              </th>
              <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase text-right">
                Amount
              </th>
              <th className="px-6 py-3 font-semibold text-xs tracking-wider uppercase">
                System Log Date
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50 relative">
            {sortedHistory.length === 0 ? (
              <tr>
                <td colSpan="3" className="px-6 py-16 text-center">
                  <div className="flex flex-col items-center justify-center max-w-sm mx-auto animate-in fade-in zoom-in-95 duration-300">
                    <div className="w-16 h-16 bg-gray-50 rounded-2xl flex items-center justify-center mb-4 shadow-inner border border-white">
                      <Inbox className="w-8 h-8 text-gray-400" />
                    </div>
                    <h3 className="text-lg font-bold text-[#1e1b4b] mb-1">
                      No history found
                    </h3>
                    <p className="text-sm text-gray-500">
                      {fullname} hasn't received any recorded payouts yet.
                    </p>
                  </div>
                </td>
              </tr>
            ) : (
              sortedHistory.map((salary) => (
                <tr
                  key={salary.id}
                  className="group hover:bg-[#f8f9ff] hover:shadow-[inset_3px_0_0_0_#10b981] transition-all duration-200"
                >
                  <td className="px-6 py-3.5 text-sm font-bold text-[#1e1b4b]">
                    {salary.paymentDate
                      ? new Date(salary.paymentDate).toLocaleDateString(
                          "en-PH",
                          {
                            year: "numeric",
                            month: "long",
                            day: "numeric",
                          },
                        )
                      : "N/A"}
                  </td>
                  <td className="px-6 py-3.5 text-sm font-bold text-emerald-600 text-right">
                    ₱
                    {parseFloat(salary.amount || 0).toLocaleString(undefined, {
                      minimumFractionDigits: 2,
                    })}
                  </td>
                  <td className="px-6 py-3.5 text-xs text-gray-400 font-medium">
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
