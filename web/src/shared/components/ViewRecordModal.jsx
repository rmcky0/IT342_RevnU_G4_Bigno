import React from "react";
import { X, FileText } from "lucide-react";

export const ViewRecordModal = ({
  isOpen,
  onClose,
  item,
  title = "Record details",
  amountColor = "text-[#7c83fd]",
  onViewReceipt,
}) => {
  if (!isOpen || !item) return null;

  const formatDateTime = (dateString) =>
    new Date(dateString).toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });

  const rows = [
    {
      label: "Date & time",
      value: formatDateTime(item.createdAt || item.saleDate || item.expenseDate),
      render: (v) => <span className="text-xs text-[#1e1b4b] font-medium">{v}</span>,
    },
    {
      label: "Category",
      value: item.categoryName,
      render: (v) =>
        v ? (
          <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full bg-[#eef0ff] text-[#6b72f5] border border-[#d6d9ff]">
            {v}
          </span>
        ) : (
          <span className="text-xs text-gray-300">—</span>
        ),
    },
    {
      label: "Notes",
      value: item.notes,
      render: (v) => (
        <span className="text-xs text-gray-500 text-right max-w-[60%] leading-relaxed">
          {v || "—"}
        </span>
      ),
    },
  ];

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/40 backdrop-blur-[3px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-sm w-full border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-200 shadow-2xl">

        {/* Header */}
        <div className="px-6 py-5 border-b border-gray-100 flex items-center justify-between">
          <h3 className="text-base font-bold text-[#1e1b4b]">{title}</h3>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-xl transition-colors"
            aria-label="Close"
          >
            <X className="w-4.5 h-4.5" />
          </button>
        </div>

        {/* Body */}
        <div className="p-6 flex flex-col gap-4">
          {/* Amount hero */}
          <div>
            <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">
              Amount
            </p>
            <p className={`text-2xl font-bold tabular-nums tracking-tight ${amountColor}`}>
              ₱{parseFloat(item.amount).toLocaleString(undefined, { minimumFractionDigits: 2 })}
            </p>
          </div>

          {/* Detail rows */}
          <div className="border-t border-gray-100">
            {rows.map(({ label, value, render }) => (
              <div
                key={label}
                className="flex items-start justify-between gap-4 py-2.5 border-b border-gray-50 last:border-b-0"
              >
                <span className="text-[11px] text-gray-400 shrink-0">{label}</span>
                {render(value)}
              </div>
            ))}
          </div>

          {/* Receipt button — expenses only */}
          {item.fileId && onViewReceipt && (
            <button
              onClick={() => onViewReceipt(item.fileId)}
              className="w-full flex items-center justify-center gap-1.5 py-2.5 bg-[#eef0ff] text-[#6b72f5] border border-[#d6d9ff] rounded-xl text-sm font-bold hover:bg-[#e5e7ff] transition-colors"
            >
              <FileText className="w-3.5 h-3.5" />
              View attached receipt
            </button>
          )}

          <button
            onClick={onClose}
            className="w-full py-2.5 rounded-xl border border-gray-200 text-gray-500 text-sm font-semibold hover:bg-gray-50 transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};
