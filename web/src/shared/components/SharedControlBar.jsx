import React from "react";
import {
  Search,
  X,
  Filter,
  ChevronLeft,
  ChevronRight,
  Plus,
} from "lucide-react";

export const SharedControlBar = ({
  searchTerm,
  setSearchTerm,
  currentPage,
  totalPages,
  handlePrevPage,
  handleNextPage,
  onAddClick,
  isLocked = false,
  type = "sales",
}) => {
  const getButtonLabel = () => {
    switch (type) {
      case "expenses":
        return "Add Expense";
      case "staff":
        return "Add Staff";
      case "salary":
        return "Record Salary";
      case "sales":
      default:
        return "Add Sale";
    }
  };

  const addButtonLabel = getButtonLabel();

  return (
    <div className="shrink-0 flex items-center justify-between gap-3 bg-white p-2.5 rounded-xl shadow-[0_2px_12px_rgb(0,0,0,0.02)] border border-gray-100">
      {/* Search & Filter Left Side */}
      <div className="flex items-center gap-2 flex-1 max-w-md">
        <div className="relative flex-1 group">
          <Search
            className={`absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 transition-colors ${
              searchTerm
                ? "text-[#7c83fd]"
                : "text-gray-400 group-focus-within:text-[#7c83fd]"
            }`}
          />
          <input
            type="text"
            placeholder="Search records..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className={`w-full pl-9 pr-8 py-2 rounded-lg border border-transparent text-sm transition-all outline-none ${
              searchTerm
                ? "bg-indigo-50/50 text-[#7c83fd] focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50"
                : "bg-gray-50/50 text-[#1e1b4b] placeholder:text-gray-400 hover:bg-gray-50 focus:bg-white focus:border-[#7c83fd] focus:ring-4 focus:ring-indigo-50"
            }`}
          />
          {searchTerm && (
            <button
              onClick={() => setSearchTerm("")}
              className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 transition-colors"
              title="Clear search"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>
        <button
          className="p-2 bg-gray-50/50 rounded-lg text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 transition-colors border border-transparent"
          title="Filter Options"
        >
          <Filter className="w-4 h-4" />
        </button>
      </div>

      {/* Pagination & Add Button Right Side */}
      <div className="flex items-center gap-3">
        <div className="flex items-center bg-gray-50/50 rounded-lg border border-gray-100">
          <span className="text-xs font-semibold text-gray-500 px-3 border-r border-gray-100 min-w-[5rem] text-center">
            {currentPage} / {totalPages || 1}
          </span>
          <div className="flex">
            <button
              onClick={handlePrevPage}
              disabled={currentPage === 1}
              className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-gray-100 disabled:opacity-30 disabled:hover:bg-transparent transition-colors rounded-l-none rounded-r-none"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              onClick={handleNextPage}
              disabled={currentPage === totalPages || totalPages === 0}
              className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-gray-100 disabled:opacity-30 disabled:hover:bg-transparent transition-colors rounded-r-lg"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
        <button
          onClick={onAddClick}
          disabled={isLocked}
          className="flex items-center gap-1.5 px-4 py-2 bg-[#7c83fd] text-white rounded-lg font-semibold text-sm shadow-md shadow-indigo-100 hover:bg-[#6b72f5] hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
        >
          <Plus className="w-4 h-4" /> {addButtonLabel}
        </button>
      </div>
    </div>
  );
};
