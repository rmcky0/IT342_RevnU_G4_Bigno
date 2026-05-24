import { createContext, useContext, useState, useEffect, useCallback } from "react";
import { authService } from "../services/authService";
import * as cache from "../../../shared/cache/dataCache";

const getSession = () => {
  const raw = sessionStorage.getItem("user");
  return raw ? JSON.parse(raw) : null;
};

const getToken = () => sessionStorage.getItem("token");

const saveSession = (user) => {
  const userToStore = {
    email: user.email,
    fullname: user.fullname,
    role: user.role,
    hasRestaurant: user.hasRestaurant,
    provider: user.provider ?? null,
  };
  sessionStorage.setItem("user", JSON.stringify(userToStore));

  const token = user?.accessToken ?? user?.token;
  if (token) {
    sessionStorage.setItem("token", token);
  }

  if (user?.refreshToken) {
    sessionStorage.setItem("refreshToken", user.refreshToken);
  }
};

const clearSession = () => {
  sessionStorage.removeItem("user");
  sessionStorage.removeItem("token");
  sessionStorage.removeItem("refreshToken");
  cache.clear();
};

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(getToken());
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const storedUser = getSession();
      if (storedUser && token) {
        try {
          const currentUser = await authService.getCurrentUser();
          const updatedUser = {
            email: currentUser.email,
            fullname: currentUser.fullname,
            role: currentUser.role,
            hasRestaurant: currentUser.hasRestaurant,
            provider: currentUser.provider ?? storedUser.provider ?? null,
          };
          sessionStorage.setItem("user", JSON.stringify(updatedUser));
          setUser(updatedUser);
        } catch (error) {
          console.warn("Token verification failed, clearing session");
          clearSession();
          setToken(null);
          setUser(null);
        }
      }
      setIsLoading(false);
    };

    initializeAuth();
  }, [token]);

  const login = useCallback((data) => {
    saveSession(data);
    setToken(data.accessToken ?? data.token);
    setUser({
      email: data.email,
      name: data.fullname,
      fullname: data.fullname,
      role: data.role,
      hasRestaurant: data.hasRestaurant,
      provider: data.provider ?? null,
    });
  }, []);

  const completeRestaurantSetup = () => {
    setUser((currentUser) => {
      if (!currentUser) {
        return currentUser;
      }

      const updatedUser = {
        ...currentUser,
        hasRestaurant: true,
      };

      sessionStorage.setItem("user", JSON.stringify(updatedUser));
      return updatedUser;
    });
  };

  const logout = () => {
    clearSession();
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        logout,
        isLoading,
        completeRestaurantSetup,
      }}
    >
      {!isLoading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
