import React from "react";
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from "recharts";
import { EmptyChart } from "./EmptyChart";

const formatPHP = (value) =>
  `₱${Number(value).toLocaleString("en-PH", { minimumFractionDigits: 2 })}`;

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload || !payload.length) return null;
  return (
    <div className="bg-white/95 backdrop-blur-md border border-gray-100 p-3 rounded-xl shadow-[0_8px_30px_rgb(0,0,0,0.08)]">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-2">{label}</p>
      {payload.map((entry, index) => (
        <div key={index} className="flex items-center justify-between gap-6 mb-1 text-sm font-semibold">
          <div className="flex items-center gap-2 text-gray-500">
            <div className="w-2 h-2 rounded-full" style={{ backgroundColor: entry.color }} />
            {entry.name === "todaySales"
              ? "Sales"
              : entry.name === "todayExpenses"
                ? "Expenses"
                : "Yesterday"}
          </div>
          <span className="text-[#1e1b4b] font-bold tabular-nums">{formatPHP(entry.value)}</span>
        </div>
      ))}
    </div>
  );
};

export const SalesTrendChart = ({ data = [], yesterdayLabel = "Yesterday" }) => (
  <div className="lg:col-span-2 bg-white rounded-xl shadow-[0_2px_12px_rgb(0,0,0,0.04)] border border-gray-100 flex flex-col min-h-0 overflow-hidden">
    <div className="px-5 py-3.5 border-b border-gray-100 flex flex-wrap justify-between items-center shrink-0 gap-3">
      <h3 className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Intraday Trend</h3>
      <div className="flex items-center gap-4">
        <span className="flex items-center gap-1.5 text-[10px] font-bold text-gray-500 uppercase tracking-wider">
          <div className="w-2 h-2 rounded-full bg-[#7c83fd]" /> Sales
        </span>
        <span className="flex items-center gap-1.5 text-[10px] font-bold text-gray-500 uppercase tracking-wider">
          <div className="w-2 h-2 rounded-full bg-red-400" /> Expenses
        </span>
        <span className="flex items-center gap-1.5 text-[10px] font-bold text-gray-400 uppercase tracking-wider">
          <div className="w-2.5 h-[3px] rounded-full bg-indigo-100" /> {yesterdayLabel}
        </span>
      </div>
    </div>

    <div className="flex-1 p-5 min-h-0">
      {data.length === 0 ? (
        <EmptyChart message="Record sales and expenses to see the intraday trend." />
      ) : (
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
            <XAxis
              dataKey="time"
              axisLine={false}
              tickLine={false}
              tick={{ fill: "#94a3b8", fontSize: 10, fontWeight: 600 }}
              dy={10}
            />
            <YAxis
              axisLine={false}
              tickLine={false}
              tick={{ fill: "#94a3b8", fontSize: 10, fontWeight: 600 }}
              tickFormatter={(v) => v >= 1000 ? `₱${(v / 1000).toFixed(1)}k` : `₱${v}`}
              dx={-10}
            />
            <Tooltip content={<CustomTooltip />} cursor={{ stroke: "#e2e8f0", strokeWidth: 1, strokeDasharray: "4 4" }} />
            <Line type="monotone" dataKey="yesterdaySales"   stroke="#c7d2fe" strokeWidth={2} strokeDasharray="5 5" dot={false} />
            <Line type="monotone" dataKey="yesterdayExpenses" stroke="#fecaca" strokeWidth={2} strokeDasharray="5 5" dot={false} />
            <Line type="monotone" dataKey="todaySales"    stroke="#7c83fd" strokeWidth={3} dot={false} activeDot={{ r: 5, fill: "#7c83fd", stroke: "#fff", strokeWidth: 2 }} />
            <Line type="monotone" dataKey="todayExpenses" stroke="#f87171" strokeWidth={3} dot={false} activeDot={{ r: 5, fill: "#f87171", stroke: "#fff", strokeWidth: 2 }} />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  </div>
);
