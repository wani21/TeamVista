import { useState, useEffect } from 'react';
import { taskAPI, userAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { 
  FiPlus, FiSearch, FiFilter, FiEdit2, FiTrash2, FiCheckCircle,
  FiClock, FiAlertCircle, FiX, FiCalendar, FiUser
} from 'react-icons/fi';
import TaskModal from '../components/TaskModal';
import TaskCard from '../components/TaskCard';
import Toast from '../components/Toast';
import CompleteTaskModal from '../components/CompleteTaskModal';

const Tasks = () => {
  const { user, isManager } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterPriority, setFilterPriority] = useState('');
  const [filterAssignee, setFilterAssignee] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [showCompleteModal, setShowCompleteModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [taskToComplete, setTaskToComplete] = useState(null);
  const [viewMode, setViewMode] = useState('grid'); // 'grid' or 'list'
  const [toast, setToast] = useState(null);

  useEffect(() => {
    fetchTasks();
    fetchUsers();
  }, [user]);

  const fetchTasks = async () => {
    try {
      setLoading(true);
      // If employee, only fetch their tasks. If manager, fetch all tasks.
      const params = isManager() ? {} : { assignedTo: user.id };
      const response = await taskAPI.getAll(params);
      setTasks(response.data.data);
    } catch (error) {
      console.error('Failed to fetch tasks:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await userAPI.getAll();
      setUsers(response.data.data);
    } catch (error) {
      console.error('Failed to fetch users:', error);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchTasks();
      return;
    }
    try {
      const response = await taskAPI.search(searchTerm);
      setTasks(response.data.data);
    } catch (error) {
      console.error('Search failed:', error);
    }
  };

  const handleCreateTask = () => {
    setSelectedTask(null);
    setShowModal(true);
  };

  const handleEditTask = (task) => {
    setSelectedTask(task);
    setShowModal(true);
  };

  const handleDeleteTask = async (taskId) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    
    try {
      const taskToDelete = tasks.find(t => t.id === taskId);
      await taskAPI.delete(taskId);
      setToast({ message: `Task "${taskToDelete?.title}" deleted successfully!`, type: 'success' });
      fetchTasks();
    } catch (error) {
      console.error('Failed to delete task:', error);
      setToast({ message: 'Failed to delete task', type: 'error' });
    }
  };

  const handleCompleteTask = (task) => {
    setTaskToComplete(task);
    setShowCompleteModal(true);
  };

  const submitTaskCompletion = async (taskId, completionData) => {
    try {
      const task = tasks.find(t => t.id === taskId);
      // For now, just complete the task. Later we'll save completion notes to backend
      await taskAPI.complete(taskId);
      setToast({ 
        message: `Task "${task?.title}" marked as completed! 🎉\nTime spent: ${completionData.hoursSpent} hours`, 
        type: 'success' 
      });
      setShowCompleteModal(false);
      setTaskToComplete(null);
      fetchTasks();
    } catch (error) {
      console.error('Failed to complete task:', error);
      setToast({ message: 'Failed to complete task', type: 'error' });
    }
  };

  const handleModalClose = (refresh) => {
    setShowModal(false);
    setSelectedTask(null);
    if (refresh) fetchTasks();
  };

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
  };

  // Filter tasks
  const filteredTasks = tasks.filter(task => {
    if (filterStatus && task.status !== filterStatus) return false;
    if (filterPriority && task.priority !== filterPriority) return false;
    if (filterAssignee && task.assignedTo?.id !== parseInt(filterAssignee)) return false;
    return true;
  });

  // Group tasks by status
  const tasksByStatus = {
    PENDING: filteredTasks.filter(t => t.status === 'PENDING'),
    IN_PROGRESS: filteredTasks.filter(t => t.status === 'IN_PROGRESS'),
    COMPLETED: filteredTasks.filter(t => t.status === 'COMPLETED'),
  };

  const statusConfig = {
    PENDING: { label: 'Pending', color: 'gray', icon: FiClock },
    IN_PROGRESS: { label: 'In Progress', color: 'primary', icon: FiAlertCircle },
    COMPLETED: { label: 'Completed', color: 'success', icon: FiCheckCircle },
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
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Tasks</h1>
          <p className="text-gray-600 mt-1">Manage and track your team's tasks</p>
        </div>
        {isManager() && (
          <button onClick={handleCreateTask} className="btn-primary flex items-center space-x-2">
            <FiPlus />
            <span>Create Task</span>
          </button>
        )}
      </div>

      {/* Filters and Search */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          {/* Search */}
          <div className="lg:col-span-2">
            <div className="relative">
              <FiSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                placeholder="Search tasks..."
                className="input-field pl-10"
              />
            </div>
          </div>

          {/* Status Filter */}
          <div>
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="input-field"
            >
              <option value="">All Status</option>
              <option value="PENDING">Pending</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>

          {/* Priority Filter */}
          <div>
            <select
              value={filterPriority}
              onChange={(e) => setFilterPriority(e.target.value)}
              className="input-field"
            >
              <option value="">All Priority</option>
              <option value="HIGH">High</option>
              <option value="MEDIUM">Medium</option>
              <option value="LOW">Low</option>
            </select>
          </div>

          {/* Assignee Filter */}
          <div>
            <select
              value={filterAssignee}
              onChange={(e) => setFilterAssignee(e.target.value)}
              className="input-field"
            >
              <option value="">All Assignees</option>
              {users.map(u => (
                <option key={u.id} value={u.id}>{u.name}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Active Filters */}
        {(filterStatus || filterPriority || filterAssignee || searchTerm) && (
          <div className="flex items-center space-x-2 mt-4 pt-4 border-t border-gray-200">
            <span className="text-sm text-gray-600">Active filters:</span>
            {searchTerm && (
              <span className="badge bg-primary-100 text-primary-700 flex items-center space-x-1">
                <span>Search: {searchTerm}</span>
                <button onClick={() => { setSearchTerm(''); fetchTasks(); }}>
                  <FiX size={14} />
                </button>
              </span>
            )}
            {filterStatus && (
              <span className="badge bg-primary-100 text-primary-700 flex items-center space-x-1">
                <span>Status: {filterStatus}</span>
                <button onClick={() => setFilterStatus('')}><FiX size={14} /></button>
              </span>
            )}
            {filterPriority && (
              <span className="badge bg-primary-100 text-primary-700 flex items-center space-x-1">
                <span>Priority: {filterPriority}</span>
                <button onClick={() => setFilterPriority('')}><FiX size={14} /></button>
              </span>
            )}
            {filterAssignee && (
              <span className="badge bg-primary-100 text-primary-700 flex items-center space-x-1">
                <span>Assignee: {users.find(u => u.id === parseInt(filterAssignee))?.name}</span>
                <button onClick={() => setFilterAssignee('')}><FiX size={14} /></button>
              </span>
            )}
          </div>
        )}
      </div>

      {/* Tasks Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {Object.entries(tasksByStatus).map(([status, statusTasks]) => {
          const config = statusConfig[status];
          return (
            <div key={status} className="card bg-gradient-to-br from-gray-50 to-white">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center space-x-2">
                  <config.icon className={`text-${config.color}-600`} />
                  <span className="font-semibold text-gray-900">{config.label}</span>
                </div>
                <span className={`badge badge-${config.color === 'gray' ? 'pending' : config.color === 'primary' ? 'in-progress' : 'completed'}`}>
                  {statusTasks.length}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Tasks Grid/List */}
      {filteredTasks.length === 0 ? (
        <div className="card text-center py-12">
          <div className="text-gray-400 mb-4">
            <FiCheckCircle size={48} className="mx-auto" />
          </div>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">No tasks found</h3>
          <p className="text-gray-600">
            {searchTerm || filterStatus || filterPriority || filterAssignee
              ? 'Try adjusting your filters'
              : isManager() 
                ? 'Create your first task to get started'
                : 'No tasks assigned yet'}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredTasks.map(task => (
            <TaskCard
              key={task.id}
              task={task}
              onEdit={handleEditTask}
              onDelete={handleDeleteTask}
              onComplete={handleCompleteTask}
              isManager={isManager()}
              currentUserId={user?.id}
            />
          ))}
        </div>
      )}

      {/* Task Modal */}
      {showModal && (
        <TaskModal
          task={selectedTask}
          users={users}
          onClose={handleModalClose}
          onSuccess={showToast}
        />
      )}

      {/* Complete Task Modal */}
      {showCompleteModal && taskToComplete && (
        <CompleteTaskModal
          task={taskToComplete}
          onClose={() => {
            setShowCompleteModal(false);
            setTaskToComplete(null);
          }}
          onSubmit={submitTaskCompletion}
        />
      )}

      {/* Toast Notification */}
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
};

export default Tasks;
