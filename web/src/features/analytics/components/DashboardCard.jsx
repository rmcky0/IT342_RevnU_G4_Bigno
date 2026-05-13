import React from "react";
import { TrendingUp, TrendingDown, Minus } from "lucide-react";

export const DeltaBadge = ({ delta, invert = false }) => {
  if (delta === null || delta === undefined) return null;
  const isPositive = invert ? delta < 0 : delta > 0;
  const isNeutral = delta === 0;

  if (isNeutral) {
    return (
      <span className="flex items-center gap-0.5 text-[10px] font-bold text-gray-500 bg-gray-100 px-2 py-0.5 rounded-md uppercase tracking-wider border border-gray-200">
        <Minus className="w-3 h-3" /> 0%
      </span>
    );
  }

  return (
    <span
      className={`flex items-center gap-0.5 text-[10px] font-bold px-2 py-0.5 rounded-md uppercase tracking-wider border ${
        isPositive
          ? "text-emerald-700 bg-emerald-50 border-emerald-200/60"
          : "text-red-600 bg-red-50 border-red-200/60"
      }`}
    >
      {isPositive ? (
        <TrendingUp className="w-3 h-3" />
      ) : (
        <TrendingDown className="w-3 h-3" />
      )}
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
}) => {
  const colorMap = {
    indigo: { bg: "bg-indigo-50", text: "text-[#7c83fd]" },
    red: { bg: "bg-red-50", text: "text-red-500" },
    emerald: { bg: "bg-emerald-50", text: "text-emerald-500" },
  };

  const theme = colorMap[color];

  return (
    <div className="bg-white rounded-xl p-5 shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col justify-center relative overflow-hidden group">
      <div
        className={`absolute -right-4 -top-4 ${theme.bg} opacity-50 group-hover:scale-110 transition-transform duration-500 pointer-events-none rounded-full p-4`}
      >
        {Icon && <Icon className="w-20 h-20" />}
      </div>
      <div className="relative z-10">
        <div className="flex justify-between items-start mb-3">
          <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">
            {title}
          </p>
          {!isEmpty && <DeltaBadge delta={delta} invert={invertDelta} />}
        </div>
        {isEmpty ? (
          <p className="text-xs font-medium text-gray-400 mt-2">
            {emptyMessage}
          </p>
        ) : (
          <div className="text-3xl font-black text-[#1e1b4b] flex items-baseline gap-1 mt-1">
            <span className="text-xl text-gray-400 font-medium">₱</span>
            {value.toLocaleString("en-PH", { minimumFractionDigits: 2 })}
          </div>
        )}
      </div>
    </div>
  );
};

export const RecordCountCard = ({ title, count, icon: Icon }) => (
  <div className="bg-white px-5 py-4 rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex items-center justify-between flex-1 min-h-0 group">
    <div className="flex flex-col justify-center min-w-0 pr-2">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1 truncate">
        {title}
      </p>
      <p className="text-2xl lg:text-3xl font-black text-[#1e1b4b] leading-none truncate">
        {count}
      </p>
    </div>
    <div className="p-2.5 bg-indigo-50/50 rounded-xl group-hover:bg-indigo-50 transition-colors border border-indigo-50 shrink-0">
      {Icon && <Icon className="w-5 h-5 text-[#7c83fd]" />}
    </div>
  </div>
);
