import { useParams, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import ProjectChat from './ProjectChat';
import { projectAPI, userAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

const ProjectDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [project, setProject] = useState(null);
  const [members, setMembers] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('overview');
  const [showAddMember, setShowAddMember] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState('');
  const [adding, setAdding] = useState(false);
  
  // Financials editing
  const [editingFinancials, setEditingFinancials] = useState(false);
  const [financials, setFinancials] = useState({ revenue: '', expenses: '' });
  const [savingFinancials, setSavingFinancials] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [projectRes, membersRes, usersRes] = await Promise.all([
          projectAPI.getById(id),
          projectAPI.getMembers(id),
          userAPI.getAll()
        ]);
        setProject(projectRes.data);
        setMembers(membersRes.data);
        setAllUsers(usersRes.data);
        setFinancials({
          revenue: projectRes.data.revenue || 0,
          expenses: projectRes.data.expenses || 0
        });
      } catch (err) {
        console.error('Failed to fetch project details:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const handleAddMember = async () => {
    if (!selectedUserId) return;
    
    try {
      setAdding(true);
      await projectAPI.addMember(id, parseInt(selectedUserId));
      const res = await projectAPI.getMembers(id);
      setMembers(res.data);
      setShowAddMember(false);
      setSelectedUserId('');
    } catch (err) {
      console.error('Failed to add member:', err);
      alert(err.response?.data?.message || 'Failed to add member');
    } finally {
      setAdding(false);
    }
  };

  const handleRemoveMember = async (userId) => {
    if (!confirm('Are you sure you want to remove this member?')) return;
    
    try {
      await projectAPI.removeMember(id, userId);
      const res = await projectAPI.getMembers(id);
      setMembers(res.data);
    } catch (err) {
      console.error('Failed to remove member:', err);
      alert(err.response?.data?.message || 'Failed to remove member');
    }
  };

  const handleSaveFinancials = async () => {
    try {
      setSavingFinancials(true);
      await projectAPI.updateFinancials(id, {
        revenue: parseFloat(financials.revenue) || 0,
        expenses: parseFloat(financials.expenses) || 0
      });
      const res = await projectAPI.getById(id);
      setProject(res.data);
      setEditingFinancials(false);
    } catch (err) {
      console.error('Failed to update financials:', err);
      alert('Failed to update financials');
    } finally {
      setSavingFinancials(false);
    }
  };

  const handleDeleteProject = async () => {
    if (!confirm('Are you sure you want to delete this project? This action cannot be undone.')) return;
    
    try {
      await projectAPI.delete(id);
      navigate('/projects');
    } catch (err) {
      console.error('Failed to delete project:', err);
      alert('Failed to delete project');
    }
  };

  // Filter users who are not already members
  const availableUsers = allUsers.filter(
    u => !members.some(m => m.userId === u.id)
  );

  const isManager = user?.role === 'MANAGER';
  
  const formatCurrency = (amount) => {
    if (amount == null) return '$0';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(amount);
  };

  const getStatusColor = (status) => {
    const colors = {
      PLANNING: 'bg-yellow-100 text-yellow-700 border-yellow-200',
      IN_PROGRESS: 'bg-blue-100 text-blue-700 border-blue-200',
      COMPLETED: 'bg-green-100 text-green-700 border-green-200',
      ON_HOLD: 'bg-gray-100 text-gray-700 border-gray-200'
    };
    return colors[status] || 'bg-gray-100 text-gray-700 border-gray-200';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="text-center py-12">
        <h2 className="text-xl font-semibold text-gray-900">Project not found</h2>
        <button onClick={() => navigate('/projects')} className="mt-4 text-indigo-600 hover:underline">
          Back to Projects
        </button>
      </div>
    );
  }

  const profit = (project.revenue || 0) - (project.expenses || 0);
  const budgetUsed = project.budget > 0 ? ((project.expenses || 0) / project.budget) * 100 : 0;

  return (
    <div className="max-w-7xl mx-auto">
      {/* Back Button */}
      <button
        onClick={() => navigate('/projects')}
        className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6 transition-colors"
      >
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
        Back to Projects
      </button>

      {/* Project Header */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 rounded-xl p-6 mb-6 text-white shadow-lg">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-2xl font-bold">{project.name}</h1>
            {project.description && (
              <p className="mt-2 text-indigo-200">{project.description}</p>
            )}
          </div>
          {isManager && (
            <button
              onClick={handleDeleteProject}
              className="p-2 bg-white/10 hover:bg-white/20 rounded-lg transition-colors"
              title="Delete Project"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          )}
        </div>
        <div className="flex flex-wrap items-center gap-3 mt-4 text-sm">
          <span className={`px-3 py-1 rounded-full border ${getStatusColor(project.status)}`}>
            {project.status?.replace('_', ' ')}
          </span>
          {project.manager && (
            <span className="bg-white/20 px-3 py-1 rounded-full">
              Manager: {project.manager.name}
            </span>
          )}
          <span className="bg-white/20 px-3 py-1 rounded-full">
            {members.length} Members
          </span>
          {project.startDate && (
            <span className="bg-white/20 px-3 py-1 rounded-full">
              Started: {new Date(project.startDate).toLocaleDateString()}
            </span>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-2 mb-6 bg-gray-100 p-1 rounded-lg w-fit">
        {['overview', 'chat', 'members'].map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-6 py-2 rounded-lg font-medium transition-all capitalize ${
              activeTab === tab
                ? 'bg-white text-indigo-600 shadow-sm'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            {tab === 'overview' && (
              <div className="flex items-center gap-2">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
                Overview
              </div>
            )}
            {tab === 'chat' && (
              <div className="flex items-center gap-2">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                Chat
              </div>
            )}
            {tab === 'members' && (
              <div className="flex items-center gap-2">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
                Members ({members.length})
              </div>
            )}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {activeTab === 'overview' && (
        <div className="grid gap-6 lg:grid-cols-2">
          {/* Financial Overview */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-gray-900">Financial Overview</h3>
              {isManager && !editingFinancials && (
                <button
                  onClick={() => setEditingFinancials(true)}
                  className="text-indigo-600 hover:text-indigo-700 text-sm font-medium"
                >
                  Edit
                </button>
              )}
            </div>

            {editingFinancials ? (
              <div className="space-y-4">
                <div>
                  <label className="block text-sm text-gray-600 mb-1">Revenue ($)</label>
                  <input
                    type="number"
                    value={financials.revenue}
                    onChange={(e) => setFinancials({ ...financials, revenue: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                    min="0"
                    step="0.01"
                  />
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1">Expenses ($)</label>
                  <input
                    type="number"
                    value={financials.expenses}
                    onChange={(e) => setFinancials({ ...financials, expenses: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                    min="0"
                    step="0.01"
                  />
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={handleSaveFinancials}
                    disabled={savingFinancials}
                    className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50"
                  >
                    {savingFinancials ? 'Saving...' : 'Save'}
                  </button>
                  <button
                    onClick={() => {
                      setEditingFinancials(false);
                      setFinancials({ revenue: project.revenue || 0, expenses: project.expenses || 0 });
                    }}
                    className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="p-4 bg-indigo-50 rounded-xl">
                    <p className="text-sm text-indigo-600 font-medium">Budget</p>
                    <p className="text-2xl font-bold text-indigo-700">{formatCurrency(project.budget)}</p>
                  </div>
                  <div className="p-4 bg-green-50 rounded-xl">
                    <p className="text-sm text-green-600 font-medium">Revenue</p>
                    <p className="text-2xl font-bold text-green-700">{formatCurrency(project.revenue)}</p>
                  </div>
                  <div className="p-4 bg-red-50 rounded-xl">
                    <p className="text-sm text-red-600 font-medium">Expenses</p>
                    <p className="text-2xl font-bold text-red-700">{formatCurrency(project.expenses)}</p>
                  </div>
                  <div className={`p-4 rounded-xl ${profit >= 0 ? 'bg-emerald-50' : 'bg-red-50'}`}>
                    <p className={`text-sm font-medium ${profit >= 0 ? 'text-emerald-600' : 'text-red-600'}`}>
                      Profit/Loss
                    </p>
                    <p className={`text-2xl font-bold ${profit >= 0 ? 'text-emerald-700' : 'text-red-700'}`}>
                      {formatCurrency(profit)}
                    </p>
                  </div>
                </div>

                {/* Budget Progress */}
                {project.budget > 0 && (
                  <div>
                    <div className="flex justify-between text-sm mb-2">
                      <span className="text-gray-600">Budget Used</span>
                      <span className={`font-medium ${budgetUsed > 100 ? 'text-red-600' : 'text-gray-900'}`}>
                        {budgetUsed.toFixed(1)}%
                      </span>
                    </div>
                    <div className="h-3 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full transition-all ${
                          budgetUsed > 100 ? 'bg-red-500' : budgetUsed > 80 ? 'bg-yellow-500' : 'bg-indigo-500'
                        }`}
                        style={{ width: `${Math.min(budgetUsed, 100)}%` }}
                      />
                    </div>
                    <p className="text-xs text-gray-500 mt-1">
                      {formatCurrency(project.expenses)} of {formatCurrency(project.budget)} spent
                    </p>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Project Details */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-6">Project Details</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between py-3 border-b border-gray-100">
                <span className="text-gray-500">Status</span>
                <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(project.status)}`}>
                  {project.status?.replace('_', ' ')}
                </span>
              </div>
              <div className="flex items-center justify-between py-3 border-b border-gray-100">
                <span className="text-gray-500">Project Manager</span>
                <span className="font-medium text-gray-900">{project.manager?.name || 'Not assigned'}</span>
              </div>
              <div className="flex items-center justify-between py-3 border-b border-gray-100">
                <span className="text-gray-500">Start Date</span>
                <span className="font-medium text-gray-900">
                  {project.startDate ? new Date(project.startDate).toLocaleDateString() : 'Not set'}
                </span>
              </div>
              <div className="flex items-center justify-between py-3 border-b border-gray-100">
                <span className="text-gray-500">End Date</span>
                <span className="font-medium text-gray-900">
                  {project.endDate ? new Date(project.endDate).toLocaleDateString() : 'Not set'}
                </span>
              </div>
              <div className="flex items-center justify-between py-3">
                <span className="text-gray-500">Team Size</span>
                <span className="font-medium text-gray-900">{members.length} members</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'chat' && (
        <ProjectChat projectId={id} />
      )}

      {activeTab === 'members' && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          {/* Members Header */}
          <div className="p-4 border-b border-gray-200 flex items-center justify-between">
            <h3 className="text-lg font-semibold text-gray-800">Project Members</h3>
            {isManager && (
              <button
                onClick={() => setShowAddMember(!showAddMember)}
                className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors flex items-center gap-2 text-sm font-medium"
              >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
                Add Member
              </button>
            )}
          </div>

          {/* Add Member Form */}
          {showAddMember && (
            <div className="p-4 bg-gray-50 border-b border-gray-200">
              <div className="flex gap-3 items-end">
                <div className="flex-1">
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Select User
                  </label>
                  <select
                    value={selectedUserId}
                    onChange={(e) => setSelectedUserId(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  >
                    <option value="">Choose a user...</option>
                    {availableUsers.map(u => (
                      <option key={u.id} value={u.id}>
                        {u.name} ({u.email})
                      </option>
                    ))}
                  </select>
                </div>
                <button
                  onClick={handleAddMember}
                  disabled={!selectedUserId || adding}
                  className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                >
                  {adding ? (
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                  ) : (
                    <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                  )}
                  Add
                </button>
                <button
                  onClick={() => {
                    setShowAddMember(false);
                    setSelectedUserId('');
                  }}
                  className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-colors"
                >
                  Cancel
                </button>
              </div>
            </div>
          )}

          {/* Members List */}
          <div className="divide-y divide-gray-100">
            {members.length === 0 ? (
              <div className="p-8 text-center text-gray-500">
                <svg className="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
                <p>No members yet. Add team members to start collaborating!</p>
              </div>
            ) : (
              members.map(member => (
                <div key={member.id} className="p-4 flex items-center justify-between hover:bg-gray-50 transition-colors">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-500 rounded-full flex items-center justify-center">
                      <span className="text-white text-lg font-medium">
                        {member.userName?.charAt(0).toUpperCase()}
                      </span>
                    </div>
                    <div>
                      <h4 className="font-medium text-gray-900">{member.userName}</h4>
                      <p className="text-sm text-gray-500">{member.userEmail}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                      member.role === 'OWNER' 
                        ? 'bg-purple-100 text-purple-700'
                        : member.role === 'ADMIN'
                        ? 'bg-blue-100 text-blue-700'
                        : 'bg-gray-100 text-gray-700'
                    }`}>
                      {member.role}
                    </span>
                    {isManager && member.role !== 'OWNER' && (
                      <button
                        onClick={() => handleRemoveMember(member.userId)}
                        className="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                        title="Remove member"
                      >
                        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ProjectDetails;
