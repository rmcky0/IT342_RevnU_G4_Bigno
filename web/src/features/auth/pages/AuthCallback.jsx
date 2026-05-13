import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export const AuthCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { login } = useAuth();
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const token = searchParams.get("token");
    const error = searchParams.get("error");

    if (error) {
      setErrorMessage(decodeURIComponent(error));
      setTimeout(() => navigate("/login", { replace: true }), 3000);
      return;
    }

    if (token) {
      const sessionData = {
        token: decodeURIComponent(token),
        accessToken: decodeURIComponent(token),
        email: decodeURIComponent(searchParams.get("email") ?? ""),
        fullname: decodeURIComponent(searchParams.get("name") ?? ""),
        role: decodeURIComponent(searchParams.get("role") ?? ""),
        hasRestaurant: searchParams.get("hasRestaurant") === "true",
      };

      login(sessionData);

      if (sessionData.role === "ADMIN") {
        navigate("/admin", { replace: true });
      } else if (sessionData.role === "TENANT") {
        if (sessionData.hasRestaurant) {
          navigate("/dashboard", { replace: true });
        } else {
          navigate("/setup-restaurant", { replace: true });
        }
      } else {
        navigate("/dashboard", { replace: true });
      }
      return;
    }

    navigate("/login", { replace: true });
  }, [searchParams, navigate, login]);

  // Error UI
  if (errorMessage) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="bg-white p-8 rounded-xl shadow-sm border border-red-100 text-center max-w-sm">
          <div className="text-red-500 text-4xl mb-4">⚠️</div>
          <h2 className="text-lg font-bold text-gray-900 mb-2">Login Failed</h2>
          <p className="text-gray-600 text-sm mb-4">{errorMessage}</p>
          <p className="text-xs text-gray-400">
            Redirecting you back to login...
          </p>
        </div>
      </div>
    );
  }

  // Loading UI
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
        <p className="text-gray-500 text-sm font-medium">
          Finishing sign in...
        </p>
      </div>
    </div>
  );
};
