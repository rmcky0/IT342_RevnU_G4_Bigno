import React from "react";
import {
  TrendingUp,
  TrendingDown,
  CreditCard,
  ListOrdered,
} from "lucide-react";

export const SharedKPIs = ({
  showKPIs,
  total,
  count,
  todayDisplay,
  type = "sales",
}) => {
  const isExpense = type === "expenses";

  const config = {
    mainIcon: isExpense ? TrendingDown : TrendingUp,
    mainTitle: isExpense ? "Total Expenses" : "Total Revenue",
    mainBgIconClass: isExpense ? "text-red-50" : "text-indigo-50",
    mainIconBoxClass: isExpense
      ? "bg-gradient-to-br from-red-50 to-red-100/50 text-red-500"
      : "bg-gradient-to-br from-indigo-50 to-[#7c83fd]/10 text-[#7c83fd]",
    avgSubtext: isExpense ? "Operational Cost" : "Current Session",
    countTitle: isExpense ? "Total Records" : "Transaction Count",
    countSubtext: isExpense ? "Current Session" : "Completed Orders",
  };

  const MainIcon = config.mainIcon;

  return (
    <div
      className={`shrink-0 transition-all duration-300 ease-in-out origin-top overflow-hidden ${
        showKPIs ? "max-h-[200px] opacity-100" : "max-h-0 opacity-0 m-0"
      }`}
    >
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pb-1">
        {/* Card 1: Main Total (Revenue/Expenses) */}
        <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
          <div
            className={`absolute -right-4 -top-4 opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none ${config.mainBgIconClass}`}
          >
            <MainIcon className="w-24 h-24" />
          </div>
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-3">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                {config.mainTitle}
              </p>
              <div className={`p-1.5 rounded-lg ${config.mainIconBoxClass}`}>
                <MainIcon className="w-4 h-4" />
              </div>
            </div>
            <div className="text-2xl font-bold text-[#1e1b4b] flex items-baseline gap-1">
              <span className="text-lg text-gray-400 font-medium">₱</span>
              {total.toLocaleString(undefined, { minimumFractionDigits: 2 })}
            </div>
            <p className="text-xs text-gray-400 font-medium mt-1">
              {todayDisplay}
            </p>
          </div>
        </div>

        {/* Card 2: Average Ticket Size */}
        <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
          <div className="absolute -right-4 -top-4 text-indigo-50 opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none">
            <CreditCard className="w-24 h-24" />
          </div>
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-3">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                Avg. Ticket Size
              </p>
              <div className="p-1.5 bg-gradient-to-br from-indigo-50 to-[#7c83fd]/10 text-[#7c83fd] rounded-lg">
                <CreditCard className="w-4 h-4" />
              </div>
            </div>
            <div className="text-2xl font-bold text-[#1e1b4b] flex items-baseline gap-1">
              <span className="text-lg text-gray-400 font-medium">₱</span>
              {(count > 0 ? total / count : 0).toLocaleString(undefined, {
                maximumFractionDigits: 2,
              })}
            </div>
            <p className="text-xs text-gray-400 font-medium mt-1">
              {config.avgSubtext}
            </p>
          </div>
        </div>

        {/* Card 3: Total Count */}
        <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
          <div className="absolute -right-4 -top-4 text-indigo-50 opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none">
            <ListOrdered className="w-24 h-24" />
          </div>
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-3">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                {config.countTitle}
              </p>
              <div className="p-1.5 bg-gradient-to-br from-indigo-50 to-[#7c83fd]/10 text-[#7c83fd] rounded-lg">
                <ListOrdered className="w-4 h-4" />
              </div>
            </div>
            <div className="text-2xl font-bold text-[#1e1b4b]">{count}</div>
            <p className="text-xs text-gray-400 font-medium mt-1">
              {config.countSubtext}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
