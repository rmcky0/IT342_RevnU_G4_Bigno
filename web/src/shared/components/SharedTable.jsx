import React from "react";
import {
  ChevronUp,
  ChevronDown,
  Eye,
  Edit2,
  Trash2,
  FileText,
  FilePlus,
  Inbox,
} from "lucide-react";

export const SharedTable = ({
  data,
  loading,
  searchTerm,
  onClearSearch,
  sortConfig,
  requestSort,
  onView,
  onEdit,
  onDelete,
  onViewReceipt,
  type = "sales",
}) => {
  const isExpense = type === "expenses";

  const theme = {
    amountColor: isExpense ? "text-red-500" : "text-[#7c83fd]",
    rowHover: isExpense
      ? "hover:shadow-[inset_3px_0_0_0_#ef4444]"
      : "hover:shadow-[inset_3px_0_0_0_#7c83fd]",
    emptyMsg: isExpense
      ? "Your expense ledger is currently empty."
      : "Your sales ledger is currently empty.",
  };

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  };

  const SortableHeader = ({ label, sortKey, align = "left" }) => {
    const isActive = sortConfig?.key === sortKey;
    return (
      <th
        onClick={() => requestSort(sortKey)}
        className={`px-5 py-3 font-semibold text-xs tracking-wider uppercase cursor-pointer hover:bg-white/10 transition-colors select-none ${align === "right" ? "text-right" : "text-left"}`}
      >
        <div
          className={`flex items-center gap-1.5 ${align === "right" ? "justify-end" : "justify-start"}`}
        >
          {label}
          <div className="flex flex-col">
            <ChevronUp
              className={`w-2.5 h-2.5 -mb-1 ${isActive && sortConfig.direction === "asc" ? "text-white" : "text-indigo-200"}`}
            />
            <ChevronDown
              className={`w-2.5 h-2.5 ${isActive && sortConfig.direction === "desc" ? "text-white" : "text-indigo-200"}`}
            />
          </div>
        </div>
      </th>
    );
  };

  return (
    <div className="flex-1 bg-white rounded-xl shadow-[0_4px_20px_rgb(0,0,0,0.03)] border border-gray-100 flex flex-col min-h-0 overflow-hidden relative">
      <div className="flex-1 overflow-auto custom-scrollbar">
        <table className="w-full text-left border-collapse whitespace-nowrap">
          <thead className="bg-gradient-to-r from-[#8f9df7] to-[#9faaf5] text-white sticky top-0 z-10 shadow-sm">
            <tr>
              <SortableHeader label="Date & Time" sortKey="date" />
              <SortableHeader label="Amount" sortKey="amount" align="right" />
              <th className="px-5 py-3 font-semibold text-xs tracking-wider uppercase w-48">
                Tags
              </th>
              <th className="px-5 py-3 font-semibold text-xs tracking-wider uppercase w-full">
                Notes
              </th>
              <th className="px-5 py-3 font-semibold text-xs tracking-wider uppercase text-center">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-50 relative">
            {/* Loading Skeleton */}
            {loading ? (
              Array.from({ length: 8 }).map((_, idx) => (
                <tr key={`skel-${idx}`} className="animate-pulse">
                  <td className="px-5 py-4">
                    <div className="h-3.5 bg-gray-100 rounded w-28"></div>
                  </td>
                  <td className="px-5 py-4 flex justify-end">
                    <div className="h-3.5 bg-gray-100 rounded w-20"></div>
                  </td>
                  <td className="px-5 py-4">
                    <div className="h-5 bg-gray-100 rounded-md w-16"></div>
                  </td>
                  <td className="px-5 py-4">
                    <div className="h-3.5 bg-gray-100 rounded w-40"></div>
                  </td>
                  <td className="px-5 py-4">
                    <div className="flex justify-center gap-2">
                      <div className="w-6 h-6 bg-gray-100 rounded-md"></div>
                    </div>
                  </td>
                </tr>
              ))
            ) : data.length === 0 ? (
              /* Empty State */
              <tr>
                <td colSpan="5" className="px-5 py-16 text-center">
                  <div className="flex flex-col items-center justify-center max-w-sm mx-auto animate-in fade-in zoom-in-95 duration-300">
                    <div className="w-16 h-16 bg-gradient-to-br from-indigo-50 to-gray-50 rounded-2xl flex items-center justify-center mb-4 shadow-inner border border-white">
                      <Inbox className="w-8 h-8 text-[#7c83fd]" />
                    </div>
                    <h3 className="text-lg font-bold text-[#1e1b4b] mb-1">
                      No records found
                    </h3>
                    <p className="text-sm text-gray-500 mb-5">
                      {searchTerm
                        ? "We couldn't find any records matching your search criteria."
                        : theme.emptyMsg}
                    </p>
                    {searchTerm && (
                      <button
                        onClick={onClearSearch}
                        className="px-5 py-2 bg-white border border-gray-200 text-gray-600 rounded-lg text-sm font-semibold shadow-sm hover:bg-gray-50 hover:text-[#1e1b4b] transition-all"
                      >
                        Clear Filters
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ) : (
              /* Actual Data Rows */
              data.map((item) => (
                <tr
                  key={item.id}
                  className={`group hover:bg-[#f8f9ff] transition-all duration-200 ${theme.rowHover}`}
                >
                  <td className="px-5 py-4 text-sm font-medium text-gray-600">
                    {formatDateTime(
                      item.createdAt || item.saleDate || item.expenseDate,
                    )}
                  </td>
                  <td
                    className={`px-5 py-4 text-sm font-bold text-right ${theme.amountColor}`}
                  >
                    ₱
                    {parseFloat(item.amount).toLocaleString(undefined, {
                      minimumFractionDigits: 2,
                    })}
                  </td>
                  <td className="px-5 py-4 max-w-[200px]">
                    <div className="flex items-center gap-1.5 overflow-hidden">
                      {item.tags && item.tags.length > 0 ? (
                        <>
                          {item.tags.slice(0, 2).map((tag, i) => (
                            <span
                              key={i}
                              className="px-2 py-0.5 bg-indigo-50 border border-indigo-100/50 text-[#7c83fd] text-[10px] font-bold uppercase rounded-md shadow-sm whitespace-nowrap"
                            >
                              {tag}
                            </span>
                          ))}
                          {item.tags.length > 2 && (
                            <span className="text-[10px] text-gray-400 font-semibold whitespace-nowrap">
                              +{item.tags.length - 2}
                            </span>
                          )}
                        </>
                      ) : (
                        <span className="text-xs text-gray-400 italic">
                          None
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="px-5 py-4 text-sm text-gray-500 truncate max-w-[200px] group-hover:text-gray-700 transition-colors">
                    {item.description || "-"}
                  </td>
                  <td className="px-5 py-4">
                    <div className="flex justify-center items-center gap-1 opacity-0 group-hover:opacity-100 transition-all duration-200 transform translate-x-2 group-hover:translate-x-0">
                      {/* View Action */}
                      <button
                        onClick={() => onView(item)}
                        className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-md transition-colors"
                        title="View Details"
                      >
                        <Eye className="w-4 h-4" />
                      </button>

                      {/* Expense-Specific Receipt Actions */}
                      {isExpense && (
                        <>
                          {item.fileId ? (
                            <button
                              onClick={() => onViewReceipt(item.fileId)}
                              className="p-1.5 text-[#7c83fd] hover:bg-indigo-50 rounded-md transition-colors"
                              title="View Receipt"
                            >
                              <FileText className="w-4 h-4" />
                            </button>
                          ) : (
                            <button
                              onClick={() => onEdit(item)}
                              className="p-1.5 text-gray-400 hover:bg-gray-100 rounded-md transition-colors"
                              title="Add Receipt"
                            >
                              <FilePlus className="w-4 h-4" />
                            </button>
                          )}
                          <div className="w-px h-3 bg-gray-200 mx-1" />
                        </>
                      )}

                      {/* Edit & Delete Actions */}
                      <button
                        onClick={() => onEdit(item)}
                        className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-indigo-50 rounded-md transition-colors"
                        title="Edit"
                      >
                        <Edit2 className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => onDelete(item)}
                        className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-md transition-colors"
                        title="Delete"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
