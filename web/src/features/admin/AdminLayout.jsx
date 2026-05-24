import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../auth/context/AuthContext";
import * as cache from "../../shared/cache/dataCache";
import {
  LayoutDashboard,
  Users,
  Building2,
  LogOut,
  Menu,
  X,
  ShieldAlert,
} from "lucide-react";
import revnuLogo from "../../../public/revnu.svg";

const NAV_ITEMS = [
  { id: "home", path: "/admin", icon: LayoutDashboard, label: "Overview" },
  { id: "users", path: "/admin/users", icon: Users, label: "Restaurateurs" },
  {
    id: "restaurants",
    path: "/admin/restaurants",
    icon: Building2,
    label: "Restaurants",
  },
];

export const AdminLayout = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const activeId =
    location.pathname === "/admin"
      ? "home"
      : location.pathname.startsWith("/admin/users")
        ? "users"
        : location.pathname.startsWith("/admin/restaurants")
          ? "restaurants"
          : "home";

  const handleLogout = () => {
    cache.clear();
    sessionStorage.removeItem("token");
    logout();
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-gray-50/50 flex font-sans overflow-hidden text-[#1e1b4b]">
      <div className="lg:hidden fixed top-0 left-0 right-0 h-16 bg-white border-b border-gray-200/60 z-30 flex items-center justify-between px-4 sm:px-6">
        <div className="flex items-center gap-2.5">
          <img src={revnuLogo} alt="RevnU" className="w-7 h-7" />
          <span
            className="text-xl font-black text-gray-900 tracking-tight"
            style={{ fontFamily: "'Bagel Fat One', system-ui" }}
          >
            RevnU
          </span>
        </div>
        <button
          onClick={() => setSidebarOpen(true)}
          className="p-2 -mr-2 text-gray-500 hover:bg-gray-50 rounded-xl transition-colors"
        >
          <Menu className="w-6 h-6" />
        </button>
      </div>

      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-gray-900/40 backdrop-blur-sm lg:hidden animate-in fade-in duration-200"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 w-72 lg:w-64 bg-white transition-transform duration-300 flex flex-col shadow-2xl lg:shadow-[4px_0_24px_rgba(0,0,0,0.02)] border-r border-gray-200/60 ${sidebarOpen ? "translate-x-0" : "-translate-x-full"} lg:translate-x-0 lg:static lg:z-auto`}
      >
        <div className="h-16 flex items-center justify-between px-6 shrink-0">
          <div className="flex items-center gap-2.5">
            <img src={revnuLogo} alt="RevnU" className="w-7 h-7" />
            <span
              className="text-[22px] font-black text-gray-900 tracking-tight"
              style={{ fontFamily: "'Bagel Fat One', system-ui" }}
            >
              RevnU
            </span>
          </div>
          <button
            className="lg:hidden p-1.5 -mr-1.5 text-gray-400 hover:bg-gray-100 rounded-lg transition-colors"
            onClick={() => setSidebarOpen(false)}
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="px-4 pb-4 pt-2">
          <div className="flex items-center gap-3 p-3 bg-indigo-50 rounded-2xl border border-indigo-100">
            <div className="w-10 h-10 rounded-xl bg-[#7c83fd] flex items-center justify-center shrink-0 shadow-sm">
              <ShieldAlert className="w-5 h-5 text-white" />
            </div>
            <div className="flex-1 min-w-0">
              <span className="text-sm font-bold text-gray-900 truncate block leading-tight">
                {user?.fullname || "Admin"}
              </span>
              <span className="text-[10px] font-bold text-[#7c83fd] uppercase tracking-wider">
                Platform Admin
              </span>
            </div>
          </div>
        </div>

        <nav className="flex-1 px-4 py-2 space-y-1 overflow-y-auto">
          <div className="px-3 pb-2 pt-1">
            <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider">
              Main Menu
            </p>
          </div>

          {NAV_ITEMS.map(({ id, path, icon: Icon, label }) => {
            const isActive = activeId === id;

            return (
              <button
                key={id}
                onClick={() => {
                  navigate(path);
                  setSidebarOpen(false);
                }}
                className={`w-full flex items-center gap-3.5 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all duration-200 border ${isActive ? "bg-indigo-50 text-[#7c83fd] border-indigo-100 shadow-sm" : "text-gray-500 border-transparent hover:bg-gray-50 hover:text-gray-900"}`}
              >
                <Icon
                  className={`w-4.5 h-4.5 ${isActive ? "text-[#7c83fd]" : "text-gray-400"}`}
                />
                {label}
              </button>
            );
          })}
        </nav>

        <div className="p-4 border-t border-gray-100 shrink-0">
          <button
            onClick={handleLogout}
            className="flex items-center gap-3.5 px-4 py-2.5 w-full text-sm font-semibold text-gray-500 rounded-xl hover:text-red-600 hover:bg-red-50 transition-colors border border-transparent hover:border-red-100/50"
          >
            <LogOut className="w-4.5 h-4.5 text-gray-400" />
            Log Out
          </button>
        </div>
      </aside>

      <div className="flex-1 flex flex-col min-w-0 h-screen pt-16 lg:pt-0">
        <main className="flex-1 p-4 sm:p-6 lg:p-8 flex flex-col min-h-0 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
};
