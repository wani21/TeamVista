import { useState } from 'react';
import { FiX, FiCheckCircle, FiClock, FiFileText } from 'react-icons/fi';

const CompleteTaskModal = ({ task, onClose, onSubmit }) => {
  const [formData, setFormData] = useState({
    completionNotes: '',
    hoursSpent: '',
    challenges: '',
    learnings: '',
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await onSubmit(task.id, formData);
      onClose();
    } catch (error) {
      console.error('Failed to complete task:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200 bg-gradient-to-r from-success-50 to-success-100">
          <div className="flex items-center space-x-3">
            <div className="w-12 h-12 bg-success-500 rounded-full flex items-center justify-center">
              <FiCheckCircle className="text-white" size={24} />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-900">Complete Task</h2>
              <p className="text-sm text-gray-600">Tell us about your work</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-white rounded-lg transition-colors duration-200"
          >
            <FiX size={24} />
          </button>
        </div>

        {/* Task Info */}
        <div className="p-6 bg-gray-50 border-b border-gray-200">
          <h3 className="font-semibold text-gray-900 mb-2">{task.title}</h3>
          {task.description && (
            <p className="text-sm text-gray-600">{task.description}</p>
          )}
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {/* Completion Notes */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              What did you accomplish? <span className="text-danger-500">*</span>
            </label>
            <textarea
              value={formData.completionNotes}
              onChange={(e) => setFormData({ ...formData, completionNotes: e.target.value })}
              className="input-field min-h-[100px]"
              placeholder="Describe what you completed and the results..."
              required
              rows={4}
            />
            <p className="text-xs text-gray-500 mt-1">
              Be specific about deliverables, outcomes, and any notable achievements
            </p>
          </div>

          {/* Hours Spent */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Time Spent (hours) <span className="text-danger-500">*</span>
            </label>
            <div className="relative">
              <FiClock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input
                type="number"
                step="0.5"
                min="0.5"
                value={formData.hoursSpent}
                onChange={(e) => setFormData({ ...formData, hoursSpent: e.target.value })}
                className="input-field pl-10"
                placeholder="e.g., 4.5"
                required
              />
            </div>
            <p className="text-xs text-gray-500 mt-1">
              Estimate the total time you spent on this task
            </p>
          </div>

          {/* Challenges */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Challenges Faced (Optional)
            </label>
            <textarea
              value={formData.challenges}
              onChange={(e) => setFormData({ ...formData, challenges: e.target.value })}
              className="input-field min-h-[80px]"
              placeholder="Any obstacles or difficulties you encountered..."
              rows={3}
            />
          </div>

          {/* Learnings */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Key Learnings (Optional)
            </label>
            <textarea
              value={formData.learnings}
              onChange={(e) => setFormData({ ...formData, learnings: e.target.value })}
              className="input-field min-h-[80px]"
              placeholder="What did you learn from this task..."
              rows={3}
            />
          </div>

          {/* Info Box */}
          <div className="p-4 bg-primary-50 border border-primary-200 rounded-lg">
            <div className="flex items-start space-x-3">
              <FiFileText className="text-primary-600 flex-shrink-0 mt-1" />
              <div className="text-sm text-primary-900">
                <p className="font-medium mb-1">Why we ask for this information:</p>
                <ul className="list-disc list-inside space-y-1 text-primary-700">
                  <li>Helps track project progress and time estimates</li>
                  <li>Improves team knowledge sharing</li>
                  <li>Contributes to your performance evaluation</li>
                  <li>Identifies areas for process improvement</li>
                </ul>
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end space-x-3 pt-4 border-t border-gray-200">
            <button
              type="button"
              onClick={onClose}
              className="btn-secondary"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn-primary flex items-center space-x-2"
              disabled={loading}
            >
              <FiCheckCircle />
              <span>{loading ? 'Completing...' : 'Complete Task'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CompleteTaskModal;
