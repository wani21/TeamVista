import { useEffect } from 'react';
import { FiCheckCircle, FiAlertCircle, FiInfo, FiX } from 'react-icons/fi';

const Toast = ({ message, type = 'success', onClose, duration = 3000 }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const config = {
    success: {
      icon: FiCheckCircle,
      bgColor: 'bg-success-50',
      borderColor: 'border-success-500',
      textColor: 'text-success-800',
      iconColor: 'text-success-500',
    },
    error: {
      icon: FiAlertCircle,
      bgColor: 'bg-danger-50',
      borderColor: 'border-danger-500',
      textColor: 'text-danger-800',
      iconColor: 'text-danger-500',
    },
    info: {
      icon: FiInfo,
      bgColor: 'bg-primary-50',
      borderColor: 'border-primary-500',
      textColor: 'text-primary-800',
      iconColor: 'text-primary-500',
    },
  };

  const { icon: Icon, bgColor, borderColor, textColor, iconColor } = config[type];

  return (
    <div className={`fixed top-4 right-4 z-50 ${bgColor} ${textColor} px-6 py-4 rounded-lg shadow-lg border-l-4 ${borderColor} flex items-center space-x-3 min-w-[300px] max-w-md animate-slide-in`}>
      <Icon className={`${iconColor} flex-shrink-0`} size={24} />
      <p className="flex-1 font-medium">{message}</p>
      <button
        onClick={onClose}
        className={`${textColor} hover:opacity-70 transition-opacity`}
      >
        <FiX size={20} />
      </button>
    </div>
  );
};

export default Toast;
