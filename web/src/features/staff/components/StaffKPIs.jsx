import React from "react";
import { Users, Wallet, TrendingUp } from "lucide-react";

export const StaffKPIs = ({ showKPIs, staffCount, totalMonthlyPayroll }) => {
  return (
    <div
      className={`shrink-0 transition-all duration-300 ease-in-out origin-top overflow-hidden ${showKPIs ? "max-h-[200px] opacity-100" : "max-h-0 opacity-0 m-0"}`}
    >
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pb-1">
        {/* Total Members */}
        <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
          <div className="absolute -right-4 -top-4 text-indigo-50 opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none">
            <Users className="w-24 h-24" />
          </div>
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-3">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                Total Members
              </p>
              <div className="p-1.5 bg-gradient-to-br from-indigo-50 to-[#7c83fd]/10 text-[#7c83fd] rounded-lg">
                <Users className="w-4 h-4" />
              </div>
            </div>
            <div className="text-2xl font-bold text-[#1e1b4b]">
              {staffCount}
            </div>
            <p className="text-xs text-gray-400 font-medium mt-1">
              Active Staff
            </p>
          </div>
        </div>

        {/* Est. Monthly Payroll */}
        <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
          <div className="absolute -right-4 -top-4 text-red-50 opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none">
            <Wallet className="w-24 h-24" />
          </div>
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-3">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                Est. Monthly Payroll
              </p>
              <div className="p-1.5 bg-gradient-to-br from-red-50 to-red-100/50 text-red-500 rounded-lg">
                <Wallet className="w-4 h-4" />
              </div>
            </div>
            <div className="text-2xl font-bold text-[#1e1b4b] flex items-baseline gap-1">
              <span className="text-lg text-gray-400 font-medium">₱</span>
              {totalMonthlyPayroll.toLocaleString(undefined, {
                maximumFractionDigits: 2,
              })}
            </div>
            <p className="text-xs text-gray-400 font-medium mt-1">
              Base Salaries Total
            </p>
          </div>
        </div>

        {/* Avg. Salary Rate */}
        <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
          <div className="absolute -right-4 -top-4 text-indigo-50 opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none">
            <TrendingUp className="w-24 h-24" />
          </div>
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-3">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                Avg. Salary Rate
              </p>
              <div className="p-1.5 bg-gradient-to-br from-indigo-50 to-[#7c83fd]/10 text-[#7c83fd] rounded-lg">
                <TrendingUp className="w-4 h-4" />
              </div>
            </div>
            <div className="text-2xl font-bold text-[#1e1b4b] flex items-baseline gap-1">
              <span className="text-lg text-gray-400 font-medium">₱</span>
              {(staffCount > 0
                ? totalMonthlyPayroll / staffCount
                : 0
              ).toLocaleString(undefined, { maximumFractionDigits: 2 })}
            </div>
            <p className="text-xs text-gray-400 font-medium mt-1">
              Per Employee
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
