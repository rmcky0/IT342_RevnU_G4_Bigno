import React, { useEffect } from "react";
import { X, FileText } from "lucide-react";
import { API_BASE_URL } from "../api/axios";

export const ReceiptLightbox = ({ fileId, onClose }) => {
  useEffect(() => {
    if (!fileId) return;
    const onKey = (e) => { if (e.key === "Escape") onClose(); };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [fileId, onClose]);

  if (!fileId) return null;

  return (
    <div
      className="fixed inset-0 bg-black/85 backdrop-blur-sm z-[200] flex items-center justify-center p-4 animate-in fade-in duration-200"
      onClick={onClose}
    >
      <button
        onClick={onClose}
        className="absolute top-5 right-5 p-2.5 bg-white/10 hover:bg-white/25 text-white rounded-full transition-colors"
      >
        <X className="w-5 h-5" />
      </button>

      <div
        className="relative max-w-3xl w-full flex flex-col items-center gap-3"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center gap-2 text-white/60 text-xs font-bold uppercase tracking-widest">
          <FileText className="w-3.5 h-3.5" /> Receipt
        </div>
        <img
          src={`${API_BASE_URL}/files/${fileId}`}
          alt="Receipt"
          className="max-h-[80vh] w-auto object-contain rounded-xl shadow-2xl border border-white/10"
        />
      </div>
    </div>
  );
};
