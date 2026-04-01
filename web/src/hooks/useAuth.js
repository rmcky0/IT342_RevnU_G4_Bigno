import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

export const useAuth = () => {
  const navigate = useNavigate();
  const user = authService.getSession();

  const logout = () => {
    authService.clearSession();
    navigate('/login');
  };

  return { user, logout };
};
