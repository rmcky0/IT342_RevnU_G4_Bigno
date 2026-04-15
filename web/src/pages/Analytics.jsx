import { useState, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';
import {
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import { analyticsAPI } from '../api/financialAPI';

export const Analytics = () => {
  const [selectedDateRange, setSelectedDateRange] = useState('today');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // State for analytics data
  const [totalSales, setTotalSales] = useState(0);
  const [totalExpenses, setTotalExpenses] = useState(0);
  const [netProfit, setNetProfit] = useState(0);
  const [salesTrendData, setSalesTrendData] = useState([]);
  const [salesByCategory, setSalesByCategory] = useState([]);
  const [salesSummaryData, setSalesSummaryData] = useState([]);
  const [profitTrendData, setProfitTrendData] = useState([]);

  // Get date range for queries
  const getTodayDateRange = () => {
    const today = new Date().toISOString().split('T')[0];
    const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
    return { today, yesterday };
  };

  // Load all analytics data
  useEffect(() => {
    loadAnalyticsData();
  }, [selectedDateRange]);

  const loadAnalyticsData = async () => {
    setLoading(true);
    setError('');
    try {
      const { today, yesterday } = getTodayDateRange();

      // Fetch daily summary
      const summary = await analyticsAPI.getDailySummary(today);
      setTotalSales(summary.totalSales || 0);
      setTotalExpenses(summary.totalExpenses || 0);
      setNetProfit((summary.totalSales || 0) - (summary.totalExpenses || 0));

      // Fetch sales trend (today vs yesterday)
      const trendData = await analyticsAPI.getSalesTrend(yesterday, today);
      setSalesTrendData(formatTrendData(trendData));

      // Fetch sales by category
      const categoryData = await analyticsAPI.getSalesByCategory(today);
      setSalesByCategory(formatCategoryData(categoryData));

      // Fetch sales summary
      const summaryData = await analyticsAPI.getSalesSummary(today, today);
      setSalesSummaryData(formatSummaryData(summaryData));

      // Fetch profit trend
      const profitData = await analyticsAPI.getProfitTrend(yesterday, today);
      setProfitTrendData(formatTrendData(profitData));
    } catch (err) {
      setError('Failed to load analytics: ' + (err.response?.data?.message || err.message));
      // Set fallback mock data if API fails
      setFallbackData();
    } finally {
      setLoading(false);
    }
  };

  const formatTrendData = (data) => {
    if (Array.isArray(data)) {
      return data.map(item => ({
        time: item.time || item.timeSlot || '12:00 PM',
        today: item.today || item.amount || 0,
        yesterday: item.yesterday || 0,
      }));
    }
    return [];
  };

  const formatCategoryData = (data) => {
    if (Array.isArray(data)) {
      const colors = ['#4F46E5', '#FBBF24', '#EC4899', '#22C55E', '#EF553B', '#00CC96'];
      return data.map((item, index) => ({
        name: item.category || item.name || 'Unknown',
        value: item.totalAmount || item.value || 0,
        color: colors[index % colors.length],
      }));
    }
    return [];
  };

  const formatSummaryData = (data) => {
    if (Array.isArray(data)) {
      return data.map(item => ({
        time: item.time || item.timeSlot || '12:00 PM',
        sales: item.sales || item.amount || 0,
        profit: item.profit || (item.amount || 0) * 0.7,
      }));
    }
    return [];
  };

  const setFallbackData = () => {
    setTotalSales(12450.00);
    setTotalExpenses(4200.00);
    setNetProfit(8250.00);

    setSalesTrendData([
      { time: '10:00 AM', today: 90, yesterday: 85 },
      { time: '12:00 PM', today: 95, yesterday: 88 },
      { time: '2:00 PM', today: 65, yesterday: 75 },
      { time: '4:00 PM', today: 75, yesterday: 82 },
    ]);

    setSalesByCategory([
      { name: 'Food', value: 45, color: '#4F46E5' },
      { name: 'Drinks', value: 30, color: '#FBBF24' },
      { name: 'Necessities', value: 20, color: '#EC4899' },
      { name: 'Others', value: 5, color: '#22C55E' },
    ]);

    setSalesSummaryData([
      { time: '10:00', sales: 85, profit: 65 },
      { time: '12:00', sales: 90, profit: 70 },
      { time: '2:00', sales: 70, profit: 50 },
    ]);

    setProfitTrendData([
      { time: '10:00', today: 80, yesterday: 75 },
      { time: '12:00', today: 85, yesterday: 78 },
      { time: '2:00', today: 65, yesterday: 72 },
    ]);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-gray-500">Loading analytics...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
        {error}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <p className="text-gray-600 text-sm mb-2">Total Sales (Today)</p>
          <div className="flex items-start gap-3">
            <span className="text-2xl">₱</span>
            <span className="text-4xl font-bold text-gray-900">{totalSales.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
          </div>
        </div>
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <p className="text-gray-600 text-sm mb-2">Total Expenses (Today)</p>
          <div className="flex items-start gap-3">
            <span className="text-2xl">₱</span>
            <span className="text-4xl font-bold text-gray-900">{totalExpenses.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
          </div>
        </div>
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <p className="text-gray-600 text-sm mb-2">Net Profit</p>
          <div className="flex items-start gap-3">
            <span className="text-2xl">₱</span>
            <span className="text-4xl font-bold text-green-600">{netProfit.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
          </div>
        </div>
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Sales Trend */}
        <div className="lg:col-span-2 bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900">Sales Trend</h3>
            <div className="flex items-center gap-2 text-sm">
              <span className="text-gray-600">Today</span>
              <span className="text-gray-400">vs</span>
              <button className="flex items-center gap-1 text-gray-600 hover:text-gray-900">
                <span className="inline-block w-2 h-2 bg-yellow-400 rounded-full"></span>
                Yesterday, 27 Feb
                <ChevronDown className="w-4 h-4" />
              </button>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <LineChart data={salesTrendData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
              <XAxis dataKey="time" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="today" 
                stroke="#EC4899" 
                strokeWidth={3}
                dot={{ fill: '#EC4899', r: 4 }}
                name="Today"
              />
              <Line 
                type="monotone" 
                dataKey="yesterday" 
                stroke="#FBBF24" 
                strokeWidth={3}
                dot={{ fill: '#FBBF24', r: 4 }}
                name="Yesterday"
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Sales by Category */}
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900 mb-8">Sales by Category</h3>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie
                data={salesByCategory}
                cx="50%"
                cy="50%"
                innerRadius={50}
                outerRadius={80}
                paddingAngle={2}
                dataKey="value"
              >
                {salesByCategory.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
            </PieChart>
          </ResponsiveContainer>
          <div className="space-y-3 mt-6">
            {salesByCategory.map(item => (
              <div key={item.name} className="flex items-center gap-2">
                <div
                  className="w-3 h-3 rounded-full"
                  style={{ backgroundColor: item.color }}
                />
                <span className="text-sm text-gray-600">{item.name}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Sales Summary */}
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900">Sales Summary</h3>
            <button className="flex items-center gap-1 text-sm text-gray-600 hover:text-gray-900">
              Daily
              <ChevronDown className="w-4 h-4" />
            </button>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={salesSummaryData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
              <XAxis dataKey="time" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="sales" fill="#EC4899" name="Sales" />
              <Bar dataKey="profit" fill="#FBBF24" name="Profit" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Profit Trend */}
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900">Profit Trend</h3>
            <button className="flex items-center gap-1 text-sm text-gray-600 hover:text-gray-900">
              Daily
              <ChevronDown className="w-4 h-4" />
            </button>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={profitTrendData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
              <XAxis dataKey="time" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="today" 
                stroke="#EC4899" 
                strokeWidth={3}
                dot={{ fill: '#EC4899', r: 4 }}
                name="Today"
              />
              <Line 
                type="monotone" 
                dataKey="yesterday" 
                stroke="#FBBF24" 
                strokeWidth={3}
                dot={{ fill: '#FBBF24', r: 4 }}
                name="Yesterday"
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};
