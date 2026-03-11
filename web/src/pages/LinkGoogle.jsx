import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { AuthLayout } from '../components/AuthLayout';
import { authService } from '../services/authService';
import { Notification } from '../components/Notification';

export const LinkGoogle = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const email    = decodeURIComponent(searchParams.get('email')    ?? '');
  const googleId = decodeURIComponent(searchParams.get('googleId') ?? '');

  const [password, setPassword] = useState('');
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState('');
  const [success, setSuccess]   = useState('');

  useEffect(() => {
    if (!email || !googleId) {
      navigate('/login', { replace: true });
    }
  }, [email, googleId, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await authService.linkGoogle({ email, password, googleId });
      authService.saveSession(data);
      setSuccess('Google linked — redirecting…');
      setTimeout(() => navigate('/dashboard', { replace: true }), 600);
    } catch (err) {
      setError(err.response?.data || 'Failed to link account. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout
      title="Link Your Google Account"
      subtitle={`A RevnU account already exists for ${email}. Enter your password to confirm and link Google.`}
    >
      {error && <Notification type="error">{error}</Notification>}
      {success && <Notification type="success">{success}</Notification>}

      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Email — read-only, just for context */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input
            type="email"
            value={email}
            readOnly
            className="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm bg-gray-50 text-gray-500 cursor-not-allowed"
          />
        </div>

        {/* Password */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your current password"
            className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#2563EB] focus:border-transparent"
            required
            autoFocus
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full py-3 bg-[#2563EB] hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-lg text-sm transition-colors"
        >
          {loading ? 'Linking…' : 'Confirm & Link Google Account'}
        </button>
      </form>

      <p className="text-center text-sm text-gray-500 mt-6">
        Don't want to link?{' '}
        <Link to="/login" className="text-[#2563EB] font-semibold hover:underline">
          Back to Sign In
        </Link>
      </p>
    </AuthLayout>
  );
};
