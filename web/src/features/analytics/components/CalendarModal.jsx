import React from "react";
import { Calendar, X, ChevronLeft, ChevronRight } from "lucide-react";
import { useHolidays } from "../hooks/useHolidays";

const MONTH_NAMES = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];
const DAY_NAMES = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

export const CalendarModal = ({ onClose }) => {
  const {
    viewYear,
    viewMonth,
    cells,
    monthHolidays,
    yearHolidays,
    loading,
    prevMonth,
    nextMonth,
    today,
  } = useHolidays();

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-[#1e1b4b]/30 backdrop-blur-[2px] p-4"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div className="bg-white rounded-2xl shadow-2xl border border-gray-100 w-full max-w-[400px] overflow-hidden flex flex-col max-h-full">
        {/* Header */}
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50 bg-gray-50/50">
          <div className="flex items-center gap-2">
            <Calendar className="w-4 h-4 text-[#7c83fd]" />
            <span className="font-bold text-[#1e1b4b] text-sm tracking-wide">
              PH Holidays
            </span>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-gray-200 text-gray-400"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-5">
          {/* Controls */}
          <div className="flex items-center justify-between mb-4">
            <button
              onClick={prevMonth}
              className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-500"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <div className="flex items-center gap-2">
              <span className="font-bold text-[#1e1b4b]">
                {MONTH_NAMES[viewMonth]} {viewYear}
              </span>
              {loading && (
                <span className="text-[10px] font-bold text-[#7c83fd] bg-indigo-50 px-2 py-0.5 rounded-md">
                  ...
                </span>
              )}
            </div>
            <button
              onClick={nextMonth}
              className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-500"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>

          {/* Grid Headers */}
          <div className="grid grid-cols-7 mb-2">
            {DAY_NAMES.map((d) => (
              <div
                key={d}
                className="text-center text-[10px] font-bold text-gray-400 uppercase"
              >
                {d}
              </div>
            ))}
          </div>

          {/* Days */}
          <div className="grid grid-cols-7 gap-1">
            {cells.map((day, idx) => {
              if (!day) return <div key={`empty-${idx}`} />;
              const dateStr = `${viewYear}-${String(viewMonth + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
              const isHoliday = !!yearHolidays[dateStr];
              const isToday =
                day === today.getDate() &&
                viewMonth === today.getMonth() &&
                viewYear === today.getFullYear();

              return (
                <div
                  key={dateStr}
                  title={isHoliday ? yearHolidays[dateStr] : undefined}
                  className={`relative flex items-center justify-center h-9 w-full rounded-lg text-sm font-semibold cursor-default ${isToday ? "bg-[#7c83fd] text-white" : isHoliday ? "bg-amber-50 text-amber-600 border border-amber-100" : "text-gray-600 hover:bg-gray-50"}`}
                >
                  {day}
                  {isHoliday && !isToday && (
                    <span className="absolute bottom-1 w-1 h-1 rounded-full bg-amber-400" />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* Holiday List */}
        <div className="border-t border-gray-100 bg-gray-50/50 px-5 py-4 space-y-2 overflow-y-auto">
          {monthHolidays.length > 0 ? (
            monthHolidays.map(({ date, name }) => (
              <div
                key={date}
                className="flex items-center gap-3 bg-white p-2 rounded-lg border border-gray-100 shadow-sm"
              >
                <span className="text-[10px] font-bold text-amber-600 bg-amber-50 px-2 py-1 rounded-md">
                  {new Date(date + "T00:00:00").toLocaleDateString("en-PH", {
                    day: "numeric",
                    month: "short",
                  })}
                </span>
                <span className="text-xs text-gray-700 font-medium truncate">
                  {name}
                </span>
              </div>
            ))
          ) : (
            <div className="text-center text-xs text-gray-400 italic">
              No holidays this month
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
