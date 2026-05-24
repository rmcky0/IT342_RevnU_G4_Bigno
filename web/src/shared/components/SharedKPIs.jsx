import React from "react";
import { TrendingUp, TrendingDown, CreditCard, ListOrdered } from "lucide-react";

const KPICard = ({ icon: Icon, iconBg, iconColor, label, value, sub, accent }) => (
  <div className={`bg-white rounded-xl px-5 py-4 border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex items-center gap-4 ${accent ? `border-l-2 ${accent}` : ""}`}>
    <div className={`w-10 h-10 rounded-xl ${iconBg} flex items-center justify-center shrink-0`}>
      <Icon className={`w-5 h-5 ${iconColor}`} />
    </div>
    <div className="min-w-0">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest truncate">{label}</p>
      <p className="text-xl font-bold text-[#1e1b4b] leading-tight mt-0.5 tabular-nums">{value}</p>
      <p className="text-[11px] text-gray-400 mt-0.5">{sub}</p>
    </div>
  </div>
);

export const SharedKPIs = ({ showKPIs, total, count, todayDisplay, type = "sales" }) => {
  const isExpense = type === "expenses";
  const avg = count > 0 ? total / count : 0;
  const fmt = (n) => `₱${n.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;

  return (
    <div
      className={`shrink-0 transition-all duration-300 ease-in-out origin-top overflow-hidden ${
        showKPIs ? "max-h-36 opacity-100" : "max-h-0 opacity-0"
      }`}
    >
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pb-1">
        <KPICard
          icon={isExpense ? TrendingDown : TrendingUp}
          iconBg={isExpense ? "bg-red-50" : "bg-[#7c83fd]/10"}
          iconColor={isExpense ? "text-red-500" : "text-[#7c83fd]"}
          label={isExpense ? "Total expenses" : "Total revenue"}
          value={fmt(total)}
          sub={todayDisplay}
          accent={isExpense ? "border-l-red-400" : "border-l-[#7c83fd]"}
        />
        <KPICard
          icon={CreditCard}
          iconBg="bg-gray-100"
          iconColor="text-gray-400"
          label="Avg. ticket size"
          value={fmt(avg)}
          sub={isExpense ? "Operational cost" : "Current session"}
        />
        <KPICard
          icon={ListOrdered}
          iconBg="bg-gray-100"
          iconColor="text-gray-400"
          label={isExpense ? "Total records" : "Transaction count"}
          value={count}
          sub={isExpense ? "Current session" : "Completed orders"}
        />
      </div>
    </div>
  );
};
