import { useState, useEffect } from "react";
import { useNavigate, useLocation, Outlet } from "react-router-dom";
import { useAuth } from "../auth/context/AuthContext";
import { useRestaurantProfile } from "../restaurant/hooks/useRestaurantProfile";
import { API_BASE_URL } from "../../shared/api/axios";

import {
  ShoppingCart,
  CreditCard,
  Users,
  Settings as SettingsIcon,
  LogOut,
  Menu,
  X,
  Home,
} from "lucide-react";
import revnuLogo from "../../../public/revnu.svg";

const NAV_ITEMS = [
  { id: "dashboard", path: "/dashboard", icon: Home, label: "Overview" },
  { id: "sales", path: "/sales", icon: ShoppingCart, label: "Sales" },
  { id: "expenses", path: "/expenses", icon: CreditCard, label: "Expenses" },
  { id: "staff", path: "/staff", icon: Users, label: "Team & Payroll" },
  { id: "settings", path: "/settings", icon: SettingsIcon, label: "Settings" },
];

export const Dashboard = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false); // Default closed on mobile

  const { profile: restaurantProfile } = useRestaurantProfile(user?.email);

  useEffect(() => {
    if (!user) navigate("/");
  }, [user, navigate]);

  if (!user?.email) {
    return (
      <div className="min-h-screen bg-gray-50/50 flex flex-col items-center justify-center space-y-4">
        <div className="w-10 h-10 border-4 border-gray-200 border-t-[#7C6FF7] rounded-full animate-spin"></div>
        <p className="text-xs font-bold tracking-widest text-gray-400 uppercase">
          Loading Workspace...
        </p>
      </div>
    );
  }

  const restaurantName = restaurantProfile?.restaurantName || "RevnU";

  const isNavActive = (itemPath) => {
    if (itemPath === "/sales" && location.pathname.startsWith("/archived"))
      return true;
    return location.pathname.startsWith(itemPath);
  };

  const handleNavClick = (path) => {
    navigate(path);
    setSidebarOpen(false);
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const isLockedTab = ["/dashboard", "/sales", "/expenses"].includes(
    location.pathname,
  );

  return (
    <div className="min-h-screen bg-gray-50/50 flex font-sans overflow-hidden">
      {/* ── Mobile Top Header (Visible only on small screens) ── */}
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

      {/* ── Mobile Sidebar Overlay ── */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-gray-900/40 backdrop-blur-sm lg:hidden animate-in fade-in duration-200"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* ── Sidebar ── */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 w-72 lg:w-64 bg-white border-r border-gray-200/60 flex flex-col transition-transform duration-300 ease-in-out shadow-2xl lg:shadow-none
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full"} lg:translate-x-0 lg:static`}
      >
        {/* Sidebar Header */}
        <div className="h-16 flex items-center justify-between px-6 lg:px-6 shrink-0">
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

        {/* Sidebar Navigation */}
        <nav className="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
          <div className="px-3 pb-3">
            <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest">
              Main Menu
            </p>
          </div>
          {NAV_ITEMS.map((item) => {
            const Icon = item.icon;
            const isActive = isNavActive(item.path);

            return (
              <button
                key={item.id}
                onClick={() => handleNavClick(item.path)}
                className={`w-full flex items-center gap-3.5 px-3.5 py-2.5 rounded-xl text-sm transition-all duration-200 outline-none
                  ${
                    isActive
                      ? "bg-[#7C6FF7]/10 text-[#7C6FF7] font-bold"
                      : "text-gray-500 font-medium hover:bg-gray-50 hover:text-gray-900"
                  }`}
              >
                <Icon
                  className={`w-5 h-5 transition-colors ${isActive ? "text-[#7C6FF7]" : "text-gray-400"}`}
                />
                {item.label}
              </button>
            );
          })}
        </nav>

        {/* Sidebar Footer (Profile & Logout) */}
        <div className="p-4 border-t border-gray-100 shrink-0 space-y-3">
          {/* Workspace Profile Block */}
          <div
            onClick={() => handleNavClick("/settings")}
            className="flex items-center gap-3 p-3 bg-white rounded-2xl border border-gray-200/80 shadow-sm hover:border-[#7C6FF7]/30 hover:shadow-md transition-all cursor-pointer group"
          >
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-50 to-white border border-gray-200 flex items-center justify-center text-[#7C6FF7] font-black text-sm shrink-0 overflow-hidden group-hover:border-[#7C6FF7]/30 transition-colors">
              {restaurantProfile?.logoFileId ? (
                <img
                  src={`${API_BASE_URL}/files/${restaurantProfile.logoFileId}`}
                  alt="Logo"
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    e.target.style.display = "none";
                    e.target.parentElement.innerHTML =
                      restaurantName[0]?.toUpperCase();
                  }}
                />
              ) : (
                restaurantName[0]?.toUpperCase()
              )}
            </div>
            <div className="flex-1 min-w-0">
              <span className="text-sm font-bold text-gray-900 truncate block leading-tight">
                {restaurantName}
              </span>
              <span className="text-[11px] font-medium text-gray-500 truncate block mt-0.5">
                {user?.fullname || "Workspace Owner"}
              </span>
            </div>
          </div>

          {/* Logout Button */}
          <button
            onClick={handleLogout}
            className="flex items-center gap-3.5 px-3.5 py-2.5 w-full text-sm font-medium text-gray-500 rounded-xl hover:text-red-600 hover:bg-red-50 transition-colors"
          >
            <LogOut className="w-5 h-5 text-gray-400 group-hover:text-red-500" />
            Sign Out
          </button>
        </div>
      </aside>

      {/* ── Main Content Area ── */}
      <div className="flex-1 flex flex-col min-w-0 h-screen pt-16 lg:pt-0">
        <main
          className={`flex-1 p-4 sm:p-6 lg:p-8 flex flex-col min-h-0 ${isLockedTab ? "overflow-hidden" : "overflow-y-auto"}`}
        >
          {/* React Router Outlet renders your individual pages here */}
          <Outlet />
        </main>
      </div>
    </div>
  );
};
