import React from "react";
import { Trash2 } from "lucide-react";

export const DeleteConfirmModal = ({
  item,
  onClose,
  onConfirm,
  type = "record",
}) => {
  if (!item) return null;

  const getMessage = () => {
    if (item.fullname) {
      return (
        <>
          This will permanently remove{" "}
          <span className="font-semibold text-[#1e1b4b]">{item.fullname}</span>{" "}
          from the directory. This action cannot be undone.
        </>
      );
    }
    if (item.amount) {
      return (
        <>
          This will permanently delete the {type.toLowerCase()} for{" "}
          <span className="font-semibold text-[#1e1b4b]">
            ₱
            {parseFloat(item.amount).toLocaleString(undefined, {
              minimumFractionDigits: 2,
            })}
          </span>
          . This action cannot be undone.
        </>
      );
    }
    return (
      <>
        This will permanently delete this {type.toLowerCase()}. This action
        cannot be undone.
      </>
    );
  };

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/20 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-150">
      <div className="bg-white rounded-2xl max-w-sm w-full border border-gray-100 overflow-hidden animate-in zoom-in-95 duration-150">
        <div className="p-5 flex flex-col items-center text-center">
          {/* Icon — square, matches modal's rounded-2xl language */}
          <div className="w-10 h-10 bg-red-50 border border-red-100 rounded-xl flex items-center justify-center mb-3">
            <Trash2 className="w-5 h-5 text-red-500" />
          </div>

          <h3 className="text-[15px] font-semibold text-[#1e1b4b] mb-1.5 capitalize">
            Delete {type}
          </h3>
          <p className="text-[12px] text-gray-400 leading-relaxed mb-5 max-w-[260px]">
            {getMessage()}
          </p>

          <div className="flex gap-2.5 w-full">
            <button
              onClick={onClose}
              className="flex-1 py-2.5 bg-gray-50 hover:bg-gray-100 text-gray-600 border border-gray-200 rounded-lg text-[13px] font-semibold transition-colors"
            >
              Cancel
            </button>
            <button
              onClick={() => onConfirm(item.id)}
              className="flex-1 py-2.5 bg-red-500 hover:bg-red-600 text-white rounded-lg text-[13px] font-semibold transition-colors"
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
