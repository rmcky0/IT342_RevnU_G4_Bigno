import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { Camera, Mail, Lock, Plus, Trash2 } from 'lucide-react';

export const Settings = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('personal');
  const [emailNotifications, setEmailNotifications] = useState(true);
  const [pushNotifications, setPushNotifications] = useState(false);
  const [requireReceiptPhoto, setRequireReceiptPhoto] = useState(false);
  const [formData, setFormData] = useState({
    fullName: user?.fullName || '',
    email: user?.email || '',
    birthday: '',
    contactNumber: '',
    emergencyContact: '',
    emergencyPhone: '',
  });
  const [restaurantData, setRestaurantData] = useState({
    restaurantName: 'ChiNyMic Restobar',
    businessCategory: 'Restaurant',
    physicalAddress: 'Liloan, Santander, Cebu',
    openingHours: '8:00 AM',
    closingHours: '10:00 PM',
  });
  const [salesCategories, setSalesCategories] = useState(['', '']);
  const [expenseCategories, setExpenseCategories] = useState(['', '']);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSave = () => {
    // TODO: Implement API call to save user profile
    console.log('Saving profile:', formData);
  };

  const handleProfilePictureChange = () => {
    // TODO: Implement profile picture upload
    console.log('Change profile picture');
  };

  const handlePasswordChange = () => {
    // TODO: Implement password change modal
    console.log('Change password');
  };

  const handleRestaurantInputChange = (e) => {
    const { name, value } = e.target;
    setRestaurantData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSalesCategory = (index, value) => {
    const newCategories = [...salesCategories];
    newCategories[index] = value;
    setSalesCategories(newCategories);
  };

  const handleExpenseCategory = (index, value) => {
    const newCategories = [...expenseCategories];
    newCategories[index] = value;
    setExpenseCategories(newCategories);
  };

  const addSalesCategory = () => {
    setSalesCategories([...salesCategories, '']);
  };

  const addExpenseCategory = () => {
    setExpenseCategories([...expenseCategories, '']);
  };

  const removeSalesCategory = (index) => {
    setSalesCategories(salesCategories.filter((_, i) => i !== index));
  };

  const removeExpenseCategory = (index) => {
    setExpenseCategories(expenseCategories.filter((_, i) => i !== index));
  };

  const handleSaveRestaurant = () => {
    // TODO: Implement API call to save restaurant profile
    console.log('Saving restaurant profile:', restaurantData, salesCategories, expenseCategories);
  };

  const handleBusinessLogoChange = () => {
    // TODO: Implement business logo upload
    console.log('Change business logo');
  };

  return (
    <div className="max-w-6xl mx-auto">
      {/* Tabs */}
      <div className="flex gap-4 mb-8 border-b border-gray-200">
        <button
          onClick={() => setActiveTab('personal')}
          className={`px-6 py-3 font-medium transition-colors ${
            activeTab === 'personal'
              ? 'text-indigo-600 border-b-2 border-indigo-600'
              : 'text-gray-600 hover:text-gray-900'
          }`}
        >
          Personal Profile
        </button>
        {user?.role === 'MANAGER' && (
          <button
            onClick={() => setActiveTab('restaurant')}
            className={`px-6 py-3 font-medium transition-colors ${
              activeTab === 'restaurant'
                ? 'text-indigo-600 border-b-2 border-indigo-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            Restaurant Profile
          </button>
        )}
      </div>

      {/* Personal Profile Tab */}
      {activeTab === 'personal' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Profile Picture Section */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg p-6 border border-gray-200 sticky top-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-6">Profile Picture</h3>
              <div className="flex flex-col items-center gap-4">
                <div className="w-48 h-48 rounded-full bg-indigo-100 flex items-center justify-center overflow-hidden border-4 border-gray-200">
                  <img
                    src={`https://ui-avatars.com/api/?name=${encodeURIComponent(user?.fullName || 'User')}&background=random&color=fff`}
                    alt="Profile"
                    className="w-full h-full object-cover"
                  />
                </div>
                <button
                  onClick={handleProfilePictureChange}
                  className="w-full flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium transition-colors"
                >
                  <Camera className="w-4 h-4" />
                  Change Profile Picture
                </button>
              </div>
            </div>
          </div>

          {/* Profile Form Section */}
          <div className="lg:col-span-2 space-y-6">
            {/* Account Information */}
            <div className="bg-white rounded-lg p-6 border border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Account Information</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Full Name</label>
                  <input
                    type="text"
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Birthday</label>
                  <input
                    type="date"
                    name="birthday"
                    value={formData.birthday}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Contact Number</label>
                  <input
                    type="tel"
                    name="contactNumber"
                    value={formData.contactNumber}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Emergency Contact Person</label>
                  <input
                    type="text"
                    name="emergencyContact"
                    value={formData.emergencyContact}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Emergency Contact Number</label>
                  <input
                    type="tel"
                    name="emergencyPhone"
                    value={formData.emergencyPhone}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>
              <button
                onClick={handlePasswordChange}
                className="mt-6 flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium transition-colors"
              >
                <Lock className="w-4 h-4" />
                Change Password
              </button>
            </div>

            {/* Preferences & Notifications */}
            <div className="bg-white rounded-lg p-6 border border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Preferences & Notifications</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">Email Notifications</p>
                    <p className="text-sm text-gray-600">Receive notifications via email</p>
                  </div>
                  <button
                    onClick={() => setEmailNotifications(!emailNotifications)}
                    className={`w-12 h-6 rounded-full transition-colors ${
                      emailNotifications ? 'bg-indigo-600' : 'bg-gray-300'
                    }`}
                  >
                    <div
                      className={`w-5 h-5 rounded-full bg-white transition-transform ${
                        emailNotifications ? 'translate-x-6' : 'translate-x-1'
                      }`}
                    />
                  </button>
                </div>
                <div className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">Push Notifications</p>
                    <p className="text-sm text-gray-600">Receive push notifications on your device</p>
                  </div>
                  <button
                    onClick={() => setPushNotifications(!pushNotifications)}
                    className={`w-12 h-6 rounded-full transition-colors ${
                      pushNotifications ? 'bg-indigo-600' : 'bg-gray-300'
                    }`}
                  >
                    <div
                      className={`w-5 h-5 rounded-full bg-white transition-transform ${
                        pushNotifications ? 'translate-x-6' : 'translate-x-1'
                      }`}
                    />
                  </button>
                </div>
              </div>
            </div>

            {/* Security & Connected Accounts */}
            <div className="bg-white rounded-lg p-6 border border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Security & Connected Accounts</h3>
              <div className="border border-gray-200 rounded-lg p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium text-gray-900">Connected Google Account</p>
                    <p className="text-sm text-gray-600">{user?.email}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs font-semibold text-green-600 bg-green-50 px-3 py-1 rounded-full">Connected</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Save Button */}
            <button
              onClick={handleSave}
              className="w-full px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium transition-colors"
            >
              Save Changes
            </button>
          </div>
        </div>
      )}

      {/* Restaurant Profile Tab */}
      {activeTab === 'restaurant' && user?.role === 'MANAGER' ? (
        <div className="max-w-4xl mx-auto space-y-6">
          {/* Business Identity */}
          <div className="bg-white rounded-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900 mb-6">Business Identity</h3>
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              <div className="lg:col-span-2 space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Restaurant Name</label>
                  <input
                    type="text"
                    name="restaurantName"
                    value={restaurantData.restaurantName}
                    onChange={handleRestaurantInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Business Category</label>
                  <input
                    type="text"
                    name="businessCategory"
                    value={restaurantData.businessCategory}
                    onChange={handleRestaurantInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Physical Address</label>
                  <input
                    type="text"
                    name="physicalAddress"
                    value={restaurantData.physicalAddress}
                    onChange={handleRestaurantInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Opening Hours</label>
                    <input
                      type="time"
                      name="openingHours"
                      value={restaurantData.openingHours}
                      onChange={handleRestaurantInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Closing Hours</label>
                    <input
                      type="time"
                      name="closingHours"
                      value={restaurantData.closingHours}
                      onChange={handleRestaurantInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                  </div>
                </div>
              </div>

              {/* Business Logo */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Business Logo</label>
                <div className="flex flex-col items-center gap-4">
                  <div className="w-full aspect-square rounded-lg bg-gray-100 border-2 border-gray-300 flex items-center justify-center overflow-hidden">
                    <img
                      src="https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=200&h=200&fit=crop"
                      alt="Business Logo"
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <button
                    onClick={handleBusinessLogoChange}
                    className="w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium transition-colors"
                  >
                    Change Business Logo
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* Sales and Expense Categories */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Sales Categories */}
            <div className="bg-white rounded-lg p-6 border border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Sales Categories</h3>
              <p className="text-sm text-gray-600 mb-4">Define the items or services you sell (e.g., Meals, Drinks).</p>
              <div className="space-y-2 mb-4">
                {salesCategories.map((category, index) => (
                  <div key={index} className="flex gap-2">
                    <input
                      type="text"
                      value={category}
                      onChange={(e) => handleSalesCategory(index, e.target.value)}
                      placeholder="Enter category"
                      className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
                    />
                    {salesCategories.length > 1 && (
                      <button
                        onClick={() => removeSalesCategory(index)}
                        className="px-3 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                ))}
              </div>
              <button
                onClick={addSalesCategory}
                className="w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium transition-colors flex items-center justify-center gap-2"
              >
                <Plus className="w-4 h-4" />
                Add
              </button>
            </div>

            {/* Expense Categories */}
            <div className="bg-white rounded-lg p-6 border border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Expense Categories</h3>
              <p className="text-sm text-gray-600 mb-4">Define the items or services you sell (e.g., Meals, Drinks).</p>
              <div className="space-y-2 mb-4">
                {expenseCategories.map((category, index) => (
                  <div key={index} className="flex gap-2">
                    <input
                      type="text"
                      value={category}
                      onChange={(e) => handleExpenseCategory(index, e.target.value)}
                      placeholder="Enter category"
                      className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
                    />
                    {expenseCategories.length > 1 && (
                      <button
                        onClick={() => removeExpenseCategory(index)}
                        className="px-3 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                ))}
              </div>
              <button
                onClick={addExpenseCategory}
                className="w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium transition-colors flex items-center justify-center gap-2 mb-4"
              >
                <Plus className="w-4 h-4" />
                Add
              </button>

              {/* Require Receipt Photo Toggle */}
              <div className="flex items-center justify-between p-3 border border-gray-200 rounded-lg">
                <label className="text-sm font-medium text-gray-900">Require Receipt Photo</label>
                <button
                  onClick={() => setRequireReceiptPhoto(!requireReceiptPhoto)}
                  className={`w-12 h-6 rounded-full transition-colors ${
                    requireReceiptPhoto ? 'bg-gray-800' : 'bg-gray-300'
                  }`}
                >
                  <div
                    className={`w-5 h-5 rounded-full bg-white transition-transform ${
                      requireReceiptPhoto ? 'translate-x-6' : 'translate-x-1'
                    }`}
                  />
                </button>
              </div>
            </div>
          </div>

          {/* Danger Zone */}
          <div className="bg-white rounded-lg p-6 border border-red-200">
            <h3 className="text-lg font-semibold text-red-600 mb-4">Danger Zone</h3>
            <button className="px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-medium transition-colors">
              Delete Restaurant Account
            </button>
          </div>

          {/* Save Button */}
          <button
            onClick={handleSaveRestaurant}
            className="w-full px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium transition-colors"
          >
            Save Changes
          </button>
        </div>
      ) : activeTab === 'restaurant' ? (
        <div className="bg-white rounded-lg p-8 border border-gray-200 text-center">
          <h3 className="text-2xl font-bold text-gray-900 mb-2">Restaurant Profile</h3>
          <p className="text-gray-600">Only managers can access restaurant settings.</p>
        </div>
      ) : null}
    </div>
  );
};
