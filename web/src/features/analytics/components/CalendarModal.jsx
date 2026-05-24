import React from "react";
import { Calendar, X, ChevronLeft, ChevronRight } from "lucide-react";
import { useHolidays } from "../hooks/useHolidays";

const MONTH_NAMES = [
  "January","February","March","April","May","June",
  "July","August","September","October","November","December",
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
      className="fixed inset-0 z-50 flex items-center justify-center bg-[#1e1b4b]/40 backdrop-blur-[3px] p-4 animate-in fade-in duration-200"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div className="bg-white rounded-2xl shadow-2xl border border-gray-100 w-full max-w-105 overflow-hidden flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-200">

        {/* Header */}
        <div className="flex items-center justify-between px-6 py-5 border-b border-gray-100">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-[#7c83fd]/10 flex items-center justify-center">
              <Calendar className="w-4.5 h-4.5 text-[#7c83fd]" />
            </div>
            <span className="font-bold text-[#1e1b4b] text-base tracking-tight">PH Holidays</span>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-xl hover:bg-gray-100 text-gray-400 transition-colors"
          >
            <X className="w-4.5 h-4.5" />
          </button>
        </div>

        <div className="p-6">
          {/* Month nav */}
          <div className="flex items-center justify-between mb-5">
            <button
              onClick={prevMonth}
              className="p-2 rounded-xl hover:bg-gray-100 text-gray-500 transition-colors"
            >
              <ChevronLeft className="w-4.5 h-4.5" />
            </button>
            <div className="flex items-center gap-2">
              <span className="font-bold text-[#1e1b4b] tracking-tight text-base">
                {MONTH_NAMES[viewMonth]} {viewYear}
              </span>
              {loading && (
                <span className="text-[10px] font-bold text-[#7c83fd] bg-[#f0f1ff] px-2 py-0.5 rounded-full border border-[#d6d9ff] animate-pulse">
                  …
                </span>
              )}
            </div>
            <button
              onClick={nextMonth}
              className="p-2 rounded-xl hover:bg-gray-100 text-gray-500 transition-colors"
            >
              <ChevronRight className="w-4.5 h-4.5" />
            </button>
          </div>

          {/* Day headers */}
          <div className="grid grid-cols-7 mb-2">
            {DAY_NAMES.map((d) => (
              <div key={d} className="text-center text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                {d}
              </div>
            ))}
          </div>

          {/* Day cells */}
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
                  className={`relative flex items-center justify-center h-10 w-full rounded-xl text-sm font-bold cursor-default transition-all ${
                    isToday
                      ? "bg-[#7c83fd] text-white shadow-md shadow-[#7c83fd]/30"
                      : isHoliday
                        ? "bg-amber-50 text-amber-600 border border-amber-200"
                        : "text-gray-600 hover:bg-gray-100"
                  }`}
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

        {/* Holiday list */}
        <div className="border-t border-gray-100 bg-gray-50/60 px-6 py-4 flex flex-col gap-2 overflow-y-auto">
          {monthHolidays.length > 0 ? (
            monthHolidays.map(({ date, name }) => (
              <div
                key={date}
                className="flex items-center gap-3 bg-white px-3 py-2.5 rounded-xl border border-gray-100 shadow-sm"
              >
                <span className="text-[10px] font-bold text-amber-600 bg-amber-50 px-2.5 py-1 rounded-lg border border-amber-100 shrink-0">
                  {new Date(date + "T00:00:00").toLocaleDateString("en-PH", {
                    day: "numeric",
                    month: "short",
                  })}
                </span>
                <span className="text-sm text-[#1e1b4b] font-semibold truncate">{name}</span>
              </div>
            ))
          ) : (
            <p className="text-center text-[11px] font-medium text-gray-400 py-2">
              No holidays this month
            </p>
          )}
        </div>
      </div>
    </div>
  );
};
