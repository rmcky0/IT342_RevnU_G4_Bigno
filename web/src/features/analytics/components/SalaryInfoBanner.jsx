import React from "react";
import { Users } from "lucide-react";

export const SalaryInfoBanner = ({ amount }) => {
  if (!amount || amount <= 0) return null;
  return (
    <div className="bg-indigo-50/50 border border-indigo-100 rounded-xl px-5 py-3 text-sm text-[#1e1b4b] font-medium flex items-center gap-3 shrink-0 shadow-sm">
      <div className="p-1.5 bg-indigo-100 text-[#7c83fd] rounded-md">
        <Users className="w-4 h-4" />
      </div>
      <p>
        <span className="font-bold text-[#7c83fd]">
          ₱{amount.toLocaleString("en-PH", { minimumFractionDigits: 2 })}
        </span>{" "}
        in salary payouts are included in today's expenses.
      </p>
    </div>
  );
};
