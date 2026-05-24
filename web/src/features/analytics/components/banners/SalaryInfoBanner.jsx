import React from "react";
import { Users } from "lucide-react";

export const SalaryInfoBanner = ({ amount }) => {
  if (!amount || amount <= 0) return null;
  return (
    <div className="bg-[#f0f1ff] border border-[#d6d9ff] rounded-xl px-4 py-3 text-sm text-[#1e1b4b] font-medium flex items-center gap-3 shrink-0 animate-in fade-in duration-200 mb-4">
      <div className="w-8 h-8 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center shrink-0">
        <Users className="w-4 h-4 text-[#7c83fd]" />
      </div>
      <p className="text-[13px]">
        <span className="font-bold text-[#7c83fd]">
          ₱{amount.toLocaleString("en-PH", { minimumFractionDigits: 2 })}
        </span>{" "}
        in salary payouts are included in today's expenses.
      </p>
    </div>
  );
};
