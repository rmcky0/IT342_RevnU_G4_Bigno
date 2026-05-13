import React from "react";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";
import { EmptyChart } from "./EmptyChart";

export const SalesByTagChart = ({ tags = [] }) => (
  <div className="bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0">
    <div className="px-6 py-4 border-b border-gray-50 shrink-0">
      <h3 className="text-sm font-bold text-[#1e1b4b] uppercase tracking-wider">
        Sales by Tag
      </h3>
    </div>

    <div className="flex-1 p-5 min-h-0 flex flex-col">
      <div className="flex-1 min-h-0 relative">
        {tags.length === 0 ? (
          <EmptyChart message="Tag your sales to view distribution." />
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={tags}
                innerRadius="60%"
                outerRadius="80%"
                paddingAngle={4}
                dataKey="value"
              >
                {tags.map((entry, index) => (
                  <Cell
                    key={entry.name ?? index}
                    fill={entry.color}
                    stroke="none"
                  />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{
                  borderRadius: "12px",
                  border: "1px solid #f1f5f9",
                  boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)",
                  padding: "8px 12px",
                }}
                itemStyle={{
                  fontWeight: "bold",
                  color: "#1e1b4b",
                  fontSize: "12px",
                }}
                formatter={(value) => [
                  `₱${Number(value).toLocaleString("en-PH", { minimumFractionDigits: 2 })}`,
                ]}
              />
            </PieChart>
          </ResponsiveContainer>
        )}
      </div>

      {tags.length > 0 && (
        <div className="grid grid-cols-2 gap-2 pt-4 shrink-0">
          {tags.map((tag) => (
            <div
              key={tag.name}
              className="flex items-center gap-2 bg-gray-50/80 px-2 py-1.5 rounded-lg border border-gray-100"
            >
              <div
                className="w-2.5 h-2.5 rounded shrink-0 shadow-sm"
                style={{ backgroundColor: tag.color }}
              />
              <span className="text-[10px] font-bold text-gray-600 uppercase tracking-wider truncate">
                {tag.name}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  </div>
);
