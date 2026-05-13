import React from "react";
import { Trash2 } from "lucide-react";

export const DeleteConfirmModal = ({
  item,
  onClose,
  onConfirm,
  type = "record",
}) => {
  if (!item) return null;

  const renderMessageContent = () => {
    if (item.fullname) {
      return (
        <>
          Are you sure you want to remove{" "}
          <span className="font-bold text-[#1e1b4b] break-all">
            {item.fullname}
          </span>{" "}
          from the directory?
        </>
      );
    }

    if (item.amount) {
      return (
        <>
          Are you sure you want to delete this {type.toLowerCase()} for{" "}
          <span className="font-bold text-[#1e1b4b]">
            ₱
            {parseFloat(item.amount).toLocaleString(undefined, {
              minimumFractionDigits: 2,
            })}
          </span>
          ?
        </>
      );
    }

    return <>Are you sure you want to delete this {type.toLowerCase()}?</>;
  };

  return (
    <div className="fixed inset-0 bg-[#1e1b4b]/30 backdrop-blur-[2px] flex items-center justify-center z-50 p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-2xl max-w-sm w-full shadow-2xl relative animate-in zoom-in-95 duration-200 border border-gray-100 overflow-hidden">
        <div className="p-6 text-center">
          <div className="w-14 h-14 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto mb-4 border border-red-100">
            <Trash2 className="w-7 h-7" />
          </div>
          <h3 className="text-lg font-bold text-[#1e1b4b] mb-2 capitalize">
            Delete {type}
          </h3>
          <p className="text-sm text-gray-500 mb-6">
            {renderMessageContent()} This action cannot be undone.
          </p>
          <div className="flex gap-3">
            <button
              onClick={onClose}
              className="flex-1 py-2.5 bg-gray-50 text-gray-600 rounded-lg font-semibold text-sm hover:bg-gray-100 transition-colors border border-gray-200"
            >
              Cancel
            </button>
            <button
              onClick={() => onConfirm(item.id)}
              className="flex-1 py-2.5 bg-red-500 text-white rounded-lg font-semibold text-sm hover:bg-red-600 shadow-md shadow-red-200 transition-colors"
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
