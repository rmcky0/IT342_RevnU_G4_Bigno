import { useEffect, useRef, useState } from "react";
import { Bell, CheckCheck, Calendar, Info, Inbox } from "lucide-react";
import { useNotifications } from "../hooks/useNotifications";

const typeIcon = (note) => {
  if (note.type === "HOLIDAY" || note.title?.toLowerCase().includes("holiday"))
    return <Calendar className="w-3.5 h-3.5 text-amber-500 shrink-0 mt-px" />;
  return <Info className="w-3.5 h-3.5 text-[#7c83fd] shrink-0 mt-px" />;
};

export const NotificationBell = ({ holidayName, holidayDate }) => {
  const [open, setOpen] = useState(false);
  const panelRef = useRef(null);
  const buttonRef = useRef(null);

  const { notifications, unreadCount, loading, markAllRead, markRead, ensureHolidayAlert } =
    useNotifications({ limit: 15 });

  useEffect(() => {
    if (holidayName && holidayDate) ensureHolidayAlert(holidayDate, holidayName);
  }, [holidayName, holidayDate, ensureHolidayAlert]);

  useEffect(() => {
    const handler = (e) => {
      if (
        panelRef.current &&
        !panelRef.current.contains(e.target) &&
        buttonRef.current &&
        !buttonRef.current.contains(e.target)
      ) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div className="relative">
      {/* Bell button */}
      <button
        ref={buttonRef}
        onClick={() => setOpen((p) => !p)}
        className={`relative p-2 rounded-xl transition-colors ${
          open
            ? "bg-[#f0f1ff] text-[#7c83fd]"
            : "text-gray-400 hover:text-[#7c83fd] hover:bg-[#f0f1ff]"
        }`}
        aria-label="Notifications"
      >
        <Bell className="w-4 h-4" />
        {unreadCount > 0 && (
          <span className="absolute -top-0.5 -right-0.5 min-w-4 h-4 px-0.5 flex items-center justify-center bg-red-500 text-white text-[9px] font-black rounded-full border-2 border-white leading-none">
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown panel */}
      {open && (
        <div
          ref={panelRef}
          className="absolute right-0 mt-2 w-80 bg-white rounded-xl border border-gray-100 shadow-2xl z-50 overflow-hidden animate-in slide-in-from-top-2 fade-in duration-150"
        >
          {/* Header */}
          <div className="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Bell className="w-3.5 h-3.5 text-[#7c83fd]" />
              <span className="text-[13px] font-bold text-[#1e1b4b]">Notifications</span>
              {unreadCount > 0 && (
                <span className="text-[10px] font-bold text-[#7c83fd] bg-[#f0f1ff] border border-[#d6d9ff] rounded-full px-1.5 py-0.5 leading-none">
                  {unreadCount}
                </span>
              )}
            </div>
            {unreadCount > 0 && (
              <button
                onClick={markAllRead}
                className="inline-flex items-center gap-1 text-[10px] font-bold text-gray-400 hover:text-[#7c83fd] uppercase tracking-wider transition-colors"
              >
                <CheckCheck className="w-3 h-3" /> Mark all read
              </button>
            )}
          </div>

          {/* List */}
          <div className="max-h-72 overflow-y-auto divide-y divide-gray-50">
            {loading && (
              <div className="flex flex-col gap-2.5 p-4">
                {Array.from({ length: 3 }).map((_, i) => (
                  <div key={i} className="animate-pulse space-y-1.5">
                    <div className="h-3 bg-gray-100 rounded w-2/3" />
                    <div className="h-2.5 bg-gray-100 rounded w-full" />
                  </div>
                ))}
              </div>
            )}

            {!loading && notifications.length === 0 && (
              <div className="flex flex-col items-center justify-center py-10 px-4">
                <div className="w-10 h-10 bg-gray-50 rounded-xl flex items-center justify-center mb-2.5">
                  <Inbox className="w-5 h-5 text-gray-300" />
                </div>
                <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest">
                  No notifications yet
                </p>
              </div>
            )}

            {!loading &&
              notifications.map((note) => (
                <div
                  key={note.id}
                  onClick={() => { if (!note.read) markRead(note.id); }}
                  className={`flex gap-2.5 px-4 py-3 cursor-pointer transition-colors hover:bg-gray-50 ${
                    !note.read ? "bg-[#f8f9ff]" : ""
                  }`}
                >
                  <div className="mt-0.5 shrink-0">{typeIcon(note)}</div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2 mb-0.5">
                      <p className={`text-[12px] leading-tight truncate ${note.read ? "font-semibold text-gray-500" : "font-bold text-[#1e1b4b]"}`}>
                        {note.title}
                      </p>
                      <span className="text-[9px] text-gray-400 font-bold uppercase tracking-wider shrink-0 mt-px">
                        {note.relativeTime}
                      </span>
                    </div>
                    <p className="text-[11px] text-gray-400 leading-relaxed line-clamp-2">
                      {note.message}
                    </p>
                  </div>
                  {!note.read && (
                    <div className="w-1.5 h-1.5 rounded-full bg-[#7c83fd] shrink-0 mt-1.5" />
                  )}
                </div>
              ))}
          </div>
        </div>
      )}
    </div>
  );
};
