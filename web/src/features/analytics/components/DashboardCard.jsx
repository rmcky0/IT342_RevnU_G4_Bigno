import React from "react";
import { TrendingUp, TrendingDown, Minus } from "lucide-react";

export const DeltaBadge = ({ delta, invert = false }) => {
  if (delta === null || delta === undefined) return null;
  const isPositive = invert ? delta < 0 : delta > 0;
  const isNeutral = delta === 0;

  if (isNeutral) {
    return (
      <span className="inline-flex items-center gap-0.5 text-[10px] font-bold text-gray-400 bg-gray-100 px-2 py-0.5 rounded-full border border-gray-200 uppercase tracking-wider">
        <Minus className="w-2.5 h-2.5" /> 0%
      </span>
    );
  }

  return (
    <span
      className={`inline-flex items-center gap-0.5 text-[10px] font-bold px-2 py-0.5 rounded-full border uppercase tracking-wider ${
        isPositive
          ? "text-emerald-700 bg-emerald-50 border-emerald-200"
          : "text-red-600 bg-red-50 border-red-200"
      }`}
    >
      {isPositive ? <TrendingUp className="w-2.5 h-2.5" /> : <TrendingDown className="w-2.5 h-2.5" />}
      {Math.abs(delta)}%
    </span>
  );
};

export const KPICard = ({
  title,
  value,
  delta,
  isEmpty,
  emptyMessage,
  invertDelta = false,
  icon: Icon,
  color = "indigo",
  payroll,
}) => {
  const colorMap = {
    indigo: { bg: "bg-[#7c83fd]/10", text: "text-[#7c83fd]", accent: "border-l-[#7c83fd]" },
    red:    { bg: "bg-red-50",        text: "text-red-500",    accent: "border-l-red-400"   },
    emerald:{ bg: "bg-emerald-50",    text: "text-emerald-500",accent: "border-l-emerald-400"},
  };
  const theme = colorMap[color] ?? colorMap.indigo;
  const hasPayroll = payroll && payroll > 0;

  return (
    <div className={`bg-white rounded-xl px-5 py-4 border border-gray-100 border-l-2 ${theme.accent} shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex items-center gap-4`}>
      {Icon && (
        <div className={`w-10 h-10 rounded-xl ${theme.bg} flex items-center justify-center shrink-0`}>
          <Icon className={`w-5 h-5 ${theme.text}`} />
        </div>
      )}
      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between gap-2 mb-0.5">
          <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest truncate">{title}</p>
          {!isEmpty && <DeltaBadge delta={delta} invert={invertDelta} />}
        </div>
        {isEmpty ? (
          <p className="text-sm text-gray-300 font-medium italic">{emptyMessage}</p>
        ) : (
          <>
            <p className="text-xl font-bold text-[#1e1b4b] tabular-nums tracking-tight leading-tight">
              <span className="text-sm text-gray-400 font-medium mr-0.5">₱</span>
              {value.toLocaleString("en-PH", { minimumFractionDigits: 2 })}
            </p>
            {hasPayroll && (
              <p className="text-[10px] font-bold text-violet-500 mt-0.5 tabular-nums">
                + ₱{payroll.toLocaleString("en-PH", { minimumFractionDigits: 2 })} payroll
              </p>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export const RecordCountCard = ({ title, count, icon: Icon }) => (
  <div className="bg-white px-5 py-4 rounded-xl border border-gray-100 shadow-[0_2px_12px_rgb(0,0,0,0.04)] flex items-center gap-4 flex-1 min-h-0">
    <div className="w-10 h-10 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center shrink-0">
      {Icon && <Icon className="w-5 h-5 text-[#7c83fd]" />}
    </div>
    <div className="min-w-0">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest truncate mb-0.5">{title}</p>
      <p className="text-xl font-bold text-[#1e1b4b] leading-tight tabular-nums">{count}</p>
    </div>
  </div>
);
