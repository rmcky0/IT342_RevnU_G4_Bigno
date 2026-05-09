import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../features/auth/context/AuthContext";

const AdminRoute = () => {
  const { user } = useAuth();
  const token = sessionStorage.getItem("token");

  if (!token) return <Navigate to="/login" replace />;
  if (!user) return null;
  if (user.role !== "ADMIN") return <Navigate to="/dashboard" replace />;

  return <Outlet />;
};

export default AdminRoute;
