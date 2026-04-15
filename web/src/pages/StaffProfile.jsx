import { useState, useEffect } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { ArrowLeft, Edit2, Trash2, X } from 'lucide-react';
import { staffAPI } from '../api/financialAPI';

export const StaffProfile = () => {
  const navigate = useNavigate();
  const { staffId } = useParams();
  const location = useLocation();
  const [staff, setStaff] = useState(location.state?.staff || null);
  const [loading, setLoading] = useState(!staff);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showEditForm, setShowEditForm] = useState(false);
  const [activeTab, setActiveTab] = useState('info');

  const [formData, setFormData] = useState({
    staffName: '',
    position: '',
    salary: '',
    paymentFrequency: 'MONTHLY',
    hireDate: '',
    status: 'ACTIVE',
  });

  // Load staff data if not passed via state
  useEffect(() => {
    if (!staff) {
      loadStaff();
    } else {
      setFormData({
        staffName: staff.staffName,
        position: staff.position,
        salary: staff.salary,
        paymentFrequency: staff.paymentFrequency,
        hireDate: staff.hireDate,
        status: staff.status,
      });
    }
  }, [staff]);

  const loadStaff = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await staffAPI.getStaffById(staffId);
      setStaff(data);
      setFormData({
        staffName: data.staffName,
        position: data.position,
        salary: data.salary,
        paymentFrequency: data.paymentFrequency,
        hireDate: data.hireDate,
        status: data.status,
      });
    } catch (err) {
      setError('Failed to load staff: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await staffAPI.updateStaff(staffId, formData);
      setSuccess('Staff member updated successfully!');
      setTimeout(() => {
        setShowEditForm(false);
        loadStaff();
      }, 1000);
    } catch (err) {
      setError('Failed to update staff: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this staff member? This action cannot be undone.')) return;
    setError('');

    try {
      await staffAPI.deleteStaff(staffId);
      setSuccess('Staff member deleted successfully! Redirecting...');
      setTimeout(() => navigate('/staff'), 1500);
    } catch (err) {
      setError('Failed to delete staff');
    }
  };

  // Mock salary history - in real implementation, this would come from backend
  const salaryHistory = [
    {
      date: new Date().toISOString().split('T')[0],
      amount: formData.salary,
      frequency: formData.paymentFrequency,
      status: 'Current',
    },
    {
      date: new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().split('T')[0],
      amount: formData.salary,
      frequency: formData.paymentFrequency,
      status: 'Paid',
    },
  ];

  // Mock activity log - in real implementation, this would come from backend
  const activityLog = [
    {
      date: new Date().toISOString().split('T')[0],
      action: 'Profile viewed',
      details: 'Profile accessed by manager',
    },
    {
      date: new Date(new Date().setDate(new Date().getDate() - 1)).toISOString().split('T')[0],
      action: 'Record created',
      details: 'Staff member added to system',
    },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-gray-500">Loading staff profile...</p>
      </div>
    );
  }

  if (!staff) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-red-700">
        <p className="font-semibold mb-2">Staff member not found</p>
        <button
          onClick={() => navigate('/staff')}
          className="text-red-600 hover:text-red-800 underline"
        >
          Back to Staff List
        </button>
      </div>
    );
  }

  const yearsOfService = Math.floor(
    (new Date() - new Date(staff.hireDate)) / (1000 * 60 * 60 * 24 * 365.25)
  );

  return (
    <div className="space-y-6">
      {/* Back Button */}
      <button
        onClick={() => navigate('/staff')}
        className="flex items-center gap-2 text-indigo-600 hover:text-indigo-700 font-medium"
      >
        <ArrowLeft className="w-4 h-4" />
        Back to Staff List
      </button>

      {/* Header Section */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{staff.staffName}</h1>
            <p className="text-gray-600 mt-1">{staff.position}</p>
            <div className="mt-3 flex items-center gap-4">
              <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                staff.status === 'ACTIVE'
                  ? 'bg-green-100 text-green-700'
                  : 'bg-red-100 text-red-700'
              }`}>
                {staff.status}
              </span>
              <p className="text-sm text-gray-500">{yearsOfService} year{yearsOfService !== 1 ? 's' : ''} of service</p>
            </div>
          </div>
          <div className="flex gap-2">
            <button
              onClick={() => setShowEditForm(true)}
              className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Edit2 className="w-4 h-4" />
              Edit
            </button>
            <button
              onClick={handleDelete}
              className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
            >
              <Trash2 className="w-4 h-4" />
              Delete
            </button>
          </div>
        </div>
      </div>

      {/* Messages */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700 text-sm">
          {error}
        </div>
      )}
      {success && (
        <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-green-700 text-sm">
          {success}
        </div>
      )}

      {/* Edit Form Modal */}
      {showEditForm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-6 border-b border-gray-200 sticky top-0 bg-white">
              <h3 className="text-lg font-semibold text-gray-900">Edit Staff Member</h3>
              <button
                onClick={() => setShowEditForm(false)}
                className="p-1 hover:bg-gray-100 rounded"
              >
                <X className="w-5 h-5 text-gray-400" />
              </button>
            </div>

            <form onSubmit={handleEditSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-1">
                  Staff Name <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={formData.staffName}
                  onChange={(e) => setFormData({ ...formData, staffName: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-900 mb-1">
                  Position <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={formData.position}
                  onChange={(e) => setFormData({ ...formData, position: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-900 mb-1">
                  Salary (₱) <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.salary}
                  onChange={(e) => setFormData({ ...formData, salary: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-900 mb-1">
                  Payment Frequency
                </label>
                <select
                  value={formData.paymentFrequency}
                  onChange={(e) => setFormData({ ...formData, paymentFrequency: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="DAILY">Daily</option>
                  <option value="WEEKLY">Weekly</option>
                  <option value="MONTHLY">Monthly</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-900 mb-1">
                  Hire Date <span className="text-red-500">*</span>
                </label>
                <input
                  type="date"
                  value={formData.hireDate}
                  onChange={(e) => setFormData({ ...formData, hireDate: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-900 mb-1">
                  Status
                </label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                </select>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="submit"
                  className="flex-1 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-medium"
                >
                  Save Changes
                </button>
                <button
                  type="button"
                  onClick={() => setShowEditForm(false)}
                  className="flex-1 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="border-b border-gray-200 flex gap-0">
        {['info', 'salary', 'activity'].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-6 py-3 font-medium transition-colors ${
              activeTab === tab
                ? 'border-b-2 border-indigo-600 text-indigo-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            {tab === 'info' && 'Information'}
            {tab === 'salary' && 'Salary History'}
            {tab === 'activity' && 'Activity Log'}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {activeTab === 'info' && (
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Staff Information</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div>
              <p className="text-sm text-gray-600 mb-1">Full Name</p>
              <p className="text-lg font-semibold text-gray-900">{staff.staffName}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Position</p>
              <p className="text-lg font-semibold text-gray-900">{staff.position}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Monthly Salary</p>
              <p className="text-lg font-semibold text-gray-900">₱{parseFloat(staff.salary).toFixed(2)}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Payment Frequency</p>
              <p className="text-lg font-semibold text-gray-900">{staff.paymentFrequency}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Hire Date</p>
              <p className="text-lg font-semibold text-gray-900">{staff.hireDate}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Employment Status</p>
              <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium ${
                staff.status === 'ACTIVE'
                  ? 'bg-green-100 text-green-700'
                  : 'bg-red-100 text-red-700'
              }`}>
                {staff.status}
              </span>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'salary' && (
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Salary History</h2>
          <div className="space-y-4">
            {salaryHistory.map((record, idx) => (
              <div key={idx} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2">
                  <div>
                    <p className="text-sm text-gray-600">{record.date}</p>
                    <p className="font-semibold text-gray-900">₱{parseFloat(record.amount).toFixed(2)} {record.frequency}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                    record.status === 'Paid'
                      ? 'bg-green-100 text-green-700'
                      : 'bg-blue-100 text-blue-700'
                  }`}>
                    {record.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {activeTab === 'activity' && (
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Activity Log</h2>
          <div className="space-y-4">
            {activityLog.map((log, idx) => (
              <div key={idx} className="border-l-4 border-indigo-500 bg-gray-50 rounded-lg p-4">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-2">
                  <p className="font-semibold text-gray-900">{log.action}</p>
                  <p className="text-sm text-gray-500">{log.date}</p>
                </div>
                <p className="text-sm text-gray-600">{log.details}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
