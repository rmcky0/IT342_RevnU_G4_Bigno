import React from "react";
import { Users, Wallet, TrendingUp } from "lucide-react";

const KPICard = ({ icon: Icon, iconBg, iconColor, label, value, sub }) => (
  <div className="bg-white rounded-xl px-5 py-4 border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex items-center gap-4">
    <div className={`w-10 h-10 rounded-xl ${iconBg} flex items-center justify-center shrink-0`}>
      <Icon className={`w-5 h-5 ${iconColor}`} />
    </div>
    <div className="min-w-0">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest truncate">{label}</p>
      <p className="text-xl font-bold text-[#1e1b4b] leading-tight mt-0.5">{value}</p>
      <p className="text-[11px] text-gray-400 mt-0.5">{sub}</p>
    </div>
  </div>
);

export const StaffKPIs = ({ showKPIs, staffCount, totalMonthlyPayroll }) => {
  const avg = staffCount > 0 ? totalMonthlyPayroll / staffCount : 0;
  const fmt = (n) => n.toLocaleString(undefined, { maximumFractionDigits: 2 });

  return (
    <div
      className={`shrink-0 transition-all duration-300 ease-in-out origin-top overflow-hidden ${
        showKPIs ? "max-h-36 opacity-100" : "max-h-0 opacity-0"
      }`}
    >
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pb-1">
        <KPICard
          icon={Users}
          iconBg="bg-[#7c83fd]/10"
          iconColor="text-[#7c83fd]"
          label="Total Members"
          value={staffCount}
          sub="Active staff"
        />
        <KPICard
          icon={Wallet}
          iconBg="bg-red-50"
          iconColor="text-red-500"
          label="Est. Monthly Payroll"
          value={`₱${fmt(totalMonthlyPayroll)}`}
          sub="Base salaries total"
        />
        <KPICard
          icon={TrendingUp}
          iconBg="bg-[#7c83fd]/10"
          iconColor="text-[#7c83fd]"
          label="Avg. Salary Rate"
          value={`₱${fmt(avg)}`}
          sub="Per employee"
        />
      </div>
    </div>
  );
};
