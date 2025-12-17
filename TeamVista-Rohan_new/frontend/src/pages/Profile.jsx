import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userAPI, taskAPI } from '../services/api';
import { 
  FiMail, FiBriefcase, FiCalendar, FiEdit2, FiSave, FiX,
  FiCheckCircle, FiClock, FiTarget, FiTrendingUp, FiAward
} from 'react-icons/fi';
import { BarChart, Bar, ResponsiveContainer, XAxis, YAxis, Tooltip } from 'recharts';

const Profile = () => {
  const { user } = useAuth();
  const [userStats, setUserStats] = useState(null);
  const [userTasks, setUserTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [editedProfile, setEditedProfile] = useState({
    name: user?.name || '',
    email: user?.email || '',
  });

  useEffect(() => {
    fetchUserData();
  }, [user]);

  const fetchUserData = async () => {
    try {
      const [statsResponse, tasksResponse] = await Promise.all([
        userAPI.getStats(user.id),
        taskAPI.getAll({ assignedTo: user.id })
      ]);
      setUserStats(statsResponse.data.data);
      setUserTasks(tasksResponse.data.data);
    } catch (error) {
      console.error('Error fetching user data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = () => {
    // TODO: Implement profile update API
    setIsEditing(false);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const recentTasks = userTasks.slice(0, 5);
  
  // Prepare chart data
  const tasksByStatus = [
    { name: 'Completed', value: userStats?.completedTasks || 0, color: '#22c55e' },
    { name: 'In Progress', value: userStats?.inProgressTasks || 0, color: '#f59e0b' },
    { name: 'Pending', value: userStats?.pendingTasks || 0, color: '#6b7280' },
  ];

  return (
    <div className="space-y-6">
      {/* Profile Header */}
      <div className="card bg-gradient-to-r from-primary-500 to-accent-500 text-white">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-6">
            {/* Avatar */}
            <div className="w-24 h-24 bg-white rounded-full flex items-center justify-center text-primary-600 text-4xl font-bold shadow-lg">
              {user?.name?.charAt(0).toUpperCase()}
            </div>
            
            {/* User Info */}
            <div>
              {isEditing ? (
                <div className="space-y-2">
                  <input
                    type="text"
                    value={editedProfile.name}
                    onChange={(e) => setEditedProfile({ ...editedProfile, name: e.target.value })}
                    className="px-3 py-2 rounded-lg text-gray-900 font-semibold text-2xl"
                  />
                  <input
                    type="email"
                    value={editedProfile.email}
                    onChange={(e) => setEditedProfile({ ...editedProfile, email: e.target.value })}
                    className="px-3 py-2 rounded-lg text-gray-900"
                  />
                </div>
              ) : (
                <>
                  <h1 className="text-3xl font-bold mb-2">{user?.name}</h1>
                  <div className="flex items-center space-x-4 text-primary-100">
                    <div className="flex items-center space-x-2">
                      <FiMail size={16} />
                      <span>{user?.email}</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <FiBriefcase size={16} />
                      <span className="px-3 py-1 bg-white bg-opacity-20 rounded-full text-sm font-medium">
                        {user?.role}
                      </span>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2 mt-2 text-primary-100">
                    <FiCalendar size={16} />
                    <span className="text-sm">
                      Member since {new Date(user?.createdAt).toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
                    </span>
                  </div>
                </>
              )}
            </div>
          </div>

          {/* Edit Button */}
          <div className="flex space-x-2">
            {isEditing ? (
              <>
                <button
                  onClick={handleSave}
                  className="p-2 bg-white text-primary-600 rounded-lg hover:bg-primary-50 transition-colors"
                >
                  <FiSave size={20} />
                </button>
                <button
                  onClick={() => setIsEditing(false)}
                  className="p-2 bg-white bg-opacity-20 rounded-lg hover:bg-opacity-30 transition-colors"
                >
                  <FiX size={20} />
                </button>
              </>
            ) : (
              <button
                onClick={() => setIsEditing(true)}
                className="p-2 bg-white bg-opacity-20 rounded-lg hover:bg-opacity-30 transition-colors"
              >
                <FiEdit2 size={20} />
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="card bg-gradient-to-br from-primary-50 to-primary-100 border-primary-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-primary-700 font-medium mb-1">Total Tasks</p>
              <p className="text-3xl font-bold text-primary-900">{userStats?.totalTasks || 0}</p>
            </div>
            <div className="p-3 bg-primary-500 rounded-lg">
              <FiTarget className="text-2xl text-white" />
            </div>
          </div>
        </div>

        <div className="card bg-gradient-to-br from-success-50 to-success-100 border-success-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-success-700 font-medium mb-1">Completed</p>
              <p className="text-3xl font-bold text-success-900">{userStats?.completedTasks || 0}</p>
            </div>
            <div className="p-3 bg-success-500 rounded-lg">
              <FiCheckCircle className="text-2xl text-white" />
            </div>
          </div>
        </div>

        <div className="card bg-gradient-to-br from-warning-50 to-warning-100 border-warning-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-warning-700 font-medium mb-1">In Progress</p>
              <p className="text-3xl font-bold text-warning-900">{userStats?.inProgressTasks || 0}</p>
            </div>
            <div className="p-3 bg-warning-500 rounded-lg">
              <FiClock className="text-2xl text-white" />
            </div>
          </div>
        </div>

        <div className="card bg-gradient-to-br from-accent-50 to-accent-100 border-accent-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-accent-700 font-medium mb-1">Completion Rate</p>
              <p className="text-3xl font-bold text-accent-900">{userStats?.completionRate?.toFixed(1) || 0}%</p>
            </div>
            <div className="p-3 bg-accent-500 rounded-lg">
              <FiAward className="text-2xl text-white" />
            </div>
          </div>
        </div>
      </div>

      {/* Performance Chart */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center space-x-2">
          <FiTrendingUp className="text-primary-600" />
          <span>My Performance</span>
        </h3>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={tasksByStatus}>
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="value" fill="#0ea5e9" radius={[8, 8, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Recent Activity */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Tasks</h3>
        <div className="space-y-3">
          {recentTasks.length > 0 ? (
            recentTasks.map((task) => (
              <div
                key={task.id}
                className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
              >
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900">{task.title}</h4>
                  <p className="text-sm text-gray-600 mt-1">{task.description}</p>
                  {task.dueDate && (
                    <p className="text-xs text-gray-500 mt-1">
                      Due: {new Date(task.dueDate).toLocaleDateString()}
                    </p>
                  )}
                </div>
                <div className="flex items-center space-x-2">
                  <span className={`badge badge-${task.status.toLowerCase().replace('_', '-')}`}>
                    {task.status.replace('_', ' ')}
                  </span>
                  <span className={`badge badge-${task.priority.toLowerCase()}`}>
                    {task.priority}
                  </span>
                </div>
              </div>
            ))
          ) : (
            <p className="text-center text-gray-500 py-8">No tasks assigned yet</p>
          )}
        </div>
      </div>

      {/* Achievements Section */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center space-x-2">
          <FiAward className="text-accent-600" />
          <span>Achievements</span>
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {userStats?.completedTasks >= 10 && (
            <div className="p-4 bg-gradient-to-br from-accent-50 to-accent-100 rounded-lg border-2 border-accent-200">
              <div className="text-4xl mb-2">🏆</div>
              <h4 className="font-semibold text-accent-900">Task Master</h4>
              <p className="text-sm text-accent-700">Completed 10+ tasks</p>
            </div>
          )}
          
          {userStats?.completionRate >= 80 && (
            <div className="p-4 bg-gradient-to-br from-success-50 to-success-100 rounded-lg border-2 border-success-200">
              <div className="text-4xl mb-2">⭐</div>
              <h4 className="font-semibold text-success-900">High Performer</h4>
              <p className="text-sm text-success-700">80%+ completion rate</p>
            </div>
          )}
          
          {userStats?.totalTasks >= 20 && (
            <div className="p-4 bg-gradient-to-br from-primary-50 to-primary-100 rounded-lg border-2 border-primary-200">
              <div className="text-4xl mb-2">🚀</div>
              <h4 className="font-semibold text-primary-900">Productivity Pro</h4>
              <p className="text-sm text-primary-700">Handled 20+ tasks</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;
