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
import revnuLogo from "../../assets/revnu_logo.svg";

const NAV_ITEMS = [
  { id: "home", path: "/admin", icon: LayoutDashboard, label: "Overview" },
  { id: "users", path: "/admin/users", icon: Users, label: "Tenants" },
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
  const [sidebarOpen, setSidebarOpen] = useState(true);

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
  };

  return (
    <div className="min-h-screen bg-[#f4f5fa] flex font-sans">
      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-40 w-[260px] bg-white transition-transform duration-300 flex flex-col
          shadow-[4px_0_24px_rgba(0,0,0,0.02)] border-r border-gray-100
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full"} lg:translate-x-0 lg:static lg:z-auto`}
      >
        <div className="flex flex-col px-6 pt-8 pb-4">
          {/* Logo */}
          <div className="flex items-center justify-between mb-8">
            <div className="flex items-center gap-3">
              <img src={revnuLogo} alt="RevnU" className="w-8 h-8" />
              <span
                className="text-3xl text-[#1e1b4b] tracking-wide"
                style={{ fontFamily: "'Bagel Fat One', system-ui" }}
              >
                RevnU
              </span>
            </div>
            <button
              className="lg:hidden p-1 hover:bg-gray-100 rounded text-gray-400"
              onClick={() => setSidebarOpen(false)}
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Admin identity block */}
          <div className="flex items-center gap-3 p-3 bg-indigo-50 rounded-xl border border-indigo-100">
            <div className="w-10 h-10 rounded-md bg-[#7c83fd] flex items-center justify-center shrink-0 shadow-sm">
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

        {/* Nav */}
        <nav className="flex-1 px-4 py-2 space-y-1.5 overflow-y-auto">
          {NAV_ITEMS.map(({ id, path, icon: Icon, label }) => {
            const isActive = activeId === id;
            return (
              <button
                key={id}
                onClick={() => {
                  navigate(path);
                  setSidebarOpen(false);
                }}
                className={`w-full flex items-center gap-4 px-5 py-3.5 rounded-xl text-sm font-semibold transition-all duration-200
                  ${
                    isActive
                      ? "bg-[#7c83fd] text-white shadow-md shadow-indigo-200"
                      : "text-gray-500 hover:bg-gray-50 hover:text-gray-900"
                  }`}
              >
                <Icon
                  className={`w-5 h-5 ${isActive ? "text-white" : "text-gray-400"}`}
                />
                {label}
              </button>
            );
          })}
        </nav>

        {/* Logout */}
        <div className="p-6">
          <button
            onClick={handleLogout}
            className="flex items-center gap-4 px-2 py-2 text-sm font-semibold text-gray-500 hover:text-gray-900 transition-colors w-full"
          >
            <LogOut className="w-5 h-5 text-gray-400" />
            Log Out
          </button>
        </div>
      </aside>

      {/* Mobile overlay */}
      {!sidebarOpen && (
        <button
          className="lg:hidden fixed top-6 left-6 z-50 p-3 bg-white rounded-xl shadow-lg border border-gray-100"
          onClick={() => setSidebarOpen(true)}
        >
          <Menu className="w-6 h-6 text-gray-600" />
        </button>
      )}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-30 bg-black/20 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Main content */}
      <div className="flex-1 min-w-0 overflow-y-auto">
        <main className="p-8">{children}</main>
      </div>
    </div>
  );
};
