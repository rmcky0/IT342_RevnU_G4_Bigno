import React, {
  createContext,
  useContext,
  useState,
  useCallback,
  useEffect,
} from "react";
import { CheckCircle2, XCircle, Info, X } from "lucide-react";

const ToastContext = createContext(null);

const CONFIGS = {
  success: {
    Icon: CheckCircle2,
    iconCls: "text-emerald-500",
    accentCls: "border-l-emerald-500",
  },
  error: {
    Icon: XCircle,
    iconCls: "text-red-500",
    accentCls: "border-l-red-500",
  },
  info: {
    Icon: Info,
    iconCls: "text-[#7c83fd]",
    accentCls: "border-l-[#7c83fd]",
  },
};

const DURATION = 4500;

const ToastItem = ({ id, type, message, onRemove }) => {
  const config = CONFIGS[type] ?? CONFIGS.info;
  const { Icon } = config;
  const [exiting, setExiting] = useState(false);

  const dismiss = useCallback(() => {
    setExiting(true);
    setTimeout(() => onRemove(id), 280);
  }, [id, onRemove]);

  useEffect(() => {
    const t = setTimeout(dismiss, DURATION);
    return () => clearTimeout(t);
  }, [dismiss]);

  return (
    <div
      className={`flex items-start gap-3 w-80 max-w-[calc(100vw-2rem)] bg-white rounded-xl border border-gray-100 border-l-4 ${config.accentCls} shadow-[0_8px_32px_rgb(0,0,0,0.14)] px-4 py-3.5 transition-all duration-280 ${
        exiting ? "opacity-0 translate-x-3" : "opacity-100 translate-x-0"
      }`}
      style={{ transitionDuration: "280ms" }}
    >
      <Icon className={`w-5 h-5 shrink-0 mt-0.5 ${config.iconCls}`} />
      <p className="flex-1 text-[13px] font-semibold text-[#1e1b4b] leading-snug">
        {message}
      </p>
      <button
        onClick={dismiss}
        className="p-0.5 text-gray-300 hover:text-gray-500 transition-colors shrink-0 ml-1"
        aria-label="Dismiss"
      >
        <X className="w-3.5 h-3.5" />
      </button>
    </div>
  );
};

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const showToast = useCallback((type, message) => {
    const id =
      typeof crypto !== "undefined" && crypto.randomUUID
        ? crypto.randomUUID()
        : Date.now() + "-" + Math.random();
    setToasts((prev) => [...prev.slice(-3), { id, type, message }]);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div
        className="fixed bottom-4 right-4 z-[200] flex flex-col gap-2 pointer-events-none"
        aria-live="polite"
        aria-atomic="false"
      >
        {toasts.map((t) => (
          <div key={t.id} className="pointer-events-auto animate-in slide-in-from-bottom-2 fade-in duration-300">
            <ToastItem {...t} onRemove={removeToast} />
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast must be used within ToastProvider");
  return ctx;
};
