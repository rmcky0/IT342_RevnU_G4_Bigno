import React from "react";
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip } from "recharts";
import { EmptyChart } from "./EmptyChart";

const CustomPieTooltip = ({ active, payload }) => {
  if (!active || !payload || !payload.length) return null;
  const data = payload[0];
  return (
    <div className="bg-white/95 backdrop-blur-md border border-gray-100 px-4 py-2.5 rounded-xl shadow-[0_8px_30px_rgb(0,0,0,0.08)] flex items-center gap-3">
      <div
        className="w-2 h-2 rounded-full shrink-0"
        style={{ backgroundColor: data.payload.color }}
      />
      <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">
        {data.name}
      </span>
      <span className="text-sm font-bold text-[#1e1b4b] tabular-nums ml-2">
        ₱{Number(data.value).toLocaleString("en-PH")}
      </span>
    </div>
  );
};

export const SalesByCategoryChart = ({ categories = [] }) => (
  <div className="bg-white rounded-xl shadow-[0_2px_12px_rgb(0,0,0,0.04)] border border-gray-100 flex flex-col min-h-0">
    <div className="px-5 py-3.5 border-b border-gray-100 shrink-0">
      <h3 className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">
        Sales by Category
      </h3>
    </div>

    <div className="flex-1 p-5 min-h-0 flex flex-col">
      <div className="flex-1 min-h-0 relative">
        {categories.length === 0 ? (
          <EmptyChart message="Category your sales to view distribution." />
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={categories}
                innerRadius="65%"
                outerRadius="85%"
                paddingAngle={6}
                dataKey="value"
                stroke="none"
              >
                {categories.map((entry, index) => (
                  <Cell key={entry.name ?? index} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip content={<CustomPieTooltip />} />
            </PieChart>
          </ResponsiveContainer>
        )}
      </div>

      {categories.length > 0 && (
        <div className="grid grid-cols-2 gap-1.5 pt-4 shrink-0">
          {categories.map((category) => (
            <div
              key={category.name}
              className="flex items-center gap-2 bg-gray-50 px-3 py-2 rounded-xl border border-gray-100 hover:bg-gray-100 transition-colors"
            >
              <div
                className="w-2 h-2 rounded-full shrink-0"
                style={{ backgroundColor: category.color }}
              />
              <span className="text-[10px] font-bold text-gray-500 uppercase tracking-widest truncate">
                {category.name}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  </div>
);
