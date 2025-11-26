import { createContext, useState, useContext, useEffect } from 'react';
import { authAPI, userAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    
    if (token && savedUser) {
      setUser(JSON.parse(savedUser));
      // Fetch fresh user data
      userAPI.getMe()
        .then(response => {
          const userData = response.data.data;
          setUser(userData);
          localStorage.setItem('user', JSON.stringify(userData));
        })
        .catch(() => {
          logout();
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (email, password) => {
    const response = await authAPI.login({ email, password });
    const token = response.data.data.token;
    localStorage.setItem('token', token);
    
    // Fetch user data
    const userResponse = await userAPI.getMe();
    const userData = userResponse.data.data;
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
    
    return userData;
  };

  const register = async (data) => {
    const response = await authAPI.register(data);
    return response.data.data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const isManager = () => user?.role === 'MANAGER';
  const isEmployee = () => user?.role === 'EMPLOYEE';

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading, isManager, isEmployee }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
