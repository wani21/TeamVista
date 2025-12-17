import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

// Task APIs
export const taskAPI = {
  getAll: (params) => api.get('/tasks', { params }),
  getById: (id) => api.get(`/tasks/${id}`),
  create: (data) => api.post('/tasks', data),
  update: (id, data) => api.put(`/tasks/${id}`, data),
  complete: (id) => api.put(`/tasks/${id}/complete`),
  delete: (id) => api.delete(`/tasks/${id}`),
  search: (keyword) => api.get('/tasks/search', { params: { keyword } }),
};

// User APIs
export const userAPI = {
  getAll: () => api.get('/users'),
  getMe: () => api.get('/users/me'),
  getById: (id) => api.get(`/users/${id}`),
  getStats: (id) => api.get(`/users/${id}/stats`),
};

// Dashboard APIs
export const dashboardAPI = {
  getSummary: () => api.get('/dashboard/summary'),
  getEnhanced: () => api.get('/dashboard/enhanced'),
  getPersonal: () => api.get('/dashboard/personal'),
};

export const projectAPI = {
  getAll: () => api.get('/projects'),
  getById: (id) => api.get(`/projects/${id}`),
  create: (data) => api.post('/projects', data),
  update: (id, data) => api.put(`/projects/${id}`, data),
  delete: (id) => api.delete(`/projects/${id}`),
  
  // Update financials
  updateFinancials: (id, data) => api.patch(`/projects/${id}/financials`, data),
  
  // Project members
  getMembers: (projectId) => api.get(`/projects/${projectId}/members`),
  addMember: (projectId, userId, role = 'MEMBER') => 
    api.post(`/projects/${projectId}/members`, { userId, role }),
  removeMember: (projectId, userId) => 
    api.delete(`/projects/${projectId}/members/${userId}`),
  checkMembership: (projectId, userId) => 
    api.get(`/projects/${projectId}/members/${userId}/check`),
  
  // Get project group
  getGroup: (projectId) => api.get(`/projects/${projectId}/group`),
};

export const chatAPI = {
  // group chat
  getGroupMessages: (groupId) =>
    api.get(`/groups/${groupId}/messages`),

  sendGroupMessage: (groupId, content) =>
    api.post(`/groups/${groupId}/messages`, content, {
      headers: { 'Content-Type': 'text/plain' }
    }),

  // Get group details
  getGroup: (groupId) => api.get(`/groups/${groupId}`),

  // direct chat
  sendDirectMessage: (receiverId, content) =>
    api.post(`/messages?receiverId=${receiverId}`, content, {
      headers: { 'Content-Type': 'text/plain' }
    }),
};

export default api;
