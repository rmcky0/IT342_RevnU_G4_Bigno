import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { Save, RefreshCw } from 'lucide-react';

export const Permissions = () => {
  const { user } = useAuth();
  const [staffPermissions, setStaffPermissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Permission modules and their operations
  const modules = [
    {
      name: 'Sales',
      key: 'sales',
      operations: ['view', 'create', 'edit', 'delete'],
    },
    {
      name: 'Expenses',
      key: 'expenses',
      operations: ['view', 'create', 'edit', 'delete'],
    },
    {
      name: 'Reports',
      key: 'reports',
      operations: ['view', 'export'],
    },
  ];

  // Check if user is manager
  if (user?.role !== 'MANAGER') {
    return (
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-8 text-center">
        <h2 className="text-2xl font-bold text-blue-700 mb-2">Staff Permissions</h2>
        <p className="text-blue-600">You do not have permission to manage staff permissions.</p>
      </div>
    );
  }

  // Mock staff data - in real implementation, fetch from backend
  const mockStaffWithPermissions = [
    {
      id: 1,
      staffName: 'John Doe',
      position: 'Chef',
      permissions: {
        sales: { view: true, create: true, edit: false, delete: false },
        expenses: { view: true, create: false, edit: false, delete: false },
        reports: { view: true, export: false },
      },
    },
    {
      id: 2,
      staffName: 'Jane Smith',
      position: 'Waiter',
      permissions: {
        sales: { view: true, create: true, edit: false, delete: false },
        expenses: { view: false, create: false, edit: false, delete: false },
        reports: { view: false, export: false },
      },
    },
  ];

  useEffect(() => {
    loadPermissions();
  }, []);

  const loadPermissions = async () => {
    setLoading(true);
    setError('');
    try {
      // In real implementation, fetch from backend
      setStaffPermissions(mockStaffWithPermissions);
    } catch (err) {
      setError('Failed to load permissions');
    } finally {
      setLoading(false);
    }
  };

  const handlePermissionChange = (staffId, moduleKey, operation) => {
    setStaffPermissions(prevState =>
      prevState.map(staff =>
        staff.id === staffId
          ? {
              ...staff,
              permissions: {
                ...staff.permissions,
                [moduleKey]: {
                  ...staff.permissions[moduleKey],
                  [operation]: !staff.permissions[moduleKey][operation],
                },
              },
            }
          : staff
      )
    );
  };

  const handleSavePermissions = async () => {
    setError('');
    setSuccess('');
    try {
      // In real implementation, send to backend
      setSuccess('Permissions saved successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Failed to save permissions');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-3xl font-bold text-gray-900">Staff Permissions</h2>
          <p className="text-gray-500 mt-1">Control what each staff member can do</p>
        </div>
        <button
          onClick={handleSavePermissions}
          className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
        >
          <Save className="w-5 h-5" />
          Save Changes
        </button>
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

      {/* Legend */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <p className="text-sm text-blue-800">
          <strong>Legend:</strong> Enable or disable specific operations for each staff member across different modules. 
          Unchecked = No access | Checked = Has access
        </p>
      </div>

      {/* Permissions Table */}
      {loading ? (
        <div className="text-center py-12">
          <p className="text-gray-500">Loading permissions...</p>
        </div>
      ) : staffPermissions.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
          <p className="text-gray-500">No staff members found</p>
        </div>
      ) : (
        <div className="space-y-6">
          {staffPermissions.map((staff) => (
            <div key={staff.id} className="bg-white rounded-lg border border-gray-200 overflow-hidden">
              {/* Staff Header */}
              <div className="bg-gray-50 px-6 py-4 border-b border-gray-200">
                <p className="text-lg font-semibold text-gray-900">{staff.staffName}</p>
                <p className="text-sm text-gray-600">{staff.position}</p>
              </div>

              {/* Permissions Grid */}
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-gray-50 border-b border-gray-200">
                      <th className="px-6 py-3 text-left font-semibold text-gray-900">Module</th>
                      {['View', 'Create', 'Edit', 'Delete', 'Export'].map((op) => (
                        <th
                          key={op}
                          className="px-4 py-3 text-center font-semibold text-gray-900 whitespace-nowrap"
                        >
                          {op}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {modules.map((module) => (
                      <tr key={module.key} className="hover:bg-gray-50">
                        <td className="px-6 py-4 font-medium text-gray-900">{module.name}</td>
                        {['view', 'create', 'edit', 'delete', 'export'].map((op) => {
                          const isApplicable = module.operations.includes(op);
                          const isChecked =
                            isApplicable && staff.permissions[module.key]?.[op];

                          return (
                            <td key={op} className="px-4 py-4 text-center">
                              {isApplicable ? (
                                <input
                                  type="checkbox"
                                  checked={isChecked || false}
                                  onChange={() =>
                                    handlePermissionChange(staff.id, module.key, op)
                                  }
                                  className="w-5 h-5 rounded border-gray-300 text-indigo-600 cursor-pointer accent-indigo-600"
                                />
                              ) : (
                                <span className="text-gray-300">—</span>
                              )}
                            </td>
                          );
                        })}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Info Box */}
      <div className="bg-gray-50 border border-gray-200 rounded-lg p-6">
        <h3 className="font-semibold text-gray-900 mb-3">Permission Guide</h3>
        <ul className="space-y-2 text-sm text-gray-700">
          <li>
            <strong>View:</strong> Allow staff to view records in this module
          </li>
          <li>
            <strong>Create:</strong> Allow staff to create new records
          </li>
          <li>
            <strong>Edit:</strong> Allow staff to modify existing records
          </li>
          <li>
            <strong>Delete:</strong> Allow staff to remove records
          </li>
          <li>
            <strong>Export:</strong> Allow staff to export data
          </li>
        </ul>
      </div>
    </div>
  );
};
