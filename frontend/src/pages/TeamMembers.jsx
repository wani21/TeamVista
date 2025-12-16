import { useState, useEffect } from 'react';
import { userAPI } from '../services/api';
import { FiMail, FiBriefcase, FiCalendar, FiTrendingUp, FiCheckCircle } from 'react-icons/fi';

const TeamMembers = () => {
  const [users, setUsers] = useState([]);
  const [userStats, setUserStats] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTeamData();
  }, []);

  const fetchTeamData = async () => {
    try {
      const usersResponse = await userAPI.getAll();
      const usersData = usersResponse.data.data;
      setUsers(usersData);

      // Fetch stats for each user
      const statsPromises = usersData.map(user => 
        userAPI.getStats(user.id).catch(() => null)
      );
      const statsResponses = await Promise.all(statsPromises);
      
      const statsMap = {};
      statsResponses.forEach((response, index) => {
        if (response) {
          statsMap[usersData[index].id] = response.data.data;
        }
      });
      setUserStats(statsMap);
    } catch (error) {
      console.error('Error fetching team data:', error);
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

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Team Members</h1>
        <p className="text-gray-600 mt-1">View and manage your team</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {users.map((user) => {
          const stats = userStats[user.id];
          return (
            <div key={user.id} className="card hover:shadow-lg transition-shadow duration-200">
              {/* User Header */}
              <div className="flex items-center space-x-4 mb-4">
                <div className="w-16 h-16 bg-gradient-to-br from-primary-500 to-accent-500 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                  {user.name.charAt(0).toUpperCase()}
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900">{user.name}</h3>
                  <span className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${
                    user.role === 'MANAGER' 
                      ? 'bg-accent-100 text-accent-700' 
                      : 'bg-primary-100 text-primary-700'
                  }`}>
                    {user.role}
                  </span>
                </div>
              </div>

              {/* User Details */}
              <div className="space-y-2 mb-4">
                <div className="flex items-center space-x-2 text-sm text-gray-600">
                  <FiMail size={16} />
                  <span>{user.email}</span>
                </div>
                <div className="flex items-center space-x-2 text-sm text-gray-600">
                  <FiCalendar size={16} />
                  <span>Joined {new Date(user.createdAt).toLocaleDateString()}</span>
                </div>
              </div>

              {/* Stats */}
              {stats && (
                <div className="border-t border-gray-200 pt-4">
                  <div className="grid grid-cols-2 gap-3">
                    <div className="text-center p-3 bg-primary-50 rounded-lg">
                      <p className="text-2xl font-bold text-primary-700">{stats.totalTasks}</p>
                      <p className="text-xs text-primary-600">Total Tasks</p>
                    </div>
                    <div className="text-center p-3 bg-success-50 rounded-lg">
                      <p className="text-2xl font-bold text-success-700">{stats.completedTasks}</p>
                      <p className="text-xs text-success-600">Completed</p>
                    </div>
                  </div>
                  
                  <div className="mt-3">
                    <div className="flex justify-between items-center mb-1">
                      <span className="text-xs text-gray-600">Completion Rate</span>
                      <span className="text-xs font-semibold text-primary-600">
                        {stats.completionRate.toFixed(1)}%
                      </span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div
                        className="bg-gradient-to-r from-primary-500 to-accent-500 h-2 rounded-full transition-all duration-500"
                        style={{ width: `${stats.completionRate}%` }}
                      ></div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default TeamMembers;
