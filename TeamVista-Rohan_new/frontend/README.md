# TeamVista Frontend

Modern, beautiful React frontend for the Team Productivity Dashboard.

## 🎨 Design Features

- **Beautiful UI**: Inspired by Linear, Notion, and Asana
- **Responsive Design**: Works perfectly on desktop, tablet, and mobile
- **Modern Color Scheme**: 
  - Primary: Blue tones for main actions
  - Accent: Purple tones for highlights
  - Success: Green for completed tasks
  - Warning: Orange for in-progress items
  - Danger: Red for overdue/critical items

## 🚀 Quick Start

### Prerequisites
- Node.js 16+ and npm/yarn
- Backend API running on http://localhost:8080

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

The app will be available at http://localhost:3000

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/        # Reusable components
│   │   ├── Layout.jsx    # Main layout with sidebar
│   │   ├── TaskCard.jsx  # Task display card
│   │   └── TaskModal.jsx # Create/Edit task modal
│   ├── context/          # React context
│   │   └── AuthContext.jsx
│   ├── pages/            # Page components
│   │   ├── Login.jsx     # Login page
│   │   ├── Register.jsx  # Registration page
│   │   ├── Dashboard.jsx # Dashboard with charts
│   │   └── Tasks.jsx     # Tasks management
│   ├── services/         # API services
│   │   └── api.js        # Axios configuration
│   ├── App.jsx           # Main app component
│   ├── main.jsx          # Entry point
│   └── index.css         # Global styles
├── index.html
├── package.json
├── tailwind.config.js
└── vite.config.js
```

## 🎯 Features

### Authentication
- ✅ Beautiful login page with image
- ✅ Registration with role selection
- ✅ JWT token management
- ✅ Auto-redirect on auth state change

### Dashboard
- ✅ Real-time statistics
- ✅ Beautiful charts (Pie & Bar)
- ✅ Team performance metrics
- ✅ Productivity insights
- ✅ Overdue task alerts

### Task Management
- ✅ Create, edit, delete tasks (Manager)
- ✅ Update task status (All users)
- ✅ Complete tasks
- ✅ Search and filter tasks
- ✅ Priority and status badges
- ✅ Due date tracking
- ✅ Overdue indicators

### UI/UX
- ✅ Responsive sidebar navigation
- ✅ Mobile-friendly design
- ✅ Smooth animations
- ✅ Loading states
- ✅ Error handling
- ✅ Toast notifications
- ✅ Modal dialogs

## 🎨 Color Palette

```css
Primary (Blue):
- 50:  #f0f9ff
- 500: #0ea5e9
- 700: #0369a1

Accent (Purple):
- 50:  #fdf4ff
- 500: #d946ef
- 700: #a21caf

Success (Green):
- 50:  #f0fdf4
- 500: #22c55e
- 700: #15803d

Warning (Orange):
- 50:  #fffbeb
- 500: #f59e0b
- 700: #b45309

Danger (Red):
- 50:  #fef2f2
- 500: #ef4444
- 700: #b91c1c
```

## 🔧 Configuration

### API Base URL
Update in `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

### Proxy Configuration
Vite proxy is configured in `vite.config.js` to forward `/api` requests to the backend.

## 📱 Responsive Breakpoints

- Mobile: < 768px
- Tablet: 768px - 1024px
- Desktop: > 1024px

## 🧪 Demo Credentials

**Manager:**
- Email: manager@example.com
- Password: password123

**Employee:**
- Email: alice@example.com
- Password: password123

## 🚀 Deployment

### Build for Production
```bash
npm run build
```

The build output will be in the `dist/` directory.

### Deploy to Netlify/Vercel
1. Connect your repository
2. Set build command: `npm run build`
3. Set publish directory: `dist`
4. Add environment variable: `VITE_API_URL=your-backend-url`

## 📦 Dependencies

### Core
- React 18.2.0
- React Router DOM 6.20.0
- Axios 1.6.2

### UI
- Tailwind CSS 3.4.0
- React Icons 4.12.0
- Recharts 2.10.3 (for charts)
- date-fns 3.0.6 (for date formatting)

### Build Tools
- Vite 5.0.8
- PostCSS 8.4.32
- Autoprefixer 10.4.16

## 🎓 Key Features Explained

### Authentication Flow
1. User logs in → JWT token stored in localStorage
2. Token added to all API requests via Axios interceptor
3. On 401 error → Auto logout and redirect to login
4. User data cached in localStorage for persistence

### Task Management
- **Managers**: Full CRUD access to all tasks
- **Employees**: Can only update status of assigned tasks
- Real-time filtering and search
- Drag-and-drop support (coming soon)

### Dashboard Analytics
- Real-time statistics from backend
- Beautiful charts using Recharts
- Team performance tracking
- Productivity insights

## 🐛 Troubleshooting

### CORS Issues
Make sure backend CORS is configured for `http://localhost:3000`

### API Connection Failed
1. Check backend is running on port 8080
2. Verify API_BASE_URL in `src/services/api.js`
3. Check browser console for errors

### Build Errors
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

## 📝 License

This project is part of the TeamVista productivity suite.
