import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { EmptyChart } from "./EmptyChart";

const formatPHP = (value) =>
  `₱${Number(value).toLocaleString("en-PH", { minimumFractionDigits: 2 })}`;

const tooltipFormatter = (value, name) => {
  const labels = {
    todaySales: "Sales (Today)",
    todayExpenses: "Expenses (Today)",
    yesterdaySales: "Sales (Yesterday)",
    yesterdayExpenses: "Expenses (Yesterday)",
  };
  return [formatPHP(value), labels[name] ?? name];
};

export const SalesTrendChart = ({ data = [], yesterdayLabel = "" }) => (
  <div className="lg:col-span-2 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0">
    {/* Header */}
    <div className="px-6 py-4 border-b border-gray-50 flex justify-between items-center shrink-0">
      <h3 className="text-sm font-bold text-[#1e1b4b] uppercase tracking-wider">
        Sales &amp; Expenses Trend
      </h3>
      <div className="flex flex-wrap gap-x-4 gap-y-1.5">
        <span className="flex items-center gap-1.5 text-[11px] font-bold text-gray-500 uppercase tracking-wider">
          <div className="w-2.5 h-2.5 rounded bg-[#7c83fd]" /> Sales Today
        </span>
        <span className="flex items-center gap-1.5 text-[11px] font-bold text-gray-500 uppercase tracking-wider">
          <div className="w-2.5 h-2.5 rounded bg-[#f87171]" /> Expenses Today
        </span>
        <span className="flex items-center gap-1.5 text-[11px] font-bold text-gray-400 uppercase tracking-wider">
          <div className="w-2.5 h-[3px] rounded bg-[#c7d2fe]" />{" "}
          {yesterdayLabel || "Yesterday"}
        </span>
      </div>
    </div>

    {/* Chart */}
    <div className="flex-1 p-5 min-h-0">
      {data.length === 0 ? (
        <EmptyChart message="Record sales and expenses to see the intraday trend." />
      ) : (
        <ResponsiveContainer width="100%" height="100%">
          <LineChart
            data={data}
            margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
          >
            <CartesianGrid
              strokeDasharray="3 3"
              vertical={false}
              stroke="#f1f5f9"
            />
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
              tickFormatter={(v) =>
                v >= 1000 ? `₱${(v / 1000).toFixed(1)}k` : `₱${v}`
              }
              dx={-10}
            />
            <Tooltip
              cursor={{
                stroke: "#e2e8f0",
                strokeWidth: 1,
                strokeDasharray: "4 4",
              }}
              contentStyle={{
                borderRadius: "12px",
                border: "1px solid #f1f5f9",
                boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)",
              }}
              formatter={tooltipFormatter}
            />

            {/* ── Today ── */}
            <Line
              type="monotone"
              dataKey="todaySales"
              stroke="#7c83fd"
              strokeWidth={3}
              dot={false}
              activeDot={{
                r: 6,
                fill: "#7c83fd",
                strokeWidth: 2,
                stroke: "#fff",
              }}
            />
            <Line
              type="monotone"
              dataKey="todayExpenses"
              stroke="#f87171"
              strokeWidth={3}
              dot={false}
              activeDot={{
                r: 6,
                fill: "#f87171",
                strokeWidth: 2,
                stroke: "#fff",
              }}
            />

            {/* ── Yesterday ── */}
            <Line
              type="monotone"
              dataKey="yesterdaySales"
              stroke="#c7d2fe"
              strokeWidth={2}
              strokeDasharray="5 5"
              dot={false}
            />
            <Line
              type="monotone"
              dataKey="yesterdayExpenses"
              stroke="#fecaca"
              strokeWidth={2}
              strokeDasharray="5 5"
              dot={false}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  </div>
);
