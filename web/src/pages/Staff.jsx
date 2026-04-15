import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Plus, Edit2, Trash2, X, Eye } from 'lucide-react';
import { staffAPI } from '../api/financialAPI';

export const Staff = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [staffMembers, setStaffMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  // Check if user is manager
  if (user?.role !== 'MANAGER') {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-8 text-center">
        <h2 className="text-2xl font-bold text-red-700 mb-2">Access Denied</h2>
        <p className="text-red-600 mb-4">Only managers can access staff management.</p>
        <button
          onClick={() => navigate('/')}
          className="text-red-600 hover:text-red-800 underline font-medium"
        >
          Return to Dashboard
        </button>
      </div>
    );
  }

  const [formData, setFormData] = useState({
    staffName: '',
    position: '',
    salary: '',
    paymentFrequency: 'MONTHLY',
    hireDate: new Date().toISOString().split('T')[0],
    status: 'ACTIVE',
  });

  // Load staff data
  useEffect(() => {
    loadStaff();
  }, []);

  const loadStaff = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await staffAPI.getAllStaff();
      setStaffMembers(data);
    } catch (err) {
      setError('Failed to load staff: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.staffName || !formData.position || !formData.salary || !formData.hireDate) {
      setError('Please fill in all required fields');
      return;
    }

    try {
      if (editingId) {
        await staffAPI.updateStaff(editingId, formData);
        setSuccess('Staff member updated successfully!');
      } else {
        await staffAPI.createStaff(formData);
        setSuccess('Staff member added successfully!');
      }
      resetForm();
      loadStaff();
    } catch (err) {
      setError('Failed to save staff: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleEdit = (staff) => {
    setFormData({
      staffName: staff.staffName,
      position: staff.position,
      salary: staff.salary,
      paymentFrequency: staff.paymentFrequency,
      hireDate: staff.hireDate,
      status: staff.status,
    });
    setEditingId(staff.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this staff member?')) return;
    setError('');
    setSuccess('');

    try {
      await staffAPI.deleteStaff(id);
      setSuccess('Staff member deleted successfully!');
      loadStaff();
    } catch (err) {
      setError('Failed to delete staff');
    }
  };

  const resetForm = () => {
    setFormData({
      staffName: '',
      position: '',
      salary: '',
      paymentFrequency: 'MONTHLY',
      hireDate: new Date().toISOString().split('T')[0],
      status: 'ACTIVE',
    });
    setEditingId(null);
    setShowForm(false);
  };

  const filteredStaff = staffMembers.filter(staff => {
    const matchesSearch = 
      staff.staffName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      staff.position?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = !statusFilter || staff.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const totalMonthlySalary = staffMembers
    .filter(s => s.status === 'ACTIVE' && s.paymentFrequency === 'MONTHLY')
    .reduce((sum, staff) => sum + (parseFloat(staff.salary) || 0), 0);

  const activeStaffCount = staffMembers.filter(s => s.status === 'ACTIVE').length;

  return (
    <div className="space-y-6">
      {/* Header Section */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-3xl font-bold text-gray-900">Staff Management</h2>
          <p className="text-gray-500 mt-1">{filteredStaff.length} records</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Add Staff
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <p className="text-sm text-gray-600 mb-1">Active Staff</p>
          <p className="text-3xl font-bold text-green-600">{activeStaffCount}</p>
        </div>
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <p className="text-sm text-gray-600 mb-1">Monthly Payroll</p>
          <p className="text-3xl font-bold text-blue-600">₱{totalMonthlySalary.toFixed(2)}</p>
        </div>
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <p className="text-sm text-gray-600 mb-1">Total Positions</p>
          <p className="text-3xl font-bold text-purple-600">
            {new Set(staffMembers.map(s => s.position)).size}
          </p>
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

      {/* Add/Edit Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-6 border-b border-gray-200 sticky top-0 bg-white">
              <h3 className="text-lg font-semibold text-gray-900">
                {editingId ? 'Edit Staff Member' : 'Add New Staff Member'}
              </h3>
              <button
                onClick={resetForm}
                className="p-1 hover:bg-gray-100 rounded"
              >
                <X className="w-5 h-5 text-gray-400" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-1">
                  Staff Name <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={formData.staffName}
                  onChange={(e) => setFormData({ ...formData, staffName: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  placeholder="e.g., John Doe"
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
                  placeholder="e.g., Chef, Waiter"
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
                  placeholder="0.00"
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
                  {editingId ? 'Update Staff' : 'Add Staff'}
                </button>
                <button
                  type="button"
                  onClick={resetForm}
                  className="flex-1 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Search & Filter Bar */}
      <div className="flex flex-col sm:flex-row gap-4">
        <input
          type="text"
          placeholder="Search staff by name or position..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 sm:w-40"
        >
          <option value="">All Status</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
      </div>

      {/* Staff Grid/Table */}
      <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-500">Loading staff...</p>
          </div>
        ) : filteredStaff.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500 mb-4">No staff members found</p>
            <button
              onClick={() => setShowForm(true)}
              className="text-indigo-600 hover:text-indigo-700 font-medium"
            >
              Add your first staff member
            </button>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-200">
                  <th className="px-6 py-3 text-left font-semibold text-gray-900">Name</th>
                  <th className="px-6 py-3 text-left font-semibold text-gray-900">Position</th>
                  <th className="px-6 py-3 text-left font-semibold text-gray-900">Salary</th>
                  <th className="px-6 py-3 text-left font-semibold text-gray-900">Frequency</th>
                  <th className="px-6 py-3 text-left font-semibold text-gray-900">Status</th>
                  <th className="px-6 py-3 text-left font-semibold text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {filteredStaff.map((staff) => (
                  <tr key={staff.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-gray-900 font-medium">{staff.staffName}</td>
                    <td className="px-6 py-4 text-gray-700">{staff.position}</td>
                    <td className="px-6 py-4 font-semibold text-gray-900">
                      ₱{parseFloat(staff.salary).toFixed(2)}
                    </td>
                    <td className="px-6 py-4">
                      <span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-medium">
                        {staff.paymentFrequency}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                        staff.status === 'ACTIVE'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-700'
                      }`}>
                        {staff.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 flex gap-2">
                      <button
                        onClick={() => navigate(`/staff/${staff.id}`, { state: { staff } })}
                        className="p-2 hover:bg-indigo-50 rounded-lg text-indigo-600 transition-colors"
                        title="View Profile"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleEdit(staff)}
                        className="p-2 hover:bg-blue-50 rounded-lg text-blue-600 transition-colors"
                        title="Edit"
                      >
                        <Edit2 className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(staff.id)}
                        className="p-2 hover:bg-red-50 rounded-lg text-red-600 transition-colors"
                        title="Delete"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
