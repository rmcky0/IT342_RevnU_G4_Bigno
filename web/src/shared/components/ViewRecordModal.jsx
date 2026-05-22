import React from "react";
import { X, FileText } from "lucide-react";

export const ViewRecordModal = ({
  isOpen,
  onClose,
  item,
  title = "Record Details",
  amountColor = "text-[#7c83fd]",
  onViewReceipt,
}) => {
  if (!isOpen || !item) return null;

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  };

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl relative border border-gray-100 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-100 bg-gray-50/50 flex items-center justify-between">
          <h3 className="text-lg font-bold text-[#1e1b4b]">{title}</h3>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>
        <div className="p-6 space-y-5">
          <div>
            <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider mb-1">
              Amount
            </p>
            <p className={`text-2xl font-black ${amountColor}`}>
              ₱
              {parseFloat(item.amount).toLocaleString(undefined, {
                minimumFractionDigits: 2,
              })}
            </p>
          </div>
          <div>
            <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider mb-1">
              Date & Time
            </p>
            <p className="text-sm font-semibold text-[#1e1b4b]">
              {formatDateTime(
                item.createdAt || item.saleDate || item.expenseDate,
              )}
            </p>
          </div>
          <div>
            <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider mb-2">
              Category
            </p>
            {item.categoryName ? (
              <span className="px-2.5 py-1 bg-indigo-50 border border-indigo-100/50 text-[#7c83fd] text-xs font-bold uppercase rounded-md shadow-sm">
                {item.categoryName}
              </span>
            ) : (
              <span className="text-sm text-gray-500 italic">
                No category assigned
              </span>
            )}
          </div>
          <div>
            <p className="text-[11px] font-bold text-gray-400 uppercase tracking-wider mb-1">
              Notes
            </p>
            <div className="bg-gray-50 p-3 rounded-lg border border-gray-100 text-sm text-gray-700 break-all whitespace-pre-wrap">
              {item.notes || "No notes provided."}
            </div>
          </div>

          {/* Conditionally render Receipt button for Expenses */}
          {item.fileId && onViewReceipt && (
            <div className="pt-2">
              <button
                onClick={() => onViewReceipt(item.fileId)}
                className="w-full flex items-center justify-center gap-2 py-2.5 bg-indigo-50 text-[#7c83fd] rounded-lg font-semibold text-sm hover:bg-indigo-100 transition-colors"
              >
                <FileText className="w-4 h-4" /> View Attached Receipt
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
