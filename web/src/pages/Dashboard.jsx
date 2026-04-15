import { useState, useEffect } from 'react';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Sales } from './Sales';
import { Expenses } from './Expenses';
import { Staff } from './Staff';
import { StaffProfile } from './StaffProfile';
import { Settings } from './Settings';
import { Analytics } from './Analytics';
import {
  Home, ShoppingCart, CreditCard, Users,
  Settings as SettingsIcon, LogOut, Menu, X, Bell,
} from 'lucide-react';
import revnuLogo from '../assets/revnu_logo.svg';

const NAV_ITEMS = [
  { id: 'home', icon: Home, label: 'Home' },
  { id: 'sales', icon: ShoppingCart, label: 'Sales' },
  { id: 'expenses', icon: CreditCard, label: 'Expenses' },
  { id: 'staff', icon: Users, label: 'Staff', managerOnly: true },
  { id: 'settings', icon: SettingsIcon, label: 'Settings' },
];

export const Dashboard = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { staffId } = useParams();
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  // Determine active tab from URL path
  const getActiveTab = () => {
    const path = location.pathname;
    if (path === '/sales') return 'sales';
    if (path === '/expenses') return 'expenses';
    if (path === '/staff' && !staffId) return 'staff';
    if (path.startsWith('/staff/')) return 'staff-profile';
    if (path === '/settings') return 'settings';
    return 'home';
  };

  const activeTab = getActiveTab();

  // Check authentication
  useEffect(() => {
    const token = sessionStorage.getItem('token');
    if (!token) {
      navigate('/login');
    }
  }, [navigate]);

  if (!user?.email) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Loading...</p>
      </div>
    );
  }

  const handleNavClick = (tabId) => {
    if (tabId === 'home') {
      navigate('/');
    } else {
      navigate(`/${tabId}`);
    }
    setSidebarOpen(false);
  }


  return (
    <div className="min-h-screen bg-gray-50 flex">
      
      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-40 w-64 bg-white border-r border-gray-200 transition-transform duration-300 flex flex-col
          ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0 lg:static lg:z-auto`}
      >
        
        {/* Logo Section */}
        <div className="flex items-center gap-3 px-6 py-6 border-b border-gray-200">
          <img src={revnuLogo} alt="RevnU" className="w-10 h-10" />
          <span className="text-2xl font-bold text-gray-900">RevnU</span>
          <button
            className="ml-auto lg:hidden p-1 hover:bg-gray-100 rounded"
            onClick={() => setSidebarOpen(false)}
          >
            <X className="w-5 h-5 text-gray-400" />
          </button>
        </div>

        {/* Navigation Items */}
        <nav className="flex-1 px-4 py-6 space-y-2">
          {NAV_ITEMS.map(({ id, icon: Icon, label, managerOnly }) => {
            // Hide manager-only items from non-managers
            if (managerOnly && user?.role !== 'MANAGER') {
              return null;
            }

            return (
              <button
                key={id}
                onClick={() => handleNavClick(id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-all
                  ${activeTab === id
                    ? 'bg-indigo-100 text-indigo-600'
                    : 'text-gray-700 hover:bg-gray-100'}`}
              >
                <Icon className="w-5 h-5" />
                {label}
              </button>
            );
          })}
        </nav>

        {/* User Section */}
        <div className="px-4 py-4 border-t border-gray-200 space-y-4">
          <div className="flex items-center gap-3 px-2">
            <div className="w-10 h-10 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 font-bold text-sm">
              {(user.fullName || 'U')[0].toUpperCase()}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-gray-900 truncate">{user.fullName || 'User'}</p>
              <p className="text-xs text-gray-500 truncate">{user.role || 'STAFF'}</p>
            </div>
          </div>

          <button
            onClick={logout}
            className="w-full flex items-center gap-3 px-4 py-2 rounded-lg text-sm font-medium text-red-600 hover:bg-red-50 transition-colors"
          >
            <LogOut className="w-5 h-5" />
            Log Out
          </button>
        </div>
      </aside>

      {/* Mobile Overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-30 bg-black/20 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0">
        
        {/* Header */}
        <header className="bg-white border-b border-gray-200 px-6 py-4 flex items-center gap-4">
          <button
            className="lg:hidden p-2 hover:bg-gray-100 rounded-lg"
            onClick={() => setSidebarOpen(true)}
          >
            <Menu className="w-5 h-5 text-gray-600" />
          </button>
          <div className="flex-1">
            <h1 className="text-2xl font-bold text-gray-900">
              {activeTab === 'staff-profile' 
                ? 'Staff Profile'
                : NAV_ITEMS.find(item => item.id === activeTab)?.label || 'Dashboard'}
            </h1>
          </div>
          <button className="p-2 hover:bg-gray-100 rounded-lg">
            <Bell className="w-5 h-5 text-gray-600" />
          </button>
        </header>

        {/* Content Area */}
        <main className="flex-1 overflow-auto p-6">
          {activeTab === 'home' && (
            user?.role === 'MANAGER' ? (
              <Analytics />
            ) : (
              <div className="bg-white rounded-lg p-8 text-center border border-gray-200">
                <h2 className="text-3xl font-bold text-gray-900 mb-2">Welcome, {user.fullName}!</h2>
                <p className="text-gray-600 mb-6">This is your financial dashboard for managing restaurant operations.</p>
                <p className="text-sm text-gray-500">Use the sidebar to access Sales, Expenses, and other sections.</p>
              </div>
            )
          )}
          
          {activeTab === 'sales' && <Sales />}
          
          {activeTab === 'expenses' && <Expenses />}
          
          {activeTab === 'staff' && <Staff />}
          
          {activeTab === 'staff-profile' && <StaffProfile />}
          
          {activeTab === 'settings' && <Settings />}
        </main>
      </div>
    </div>
  );
};
