import { useState, useEffect } from 'react';
import { dashboardAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { 
  FiCheckCircle, FiClock, FiTrendingUp, FiAlertCircle, 
  FiUsers, FiTarget, FiActivity, FiAward 
} from 'react-icons/fi';
import { BarChart, Bar, PieChart, Pie, Cell, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend } from 'recharts';

const Dashboard = () => {
  const { user, isManager } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboard();
  }, [user]);

  const fetchDashboard = async () => {
    try {
      // Managers see full team dashboard, Employees see personal dashboard
      const response = isManager() 
        ? await dashboardAPI.getEnhanced()
        : await dashboardAPI.getPersonal();
      setDashboard(response.data.data);
    } catch (error) {
      console.error('Failed to fetch dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const stats = [
    {
      label: 'Total Tasks',
      value: dashboard?.totalTasks || 0,
      icon: FiTarget,
      color: 'bg-primary-500',
      bgColor: 'bg-primary-50',
      textColor: 'text-primary-700',
    },
    {
      label: 'Completed',
      value: dashboard?.completedTasks || 0,
      icon: FiCheckCircle,
      color: 'bg-success-500',
      bgColor: 'bg-success-50',
      textColor: 'text-success-700',
    },
    {
      label: 'In Progress',
      value: dashboard?.inProgressTasks || 0,
      icon: FiActivity,
      color: 'bg-warning-500',
      bgColor: 'bg-warning-50',
      textColor: 'text-warning-700',
    },
    {
      label: 'Pending',
      value: dashboard?.pendingTasks || 0,
      icon: FiClock,
      color: 'bg-gray-500',
      bgColor: 'bg-gray-50',
      textColor: 'text-gray-700',
    },
    {
      label: 'Overdue',
      value: dashboard?.overdueTasks || 0,
      icon: FiAlertCircle,
      color: 'bg-danger-500',
      bgColor: 'bg-danger-50',
      textColor: 'text-danger-700',
    },
    {
      label: 'High Priority',
      value: dashboard?.highPriorityTasks || 0,
      icon: FiTrendingUp,
      color: 'bg-accent-500',
      bgColor: 'bg-accent-50',
      textColor: 'text-accent-700',
    },
  ];

  // Prepare data for charts
  const taskStatusData = [
    { name: 'Completed', value: dashboard?.completedTasks || 0, color: '#22c55e' },
    { name: 'In Progress', value: dashboard?.inProgressTasks || 0, color: '#f59e0b' },
    { name: 'Pending', value: dashboard?.pendingTasks || 0, color: '#6b7280' },
  ];

  const userProductivityData = dashboard?.userStats?.map(stat => ({
    name: stat.userName.split(' ')[0],
    completed: stat.completedTasks,
    pending: stat.pendingTasks,
    inProgress: stat.inProgressTasks,
    rate: stat.completionRate.toFixed(1),
  })) || [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">
            Welcome back, {user?.name?.split(' ')[0]}! 👋
          </h1>
          <p className="text-gray-600 mt-1">Here's what's happening with your team today.</p>
        </div>
        <div className="flex items-center space-x-2 px-4 py-2 bg-gradient-to-r from-primary-500 to-accent-500 text-white rounded-lg shadow-md">
          <FiAward className="text-xl" />
          <div>
            <p className="text-xs opacity-90">On-time Rate</p>
            <p className="text-lg font-bold">{dashboard?.onTimeCompletionPercent?.toFixed(1)}%</p>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        {stats.map((stat, index) => (
          <div
            key={index}
            className="card hover:scale-105 transform transition-all duration-200 cursor-pointer"
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">{stat.label}</p>
                <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
              </div>
              <div className={`${stat.bgColor} p-3 rounded-lg`}>
                <stat.icon className={`text-2xl ${stat.textColor}`} />
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Task Status Distribution */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Task Status Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={taskStatusData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {taskStatusData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Team Productivity */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Team Productivity</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={userProductivityData}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="completed" fill="#22c55e" name="Completed" />
              <Bar dataKey="inProgress" fill="#f59e0b" name="In Progress" />
              <Bar dataKey="pending" fill="#6b7280" name="Pending" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Team Members Stats */}
      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-lg font-semibold text-gray-900 flex items-center space-x-2">
            <FiUsers className="text-primary-600" />
            <span>Team Performance</span>
          </h3>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {dashboard?.userStats?.map((stat, index) => (
            <div
              key={index}
              className="p-4 bg-gradient-to-br from-gray-50 to-white border border-gray-200 rounded-lg hover:shadow-md transition-shadow duration-200"
            >
              <div className="flex items-center space-x-3 mb-3">
                <div className="w-12 h-12 bg-gradient-to-br from-primary-500 to-accent-500 rounded-full flex items-center justify-center text-white font-bold text-lg">
                  {stat.userName.charAt(0)}
                </div>
                <div>
                  <h4 className="font-semibold text-gray-900">{stat.userName}</h4>
                  <p className="text-xs text-gray-500">{stat.totalTasks} total tasks</p>
                </div>
              </div>
              
              <div className="space-y-2">
                <div className="flex justify-between items-center">
                  <span className="text-sm text-gray-600">Completion Rate</span>
                  <span className="font-semibold text-primary-600">{stat.completionRate.toFixed(1)}%</span>
                </div>
                
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-gradient-to-r from-primary-500 to-accent-500 h-2 rounded-full transition-all duration-500"
                    style={{ width: `${stat.completionRate}%` }}
                  ></div>
                </div>
                
                <div className="grid grid-cols-3 gap-2 mt-3 text-center">
                  <div className="bg-success-50 rounded-lg p-2">
                    <p className="text-xs text-success-600 font-medium">Completed</p>
                    <p className="text-lg font-bold text-success-700">{stat.completedTasks}</p>
                  </div>
                  <div className="bg-warning-50 rounded-lg p-2">
                    <p className="text-xs text-warning-600 font-medium">In Progress</p>
                    <p className="text-lg font-bold text-warning-700">{stat.inProgressTasks}</p>
                  </div>
                  <div className="bg-gray-50 rounded-lg p-2">
                    <p className="text-xs text-gray-600 font-medium">Pending</p>
                    <p className="text-lg font-bold text-gray-700">{stat.pendingTasks}</p>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Quick Insights */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="card bg-gradient-to-br from-primary-50 to-primary-100 border-primary-200">
          <div className="flex items-center space-x-3">
            <div className="p-3 bg-primary-500 rounded-lg">
              <FiTrendingUp className="text-2xl text-white" />
            </div>
            <div>
              <p className="text-sm text-primary-700 font-medium">Productivity Trend</p>
              <p className="text-2xl font-bold text-primary-900">
                {dashboard?.onTimeCompletionPercent > 70 ? '📈 Excellent' : dashboard?.onTimeCompletionPercent > 50 ? '📊 Good' : '📉 Needs Attention'}
              </p>
            </div>
          </div>
        </div>

        <div className="card bg-gradient-to-br from-success-50 to-success-100 border-success-200">
          <div className="flex items-center space-x-3">
            <div className="p-3 bg-success-500 rounded-lg">
              <FiCheckCircle className="text-2xl text-white" />
            </div>
            <div>
              <p className="text-sm text-success-700 font-medium">Completion Rate</p>
              <p className="text-2xl font-bold text-success-900">
                {dashboard?.totalTasks > 0 
                  ? ((dashboard?.completedTasks / dashboard?.totalTasks) * 100).toFixed(1) 
                  : 0}%
              </p>
            </div>
          </div>
        </div>

        <div className="card bg-gradient-to-br from-accent-50 to-accent-100 border-accent-200">
          <div className="flex items-center space-x-3">
            <div className="p-3 bg-accent-500 rounded-lg">
              <FiUsers className="text-2xl text-white" />
            </div>
            <div>
              <p className="text-sm text-accent-700 font-medium">Active Team Members</p>
              <p className="text-2xl font-bold text-accent-900">{dashboard?.userStats?.length || 0}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
