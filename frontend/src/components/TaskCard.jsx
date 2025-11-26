import { FiEdit2, FiTrash2, FiCheckCircle, FiCalendar, FiUser, FiClock } from 'react-icons/fi';
import { format, isPast, isToday } from 'date-fns';

const TaskCard = ({ task, onEdit, onDelete, onComplete, isManager, currentUserId }) => {
  const canEdit = isManager || task.assignedTo?.id === currentUserId;
  const canDelete = isManager;
  const canComplete = canEdit && task.status !== 'COMPLETED';

  const priorityColors = {
    HIGH: 'badge-high',
    MEDIUM: 'badge-medium',
    LOW: 'badge-low',
  };

  const statusColors = {
    PENDING: 'badge-pending',
    IN_PROGRESS: 'badge-in-progress',
    COMPLETED: 'badge-completed',
  };

  const isOverdue = task.dueDate && isPast(new Date(task.dueDate)) && task.status !== 'COMPLETED';
  const isDueToday = task.dueDate && isToday(new Date(task.dueDate));

  return (
    <div className={`card hover:shadow-lg transition-all duration-200 ${
      isOverdue ? 'border-l-4 border-danger-500' : ''
    }`}>
      {/* Header */}
      <div className="flex items-start justify-between mb-3">
        <div className="flex-1">
          <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2">{task.title}</h3>
          <div className="flex flex-wrap gap-2">
            <span className={`badge ${statusColors[task.status]}`}>
              {task.status.replace('_', ' ')}
            </span>
            <span className={`badge ${priorityColors[task.priority]}`}>
              {task.priority}
            </span>
          </div>
        </div>
      </div>

      {/* Description */}
      {task.description && (
        <p className="text-sm text-gray-600 mb-4 line-clamp-3">{task.description}</p>
      )}

      {/* Meta Info */}
      <div className="space-y-2 mb-4 text-sm">
        {/* Assignee */}
        <div className="flex items-center space-x-2 text-gray-600">
          <FiUser size={16} />
          <span>{task.assignedTo?.name || 'Unassigned'}</span>
        </div>

        {/* Due Date */}
        {task.dueDate && (
          <div className={`flex items-center space-x-2 ${
            isOverdue ? 'text-danger-600 font-medium' : isDueToday ? 'text-warning-600 font-medium' : 'text-gray-600'
          }`}>
            <FiCalendar size={16} />
            <span>
              {isOverdue && '⚠️ '}
              {isDueToday && '📅 '}
              Due: {format(new Date(task.dueDate), 'MMM dd, yyyy')}
            </span>
          </div>
        )}

        {/* Completed Date */}
        {task.completedDate && (
          <div className="flex items-center space-x-2 text-success-600">
            <FiCheckCircle size={16} />
            <span>Completed: {format(new Date(task.completedDate), 'MMM dd, yyyy')}</span>
          </div>
        )}

        {/* Created Date */}
        <div className="flex items-center space-x-2 text-gray-500 text-xs">
          <FiClock size={14} />
          <span>Created: {format(new Date(task.createdAt), 'MMM dd, yyyy')}</span>
        </div>
      </div>

      {/* Actions */}
      <div className="flex items-center space-x-2 pt-4 border-t border-gray-200">
        {canComplete && (
          <button
            onClick={() => onComplete(task.id)}
            className="flex-1 flex items-center justify-center space-x-1 px-3 py-2 bg-success-50 text-success-700 hover:bg-success-100 rounded-lg transition-colors duration-200 text-sm font-medium"
          >
            <FiCheckCircle size={16} />
            <span>Complete</span>
          </button>
        )}
        {canEdit && (
          <button
            onClick={() => onEdit(task)}
            className="flex-1 flex items-center justify-center space-x-1 px-3 py-2 bg-primary-50 text-primary-700 hover:bg-primary-100 rounded-lg transition-colors duration-200 text-sm font-medium"
          >
            <FiEdit2 size={16} />
            <span>Edit</span>
          </button>
        )}
        {canDelete && (
          <button
            onClick={() => onDelete(task.id)}
            className="px-3 py-2 bg-danger-50 text-danger-700 hover:bg-danger-100 rounded-lg transition-colors duration-200"
          >
            <FiTrash2 size={16} />
          </button>
        )}
      </div>
    </div>
  );
};

export default TaskCard;
