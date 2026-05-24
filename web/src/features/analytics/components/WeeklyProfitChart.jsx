import React from "react";
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
} from "recharts";
import { EmptyChart } from "./EmptyChart";

const formatPHP = (value) => `₱${Number(value).toLocaleString("en-PH")}`;

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload || !payload.length) return null;
  return (
    <div className="bg-white/95 backdrop-blur-md border border-gray-100 px-4 py-2.5 rounded-xl shadow-[0_8px_30px_rgb(0,0,0,0.08)]">
      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">{label}</p>
      <p className="text-sm font-bold text-[#1e1b4b] tabular-nums">{formatPHP(payload[0].value)}</p>
    </div>
  );
};

export const WeeklyProfitChart = ({ data = [] }) => (
  <div className="lg:col-span-2 bg-white rounded-xl shadow-[0_2px_12px_rgb(0,0,0,0.04)] border border-gray-100 flex flex-col min-h-0">
    <div className="px-5 py-3.5 border-b border-gray-100 shrink-0">
      <h3 className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">7-Day Profit</h3>
    </div>

    <div className="flex-1 p-5 min-h-0">
      {data.length === 0 ? (
        <EmptyChart message="Complete an EOD close to track history." />
      ) : (
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <defs>
              <linearGradient id="colorProfit" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%"  stopColor="#10b981" stopOpacity={0.2} />
                <stop offset="95%" stopColor="#10b981" stopOpacity={0}   />
              </linearGradient>
            </defs>
            <XAxis
              dataKey="date"
              axisLine={false}
              tickLine={false}
              tick={{ fill: "#94a3b8", fontSize: 10, fontWeight: 600 }}
              dy={5}
            />
            <YAxis
              axisLine={false}
              tickLine={false}
              tick={{ fill: "#94a3b8", fontSize: 10, fontWeight: 600 }}
              tickFormatter={(v) => v >= 1000 ? `₱${(v / 1000).toFixed(1)}k` : `₱${v}`}
            />
            <Tooltip content={<CustomTooltip />} />
            <Area
              type="monotone"
              dataKey="netProfit"
              stroke="#10b981"
              fillOpacity={1}
              fill="url(#colorProfit)"
              strokeWidth={3}
              activeDot={{ r: 5, fill: "#10b981", stroke: "#fff", strokeWidth: 2 }}
            />
          </AreaChart>
        </ResponsiveContainer>
      )}
    </div>
  </div>
);
