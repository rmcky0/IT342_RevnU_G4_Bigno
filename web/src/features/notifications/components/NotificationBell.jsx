import { useEffect, useState } from "react";
import { Bell, CheckCircle2 } from "lucide-react";
import { useNotifications } from "../hooks/useNotifications";

export const NotificationBell = ({ holidayName, holidayDate }) => {
  const [open, setOpen] = useState(false);
  const {
    notifications,
    unreadCount,
    loading,
    markAllRead,
    markRead,
    ensureHolidayAlert,
  } = useNotifications({ limit: 15 });

  useEffect(() => {
    if (holidayName && holidayDate) {
      ensureHolidayAlert(holidayDate, holidayName);
    }
  }, [holidayName, holidayDate, ensureHolidayAlert]);

  const handleItemClick = (note) => {
    if (!note.read) {
      markRead(note.id);
    }
  };

  return (
    <div className="relative">
      <button
        onClick={() => setOpen((prev) => !prev)}
        className={`p-2 rounded-lg transition-colors ${open ? "bg-indigo-50 text-[#7c83fd]" : "text-gray-400 hover:bg-gray-50 hover:text-gray-700"}`}
        aria-label="Notifications"
      >
        <Bell className="w-4 h-4" />
        {unreadCount > 0 && (
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white" />
        )}
      </button>

      {open && (
        <div className="absolute right-0 mt-3 w-80 bg-white rounded-2xl shadow-xl shadow-indigo-900/10 border border-gray-100 z-50 overflow-hidden animate-in slide-in-from-top-2 duration-200">
          <div className="px-4 py-3 border-b border-gray-50 flex justify-between items-center bg-gray-50/80 backdrop-blur-sm">
            <h4 className="font-bold text-[#1e1b4b] text-sm">Notifications</h4>
            {unreadCount > 0 && (
              <button
                onClick={markAllRead}
                className="text-[10px] text-[#7c83fd] hover:text-[#6b72f5] font-bold uppercase tracking-wider flex items-center gap-1 transition-colors"
              >
                <CheckCircle2 className="w-3 h-3" /> Mark read
              </button>
            )}
          </div>
          <div className="max-h-[280px] overflow-y-auto">
            {loading && (
              <div className="p-4 text-xs text-gray-400 font-semibold">
                Loading notifications...
              </div>
            )}
            {!loading && notifications.length === 0 && (
              <div className="p-4 text-xs text-gray-400 font-semibold">
                No notifications yet.
              </div>
            )}
            {notifications.map((note) => (
              <div
                key={note.id}
                onClick={() => handleItemClick(note)}
                className={`p-4 border-b border-gray-50 hover:bg-gray-50 transition-colors cursor-pointer ${!note.read ? "bg-indigo-50/30" : ""}`}
              >
                <div className="flex justify-between items-start mb-1">
                  <p className="text-sm font-bold text-[#1e1b4b]">
                    {note.title}
                  </p>
                  <p className="text-[10px] text-gray-400 font-bold uppercase tracking-wider mt-0.5">
                    {note.relativeTime}
                  </p>
                </div>
                <p className="text-xs text-gray-500 leading-relaxed">
                  {note.message}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
