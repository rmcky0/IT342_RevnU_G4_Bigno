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
  isLocked = false,
  type = "sales",
}) => {
  const isExpense = type === "expenses";

  const theme = {
    amountColor: isExpense ? "text-red-500" : "text-[#6b72f5]",
    chipBg: isExpense
      ? "bg-red-50 text-red-600 border-red-100"
      : "bg-[#eef0ff] text-[#6b72f5] border-[#d6d9ff]",
    emptyMsg: isExpense
      ? "No expense records yet. Add your first one to get started."
      : "No sales recorded yet. Add your first transaction to get started.",
    emptyIcon: isExpense ? "text-red-300" : "text-[#7c83fd]",
    emptyBg: isExpense ? "bg-red-50" : "bg-indigo-50",
  };

  const formatDateTime = (dateString) =>
    new Date(dateString).toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });

  const SortableHeader = ({ label, sortKey, align = "left" }) => {
    const isActive = sortConfig?.key === sortKey;
    const dir = sortConfig?.direction;
    return (
      <th
        onClick={() => requestSort(sortKey)}
        className={`px-5 py-3 text-[10px] font-bold tracking-widest uppercase cursor-pointer select-none whitespace-nowrap transition-colors hover:bg-black/3 border-b border-gray-100 ${
          align === "right" ? "text-right" : "text-left"
        } text-gray-400`}
      >
        <span className={`inline-flex items-center gap-1 ${align === "right" ? "flex-row-reverse" : ""}`}>
          {label}
          <span className="flex flex-col opacity-40">
            <ChevronUp
              className={`w-2.5 h-2.5 -mb-0.5 transition-opacity ${
                isActive && dir === "asc" ? "opacity-100 text-[#7c83fd]" : ""
              }`}
            />
            <ChevronDown
              className={`w-2.5 h-2.5 transition-opacity ${
                isActive && dir === "desc" ? "opacity-100 text-[#7c83fd]" : ""
              }`}
            />
          </span>
        </span>
      </th>
    );
  };

  return (
    <div className="flex-1 bg-white rounded-xl border border-gray-100 flex flex-col min-h-0 overflow-hidden">
      <div className="flex-1 overflow-auto">
        <table
          className="w-full border-collapse"
          style={{ tableLayout: "fixed", minWidth: "560px" }}
        >
          <colgroup>
            <col style={{ width: "160px" }} />
            <col style={{ width: "120px" }} />
            <col style={{ width: "120px" }} />
            <col style={{ width: "auto" }} />
            {isExpense && <col style={{ width: "44px" }} />}
            <col style={{ width: "100px" }} />
          </colgroup>

          <thead className="sticky top-0 z-10 bg-gray-50">
            <tr>
              <SortableHeader label="Date & time" sortKey="date" />
              <SortableHeader label="Amount" sortKey="amount" align="right" />
              <th className="px-5 py-3 text-[10px] font-bold tracking-widest uppercase text-gray-400 text-left border-b border-gray-100">
                Category
              </th>
              <th className="px-5 py-3 text-[10px] font-bold tracking-widest uppercase text-gray-400 text-left border-b border-gray-100">
                Notes
              </th>
              {isExpense && <th className="border-b border-gray-100" />}
              <th className="px-5 py-3 text-[10px] font-bold tracking-widest uppercase text-gray-400 text-right border-b border-gray-100">
                Actions
              </th>
            </tr>
          </thead>

          <tbody className="divide-y divide-gray-50">
            {/* Loading skeleton */}
            {loading &&
              Array.from({ length: 7 }).map((_, idx) => (
                <tr key={`sk-${idx}`} className="animate-pulse">
                  <td className="px-5 py-3.5">
                    <div className="h-3 bg-gray-100 rounded-full w-28" />
                  </td>
                  <td className="px-5 py-3.5">
                    <div className="h-3 bg-gray-100 rounded-full w-16 ml-auto" />
                  </td>
                  <td className="px-5 py-3.5">
                    <div className="h-4 bg-gray-100 rounded-full w-14" />
                  </td>
                  <td className="px-5 py-3.5">
                    <div className="h-3 bg-gray-100 rounded-full w-32" />
                  </td>
                  {isExpense && <td />}
                  <td className="px-5 py-3.5">
                    <div className="h-3 bg-gray-100 rounded-full w-12 ml-auto" />
                  </td>
                </tr>
              ))}

            {/* Empty state */}
            {!loading && data.length === 0 && (
              <tr>
                <td colSpan={isExpense ? 6 : 5} className="px-4 py-16 text-center">
                  <div className="flex flex-col items-center gap-3 max-w-xs mx-auto animate-in fade-in zoom-in-95 duration-300">
                    <div className={`w-14 h-14 rounded-2xl ${theme.emptyBg} flex items-center justify-center shadow-inner`}>
                      <Inbox className={`w-7 h-7 ${theme.emptyIcon}`} />
                    </div>
                    <div>
                      <p className="text-base font-bold text-[#1e1b4b] mb-1">No records found</p>
                      <p className="text-sm text-gray-400 leading-relaxed">
                        {searchTerm
                          ? "Nothing matched your search. Try a different term."
                          : theme.emptyMsg}
                      </p>
                    </div>
                    {searchTerm && (
                      <button
                        onClick={onClearSearch}
                        className="text-xs font-bold text-[#7c83fd] hover:underline underline-offset-2"
                      >
                        Clear search
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            )}

            {/* Data rows */}
            {!loading &&
              data.map((item) => (
                <tr
                  key={item.id}
                  className="group hover:bg-[#f8f9ff] transition-colors duration-100"
                >
                  <td className="px-5 py-3 text-xs text-gray-400 whitespace-nowrap overflow-hidden text-ellipsis">
                    {formatDateTime(item.createdAt || item.saleDate || item.expenseDate)}
                  </td>

                  <td className={`px-5 py-3 text-sm font-bold text-right tabular-nums whitespace-nowrap ${theme.amountColor}`}>
                    ₱{parseFloat(item.amount).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                  </td>

                  <td className="px-5 py-3">
                    {item.categoryName ? (
                      <span className={`inline-block text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full border ${theme.chipBg}`}>
                        {item.categoryName}
                      </span>
                    ) : (
                      <span className="text-xs text-gray-300">—</span>
                    )}
                  </td>

                  <td className="px-5 py-3 text-xs text-gray-400 overflow-hidden text-ellipsis max-w-0 group-hover:text-gray-600 transition-colors">
                    {item.notes || "—"}
                  </td>

                  {isExpense && (
                    <td className="px-1.5 py-3">
                      {item.fileId ? (
                        <button
                          onClick={() => onViewReceipt(item.fileId)}
                          className="p-1.5 text-[#7c83fd] hover:bg-[#eef0ff] rounded-lg transition-colors"
                          title="View receipt"
                        >
                          <FileText className="w-3.5 h-3.5" />
                        </button>
                      ) : !isLocked ? (
                        <button
                          onClick={() => onEdit(item)}
                          className="p-1.5 text-gray-300 hover:bg-gray-50 hover:text-gray-500 rounded-lg transition-colors"
                          title="Add receipt"
                        >
                          <FilePlus className="w-3.5 h-3.5" />
                        </button>
                      ) : null}
                    </td>
                  )}

                  <td className="px-5 py-3">
                    <div className="flex items-center justify-end gap-0.5 opacity-0 group-hover:opacity-100 transition-opacity duration-150">
                      <button
                        onClick={() => onView(item)}
                        className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-[#eef0ff] rounded-lg transition-colors"
                        title="View"
                      >
                        <Eye className="w-3.5 h-3.5" />
                      </button>
                      {!isLocked && (
                        <>
                          <button
                            onClick={() => onEdit(item)}
                            className="p-1.5 text-gray-400 hover:text-[#7c83fd] hover:bg-[#eef0ff] rounded-lg transition-colors"
                            title="Edit"
                          >
                            <Edit2 className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => onDelete(item)}
                            className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                            title="Delete"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
