import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

export const useLogin = () => {
  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const data = await authService.login(form);
      authService.saveSession(data);
      
      // Check if account is pending approval
      if (data.status === 'PENDING') {
        setSuccess('Login successful — redirecting to pending approval…');
        setTimeout(() => navigate('/pending-approval', { state: { fullName: data.fullName } }), 600);
        return;
      }
      
      // Check if account is inactive
      if (data.status === 'INACTIVE') {
        setError('Your account is disabled. Please contact the owner.');
        setLoading(false);
        return;
      }
      
      setSuccess('Login successful — redirecting…');
      setTimeout(() => navigate('/dashboard'), 600);
    } catch (err) {
      setError(err.response?.data || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return { form, loading, error, success, handleChange, handleSubmit };
};
