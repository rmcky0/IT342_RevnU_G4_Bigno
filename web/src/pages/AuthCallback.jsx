import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '../services/authService';

export const AuthCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState('');
  const [returnPath, setReturnPath] = useState('/login');

  useEffect(() => {
    const token = searchParams.get('token');
    const error = searchParams.get('error');
    const source = sessionStorage.getItem('oauth2_source') ?? '/login';

    if (error) {
      sessionStorage.removeItem('oauth2_source');
      // access_denied = user clicked Cancel — return silently
      if (error.toLowerCase().includes('access_denied')) {
        navigate(source, { replace: true });
        return;
      }
      setReturnPath(source);
      setErrorMessage(decodeURIComponent(error));
      setTimeout(() => navigate(source, { replace: true }), 3000);
      return;
    }

    if (token) {
      sessionStorage.removeItem('oauth2_source');
      const sessionData = {
        accessToken: decodeURIComponent(token),
        email: decodeURIComponent(searchParams.get('email') ?? ''),
        fullName: decodeURIComponent(searchParams.get('name') ?? ''),
        role: decodeURIComponent(searchParams.get('role') ?? ''),
        message: 'Google login successful',
      };
      authService.saveSession(sessionData);
      navigate('/dashboard', { replace: true });
      return;
    }

    // Neither token nor error — just send back
    navigate(source, { replace: true });
  }, [searchParams, navigate]);

  if (errorMessage) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="bg-red-50 border border-red-200 text-red-600 rounded-lg px-6 py-4 text-sm text-center max-w-sm">
          <p className="font-semibold mb-1">Google sign-in failed</p>
          <p>{errorMessage}</p>
          <p className="text-gray-400 mt-2">Redirecting to {returnPath === '/register' ? 'registration' : 'login'}…</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-gray-500 text-sm animate-pulse">Signing you in…</p>
    </div>
  );
};
