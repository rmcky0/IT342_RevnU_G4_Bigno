import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import {
  LayoutDashboard, TrendingUp, Receipt, Users,
  LogOut, Menu, X, Bell, ChevronUp, ChevronDown,
} from 'lucide-react';
import revnuLogo from '../assets/revnu_logo.svg';

const NAV_ITEMS = [
  { icon: LayoutDashboard, label: 'Dashboard' },
  { icon: TrendingUp,      label: 'Revenue'   },
  { icon: Receipt,         label: 'Expenses'  },
  { icon: Users,           label: 'Staff'     },
];

const STATS = [
  { label: 'Total Revenue',  value: '₱0.00',  change: '+0%',  up: true  },
  { label: 'Total Expenses', value: '₱0.00',  change: '+0%',  up: false },
  { label: 'Net Income',     value: '₱0.00',  change: '+0%',  up: true  },
  { label: 'Staff Members',  value: '0',       change: '',     up: null  },
];

export const Dashboard = () => {
  const { user, logout } = useAuth();
  const [active, setActive] = useState('Dashboard');
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen bg-gray-50 flex">

      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-40 w-64 bg-white border-r border-gray-100 flex flex-col transition-transform duration-300
          ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0 lg:static lg:z-auto`}
      >
        {/* Logo */}
        <div className="flex items-center gap-3 px-6 py-5 border-b border-gray-100">
          <img src={revnuLogo} alt="RevnU" className="w-9 h-9" />
          <span className="text-xl font-bold text-gray-900">RevnU</span>
          <button className="ml-auto lg:hidden" onClick={() => setSidebarOpen(false)}>
            <X className="w-5 h-5 text-gray-400" />
          </button>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-3 py-4 space-y-1">
          {NAV_ITEMS.map(({ icon: Icon, label }) => (
            <button
              key={label}
              onClick={() => { setActive(label); setSidebarOpen(false); }}
              className={`w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-medium transition-colors
                ${active === label
                  ? 'bg-[#EEF2FF] text-[#5755FF]'
                  : 'text-gray-500 hover:bg-gray-50 hover:text-gray-800'}`}
            >
              <Icon className="w-4 h-4" />
              {label}
            </button>
          ))}
        </nav>

        {/* User + Logout */}
        <div className="px-4 py-4 border-t border-gray-100">
          <div className="flex items-center gap-3 mb-3 px-2">
            <div className="w-8 h-8 rounded-full bg-purple-100 flex items-center justify-center text-purple-700 font-bold text-sm">
              {(user.fullName || 'U')[0].toUpperCase()}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-gray-900 truncate">{user.fullName || 'User'}</p>
              <p className="text-xs text-gray-400 truncate">{user.role || 'STAFF'}</p>
            </div>
          </div>
          <button
            onClick={logout}
            className="w-full flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-medium text-red-500 hover:bg-red-50 transition-colors"
          >
            <LogOut className="w-4 h-4" />
            Log out
          </button>
        </div>
      </aside>

      {/* Overlay for mobile */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-30 bg-black/20 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Main */}
      <div className="flex-1 flex flex-col min-w-0">

        {/* Topbar */}
        <header className="bg-white border-b border-gray-100 px-6 py-4 flex items-center gap-4">
          <button className="lg:hidden" onClick={() => setSidebarOpen(true)}>
            <Menu className="w-5 h-5 text-gray-500" />
          </button>
          <div>
            <h1 className="text-lg font-bold text-gray-900">{active}</h1>
            <p className="text-xs text-gray-400">Welcome back, {user.fullName || 'User'}!</p>
          </div>
          <div className="ml-auto flex items-center gap-3">
            <button className="relative p-2 rounded-xl hover:bg-gray-50 transition-colors">
              <Bell className="w-5 h-5 text-gray-400" />
            </button>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 p-6 overflow-auto">

          {/* Stats grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-8">
            {STATS.map(({ label, value, change, up }) => (
              <div key={label} className="bg-white rounded-2xl border border-gray-100 p-5">
                <p className="text-xs text-gray-400 font-medium mb-1">{label}</p>
                <p className="text-2xl font-bold text-gray-900">{value}</p>
                {change && (
                  <p className={`flex items-center gap-1 text-xs font-medium mt-1 ${up ? 'text-green-500' : 'text-red-400'}`}>
                    {up ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
                    {change} this month
                  </p>
                )}
              </div>
            ))}
          </div>

          {/* Placeholder panels */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
            <div className="bg-white rounded-2xl border border-gray-100 p-6">
              <h2 className="text-sm font-semibold text-gray-900 mb-4">Recent Transactions</h2>
              <div className="flex flex-col items-center justify-center py-12 text-center">
                <Receipt className="w-10 h-10 text-gray-200 mb-3" />
                <p className="text-sm text-gray-400">No transactions yet</p>
              </div>
            </div>
            <div className="bg-white rounded-2xl border border-gray-100 p-6">
              <h2 className="text-sm font-semibold text-gray-900 mb-4">Staff Overview</h2>
              <div className="flex flex-col items-center justify-center py-12 text-center">
                <Users className="w-10 h-10 text-gray-200 mb-3" />
                <p className="text-sm text-gray-400">No staff records yet</p>
              </div>
            </div>
          </div>

        </main>
      </div>
    </div>
  );
};
