import { useEffect, useState, useRef } from 'react';
import { chatAPI, projectAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

const ProjectChat = ({ projectId }) => {
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState('');
  const [groupId, setGroupId] = useState(null);
  const [groupName, setGroupName] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef(null);
  const { user } = useAuth();

  // Scroll to bottom when messages change
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Fetch the group for this project
  useEffect(() => {
    const fetchGroup = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await projectAPI.getGroup(projectId);
        setGroupId(res.data.id);
        setGroupName(res.data.name);
      } catch (err) {
        console.error('Failed to fetch group:', err);
        setError('Failed to load chat. You may not be a member of this project.');
      } finally {
        setLoading(false);
      }
    };
    
    if (projectId) {
      fetchGroup();
    }
  }, [projectId]);

  // Fetch messages when groupId is available
  useEffect(() => {
    const fetchMessages = async () => {
      if (!groupId) return;
      try {
        const res = await chatAPI.getGroupMessages(groupId);
        setMessages(res.data);
      } catch (err) {
        console.error('Failed to fetch messages:', err);
        if (err.response?.status === 403) {
          setError('You are not a member of this project and cannot view messages.');
        }
      }
    };

    fetchMessages();
    
    // Poll for new messages every 5 seconds
    const interval = setInterval(fetchMessages, 5000);
    return () => clearInterval(interval);
  }, [groupId]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!text.trim() || !groupId || sending) return;

    try {
      setSending(true);
      await chatAPI.sendGroupMessage(groupId, text.trim());
      setText('');
      // Fetch updated messages
      const res = await chatAPI.getGroupMessages(groupId);
      setMessages(res.data);
    } catch (err) {
      console.error('Failed to send message:', err);
      if (err.response?.status === 403) {
        setError('You are not authorized to send messages in this group.');
      }
    } finally {
      setSending(false);
    }
  };

  const formatTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    }
    return date.toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' });
  };

  const isOwnMessage = (message) => {
    return message.sender?.email === user?.email;
  };

  // Group messages by date
  const groupedMessages = messages.reduce((groups, message) => {
    const date = formatDate(message.createdAt);
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(message);
    return groups;
  }, {});

  if (loading) {
    return (
      <div className="flex items-center justify-center h-[500px] bg-gray-50 rounded-xl">
        <div className="flex flex-col items-center gap-3">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
          <span className="text-gray-500">Loading chat...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-[500px] bg-red-50 rounded-xl border border-red-200">
        <div className="text-center p-6">
          <svg className="mx-auto h-12 w-12 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p className="mt-3 text-red-600 font-medium">{error}</p>
          <p className="mt-1 text-sm text-red-500">Please contact a project manager to get access.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 h-[600px] flex flex-col overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 px-6 py-4 flex items-center gap-3">
        <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
          <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z" />
          </svg>
        </div>
        <div>
          <h3 className="text-white font-semibold">{groupName || 'Project Chat'}</h3>
          <p className="text-indigo-200 text-sm">{messages.length} messages</p>
        </div>
      </div>

      {/* Messages area */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50">
        {messages.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full text-gray-400">
            <svg className="w-16 h-16 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
            <p className="text-lg font-medium">No messages yet</p>
            <p className="text-sm">Be the first to send a message!</p>
          </div>
        ) : (
          Object.entries(groupedMessages).map(([date, dateMessages]) => (
            <div key={date}>
              {/* Date separator */}
              <div className="flex items-center gap-4 my-4">
                <div className="flex-1 h-px bg-gray-300"></div>
                <span className="text-xs text-gray-500 font-medium bg-gray-50 px-3">{date}</span>
                <div className="flex-1 h-px bg-gray-300"></div>
              </div>

              {/* Messages for this date */}
              {dateMessages.map((m, index) => {
                const own = isOwnMessage(m);
                const showAvatar = index === 0 || dateMessages[index - 1]?.sender?.id !== m.sender?.id;
                
                return (
                  <div
                    key={m.id}
                    className={`flex ${own ? 'justify-end' : 'justify-start'} mb-2`}
                  >
                    <div className={`flex gap-2 max-w-[70%] ${own ? 'flex-row-reverse' : 'flex-row'}`}>
                      {/* Avatar */}
                      {!own && (
                        <div className={`w-8 h-8 rounded-full flex-shrink-0 ${showAvatar ? 'bg-gradient-to-br from-indigo-500 to-purple-500' : 'invisible'} flex items-center justify-center`}>
                          <span className="text-white text-xs font-medium">
                            {m.sender?.name?.charAt(0).toUpperCase()}
                          </span>
                        </div>
                      )}

                      {/* Message bubble */}
                      <div>
                        {!own && showAvatar && (
                          <p className="text-xs text-gray-500 mb-1 ml-1">{m.sender?.name}</p>
                        )}
                        <div
                          className={`px-4 py-2 rounded-2xl ${
                            own
                              ? 'bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-br-md'
                              : 'bg-white text-gray-800 shadow-sm border border-gray-100 rounded-bl-md'
                          }`}
                        >
                          <p className="text-sm whitespace-pre-wrap break-words">{m.content}</p>
                        </div>
                        <p className={`text-xs text-gray-400 mt-1 ${own ? 'text-right mr-1' : 'ml-1'}`}>
                          {formatTime(m.createdAt)}
                        </p>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input area */}
      <form onSubmit={handleSend} className="p-4 bg-white border-t border-gray-200">
        <div className="flex gap-3 items-center">
          <input
            type="text"
            value={text}
            onChange={(e) => setText(e.target.value)}
            className="flex-1 px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all outline-none"
            placeholder="Type your message..."
            disabled={sending}
          />
          <button
            type="submit"
            disabled={!text.trim() || sending}
            className="px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl font-medium hover:from-indigo-700 hover:to-purple-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            {sending ? (
              <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
            ) : (
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
              </svg>
            )}
            <span>Send</span>
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProjectChat;
