import { useState, useEffect } from "react";
import { useNavigate, useLocation, Outlet } from "react-router-dom";
import { useAuth } from "../auth/context/AuthContext";
import { useRestaurantProfile } from "../restaurant/hooks/useRestaurantProfile";
import { settingsApi } from "../settings/api/settingsApi";
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
import revnuLogo from "../../assets/revnu_logo.svg";

const NAV_ITEMS = [
  {
    id: "dashboard",
    path: "/dashboard",
    icon: Home,
    label: "Dashboard Overview",
  },
  { id: "sales", path: "/sales", icon: ShoppingCart, label: "Sales" },
  { id: "expenses", path: "/expenses", icon: CreditCard, label: "Expenses" },
  { id: "staff", path: "/staff", icon: Users, label: "Team & Payroll" },
  { id: "settings", path: "/settings", icon: SettingsIcon, label: "Settings" },
];

export const Dashboard = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout, updateAvatar } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const { profile: restaurantProfile } = useRestaurantProfile(user?.email);

  useEffect(() => {
    if (user && !user.avatarFileId) {
      settingsApi.getProfile().then((profile) => {
        if (profile?.avatarFileId) updateAvatar(profile.avatarFileId);
      }).catch(() => {});
    }
  }, []);

  // Security check: Redirect if Context says no user
  useEffect(() => {
    if (!user) navigate("/login");
  }, [user, navigate]);

  if (!user?.email) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center space-y-4">
        <div className="w-10 h-10 border-4 border-indigo-100 border-t-[#7c83fd] rounded-full animate-spin"></div>
        <p className="text-sm font-bold tracking-wider text-gray-400 uppercase">
          Loading Workspace
        </p>
      </div>
    );
  }

  // Uses the corrected DTO key: restaurantName
  const restaurantName = restaurantProfile?.restaurantName || "RevnU";

  // Helper to determine if a nav item is active based on the current URL
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
    // AuthContext handles all cache and storage clearing now!
    logout();
    navigate("/login");
  };

  const isLockedTab = ["/dashboard", "/sales", "/expenses"].includes(
    location.pathname,
  );

  return (
    <div className="min-h-screen bg-[#f4f5fa] flex font-sans">
      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-40 w-[260px] bg-white transition-transform duration-300 flex flex-col shadow-[1px_0_20px_rgba(0,0,0,0.03)] border-r border-gray-100
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full"} lg:translate-x-0 lg:static lg:z-auto`}
      >
        <div className="flex flex-col px-5 pt-6 pb-4 shrink-0">
          {/* Logo row */}
          <div className="flex items-center justify-between mb-6 px-1">
            <div className="flex items-center gap-2.5">
              <img src={revnuLogo} alt="RevnU" className="w-7 h-7" />
              <span
                className="text-[22px] text-[#1e1b4b] tracking-wide"
                style={{ fontFamily: "'Bagel Fat One', system-ui" }}
              >
                RevnU
              </span>
            </div>
            <button
              className="lg:hidden p-1.5 hover:bg-gray-100 rounded-lg text-gray-400 transition-colors"
              onClick={() => setSidebarOpen(false)}
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Restaurant identity block */}
          <div className="flex items-center gap-3 p-2.5 bg-white rounded-xl border border-gray-100 shadow-[0_2px_10px_rgb(0,0,0,0.02)] hover:border-indigo-100 transition-colors cursor-default">
            {/* UPDATED: Image Rendering Logic */}
            <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-indigo-50 to-white border border-indigo-100 flex items-center justify-center text-[#7c83fd] font-black text-sm shrink-0 shadow-sm overflow-hidden">
              {restaurantProfile?.logoFileId ? (
                <img
                  src={`${API_BASE_URL}/files/${restaurantProfile.logoFileId}`}
                  alt="Restaurant Logo"
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    // Fallback if the image fails to load (e.g. backend error)
                    e.target.style.display = "none";
                    e.target.parentElement.innerHTML =
                      restaurantName[0]?.toUpperCase();
                  }}
                />
              ) : (
                restaurantName[0]?.toUpperCase()
              )}
            </div>
            {/* END UPDATED SECTION */}

            <div className="flex-1 min-w-0">
              <span className="text-sm font-bold text-[#1e1b4b] truncate block leading-tight">
                {restaurantName}
              </span>
              <span className="text-[10px] font-bold text-gray-400 uppercase tracking-wider block mt-0.5 truncate">
                {user?.fullname || "Workspace Owner"}
              </span>
            </div>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-4 py-2 space-y-1 overflow-y-auto custom-scrollbar">
          <div className="px-3 pb-2 pt-1">
            <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider">
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
                className={`w-full flex items-center gap-3.5 px-4 py-2.5 rounded-lg text-sm font-semibold transition-all duration-200
                  ${isActive ? "bg-indigo-50 text-[#7c83fd] shadow-sm border border-indigo-100/50" : "text-gray-500 hover:bg-gray-50 hover:text-[#1e1b4b] border border-transparent"}`}
              >
                <Icon
                  className={`w-[18px] h-[18px] ${isActive ? "text-[#7c83fd]" : "text-gray-400"}`}
                />
                {item.label}
              </button>
            );
          })}
        </nav>

        {/* User + Logout Footer */}
        <div className="p-4 border-t border-gray-50 shrink-0 space-y-1">
          <div className="flex items-center gap-3 px-2 py-2">
            <div className="w-8 h-8 rounded-full overflow-hidden bg-indigo-100 shrink-0 border border-indigo-100 shadow-sm">
              <img
                src={
                  user?.avatarFileId
                    ? `${API_BASE_URL}/files/${user.avatarFileId}`
                    : `https://ui-avatars.com/api/?name=${encodeURIComponent(user?.fullname || "U")}&background=c7d2fe&color=4338ca&bold=true`
                }
                alt="avatar"
                className="w-full h-full object-cover"
              />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-bold text-[#1e1b4b] truncate leading-tight">
                {user?.fullname || "Workspace Owner"}
              </p>
              <p className="text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                {user?.role || "Tenant"}
              </p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 px-4 py-2.5 w-full text-sm font-semibold text-gray-500 rounded-lg hover:text-red-600 hover:bg-red-50 transition-colors border border-transparent hover:border-red-100/50"
          >
            <LogOut className="w-[18px] h-[18px]" />
            Sign Out
          </button>
        </div>
      </aside>

      {/* Mobile overlay */}
      {!sidebarOpen && (
        <button
          className="lg:hidden fixed top-6 left-6 z-50 p-2.5 bg-white rounded-xl shadow-lg border border-gray-100 text-[#1e1b4b] hover:bg-gray-50 transition-colors"
          onClick={() => setSidebarOpen(true)}
        >
          <Menu className="w-5 h-5" />
        </button>
      )}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-30 bg-[#1e1b4b]/20 backdrop-blur-sm lg:hidden animate-in fade-in duration-200"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
        <main
          className={`flex-1 p-8 flex flex-col min-h-0 ${isLockedTab ? "overflow-hidden" : "overflow-y-auto"}`}
        >
          {/* APPLIED: React Router Outlet */}
          <Outlet />
        </main>
      </div>
    </div>
  );
};
