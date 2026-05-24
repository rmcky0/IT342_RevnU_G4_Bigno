import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import { AuthProvider } from "../features/auth/context/AuthContext";
import { ToastProvider } from "../shared/components/Toast";
import ProtectedRoute from "../shared/routes/ProtectedRoute";
import AdminRoute from "../shared/routes/AdminRoute";
import { Login } from "../features/auth/pages/Login";
import { Register } from "../features/auth/pages/Register";
import { AuthCallback } from "../features/auth/pages/AuthCallback";
import { LinkGoogle } from "../features/auth/pages/LinkGoogle";
import { Suspended } from "../features/auth/pages/Suspended";
import { ForgotPassword } from "../features/auth/pages/ForgotPassword";
import { SetupRestaurant } from "../features/restaurant/SetupRestaurant";
import { Dashboard } from "../features/dashboard/Dashboard";
import { Analytics } from "../features/analytics/Analytics";
import { Sales } from "../features/sales/Sales";
import { Expenses } from "../features/expenses/Expenses";
import { Staff } from "../features/staff/Staff";
import { StaffProfile } from "../features/staff/StaffProfile";
import { Settings } from "../features/settings/Settings";
import { ArchivedList } from "../features/archive/ArchivedList";
import { ArchivedDetail } from "../features/archive/ArchivedDetail";
import { AdminHome } from "../features/admin/AdminHome";
import { AdminUsers } from "../features/admin/AdminUsers";
import { AdminRestaurants } from "../features/admin/AdminRestaurants";
import { AdminLayout } from "../features/admin/AdminLayout";
import { Welcome } from "../features/auth/pages/Welcome";
import { AuthShell } from "../features/auth/components/AuthShell";

function App() {
  return (
    <ToastProvider>
      <AuthProvider>
      <Router>
        <Routes>
          <Route element={<AuthShell />}>
            <Route path="/" element={<Welcome />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/auth/callback" element={<AuthCallback />} />
            <Route path="/auth/link-google" element={<LinkGoogle />} />
            <Route path="/suspended" element={<Suspended />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
          </Route>

          <Route element={<ProtectedRoute />}>
            <Route path="/setup-restaurant" element={<SetupRestaurant />} />
            <Route element={<Dashboard />}>
              <Route path="/dashboard" element={<Analytics />} />
              <Route path="/sales" element={<Sales />} />
              <Route path="/expenses" element={<Expenses />} />
              <Route path="/staff" element={<Staff />} />
              <Route path="/staff/:staffId" element={<StaffProfile />} />
              <Route path="/settings" element={<Settings />} />
              <Route path="/archived" element={<ArchivedList />} />
              <Route path="/archived/:date" element={<ArchivedDetail />} />
            </Route>
          </Route>
          <Route element={<AdminRoute />}>
            <Route
              path="/admin"
              element={
                <AdminLayout>
                  <AdminHome />
                </AdminLayout>
              }
            />
            <Route
              path="/admin/users"
              element={
                <AdminLayout>
                  <AdminUsers />
                </AdminLayout>
              }
            />
            <Route
              path="/admin/restaurants"
              element={
                <AdminLayout>
                  <AdminRestaurants />
                </AdminLayout>
              }
            />
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
