import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { Login } from "./pages/Login";
import { Register } from "./pages/Register";
import { Dashboard } from "./pages/Dashboard";
import { StaffProfile } from "./pages/StaffProfile";
import { AuthCallback } from "./pages/AuthCallback";
import { LinkGoogle } from "./pages/LinkGoogle";
import { PendingApproval } from "./pages/PendingApproval";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* Dashboard with nested routes - all show Dashboard with sidebar */}
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/sales" element={<Dashboard />} />
        <Route path="/expenses" element={<Dashboard />} />
        <Route path="/analytics" element={<Dashboard />} />
        <Route path="/staff" element={<Dashboard />} />
        <Route path="/staff/:staffId" element={<Dashboard />} />
        <Route path="/settings" element={<Dashboard />} />
        
        <Route path="/auth/callback" element={<AuthCallback />} />
        <Route path="/auth/link-google" element={<LinkGoogle />} />
        <Route path="/pending-approval" element={<PendingApproval />} />
      </Routes>
    </Router>
  );
}

export default App;
