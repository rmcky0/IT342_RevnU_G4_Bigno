import React from "react";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { EmptyChart } from "./EmptyChart";

export const WeeklyProfitChart = ({ data = [] }) => (
  <div className="lg:col-span-2 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0">
    <div className="px-6 py-4 border-b border-gray-50 flex justify-between items-center shrink-0">
      <div>
        <h3 className="text-sm font-bold text-[#1e1b4b] uppercase tracking-wider">
          Weekly Profit Trend
        </h3>
        <p className="text-[10px] text-gray-400 font-bold uppercase tracking-wider mt-0.5">
          Last 7 closed days
        </p>
      </div>
    </div>

    <div className="flex-1 p-5 min-h-0">
      {data.length === 0 ? (
        <EmptyChart message="Complete an EOD close to track history." />
      ) : (
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart
            data={data}
            margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
          >
            <defs>
              <linearGradient id="colorProfit" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#10b981" stopOpacity={0.2} />
                <stop offset="95%" stopColor="#10b981" stopOpacity={0} />
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
              tickFormatter={(v) =>
                v >= 1000 ? `₱${(v / 1000).toFixed(1)}k` : `₱${v}`
              }
            />
            <Tooltip
              contentStyle={{
                borderRadius: "12px",
                border: "1px solid #f1f5f9",
                boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)",
              }}
              formatter={(value, name) => [
                `₱${Number(value).toLocaleString("en-PH", { minimumFractionDigits: 2 })}`,
                name === "netProfit"
                  ? "Net Profit"
                  : name === "totalSales"
                    ? "Sales"
                    : "Expenses",
              ]}
            />
            <Area
              type="monotone"
              dataKey="netProfit"
              stroke="#10b981"
              fillOpacity={1}
              fill="url(#colorProfit)"
              strokeWidth={3}
              activeDot={{ r: 5, strokeWidth: 0 }}
            />
          </AreaChart>
        </ResponsiveContainer>
      )}
    </div>
  </div>
);
