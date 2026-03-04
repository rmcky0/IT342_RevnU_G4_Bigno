import { Link } from 'react-router-dom';
import { AuthLayout } from '../components/AuthLayout';
import { useLogin } from '../hooks/useLogin';

const GoogleIcon = () => (
  <svg width="18" height="18" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
    <path d="M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844c-.209 1.125-.843 2.078-1.796 2.717v2.258h2.908c1.702-1.567 2.684-3.874 2.684-6.615z" fill="#4285F4"/>
    <path d="M9 18c2.43 0 4.467-.806 5.956-2.184l-2.908-2.258c-.806.54-1.837.86-3.048.86-2.344 0-4.328-1.584-5.036-3.711H.957v2.332C2.438 15.983 5.482 18 9 18z" fill="#34A853"/>
    <path d="M3.964 10.707c-.18-.54-.282-1.117-.282-1.707s.102-1.167.282-1.707V4.961H.957C.347 6.175 0 7.55 0 9s.348 2.825.957 4.039l3.007-2.332z" fill="#FBBC05"/>
    <path d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0 5.482 0 2.438 2.017.957 4.961L3.964 7.293C4.672 5.166 6.656 3.58 9 3.58z" fill="#EA4335"/>
  </svg>
);

export const Login = () => {
  const { form, loading, error, handleChange, handleSubmit } = useLogin();

  return (
    <AuthLayout
      title="Welcome Back to RevnU"
      subtitle="Sign in to manage your restaurant's revenue and expenses."
    >
      {error && (
        <div className="mb-4 px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-600 text-sm">
          {error}
        </div>
      )}
      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Email */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input
            type="text"
            name="email"
            value={form.email}
            onChange={handleChange}
            placeholder="eg. staffname123 or 1234-56"
            className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#2563EB] focus:border-transparent"
            required
          />
        </div>

        {/* Password */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            placeholder="eg. Password@123"
            className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#2563EB] focus:border-transparent"
            required
          />
          <div className="text-right mt-1">
            <Link to="/forgot-password" className="text-xs italic text-gray-500 hover:text-[#2563EB]">
              Forgot Password?
            </Link>
          </div>
        </div>

        {/* Sign in button */}
        <button
          type="submit"
          disabled={loading}
          className="w-full py-3 bg-[#2563EB] hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-lg text-sm transition-colors"
        >
          {loading ? 'Signing in…' : 'Sign in'}
        </button>

        {/* Google sign in */}
        <button
          type="button"
          className="w-full flex items-center justify-center gap-3 py-2.5 px-4 border border-gray-300 rounded-lg text-sm font-semibold text-gray-700 hover:bg-gray-50 transition-colors"
        >
          <GoogleIcon />
          Sign in with Google
        </button>

        <p className="text-center text-xs text-gray-400 italic">
          By signing in, you agree to our{' '}
          <Link to="/terms" className="underline hover:text-gray-600">Terms of Service</Link>
          {' '}and{' '}
          <Link to="/privacy" className="underline hover:text-gray-600">Privacy Policy</Link>.
        </p>
      </form>

      <p className="text-center text-sm text-gray-600 mt-6 font-medium">
        Don't have an account?{' '}
        <Link to="/register" className="text-[#2563EB] font-semibold hover:underline">
          Register here
        </Link>
      </p>
    </AuthLayout>
  );
};
