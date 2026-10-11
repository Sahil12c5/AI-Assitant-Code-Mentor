import React from 'react';
import { 
  Brain, 
  Code2, 
  BookOpen, 
  Dna, 
  History, 
  LogOut, 
  ArrowLeft 
} from 'lucide-react';

export default function Navbar({ activeTab, onSelectTab, user }) {
  const getPortalUrl = () => {
    const path = window.location.pathname;
    const appIndex = path.indexOf('/app');
    if (appIndex > 0) {
      return path.substring(0, appIndex) + '/dashboard';
    }
    return '/dashboard';
  };

  const getLogoutUrl = () => {
    const path = window.location.pathname;
    const appIndex = path.indexOf('/app');
    if (appIndex > 0) {
      return path.substring(0, appIndex) + '/logout';
    }
    return '/logout';
  };

  return (
    <nav className="app-navbar">
      {/* Brand */}
      <a href="#/workspace" className="nav-brand" onClick={() => onSelectTab('workspace')}>
        <Brain className="text-accent-primary" size={24} style={{ color: '#818cf8' }} />
        <span>Adaptive <span className="highlight">AI Mentor</span></span>
      </a>

      {/* Nav Tabs */}
      <div className="nav-tabs">
        <button
          className={`nav-tab-btn ${activeTab === 'workspace' ? 'active' : ''}`}
          onClick={() => onSelectTab('workspace')}
        >
          <Code2 size={16} />
          <span>IDE Workspace</span>
        </button>

        <button
          className={`nav-tab-btn ${activeTab === 'practice' ? 'active' : ''}`}
          onClick={() => onSelectTab('practice')}
        >
          <BookOpen size={16} />
          <span>Practice Catalog</span>
        </button>

        <button
          className={`nav-tab-btn ${activeTab === 'profile' ? 'active' : ''}`}
          onClick={() => onSelectTab('profile')}
        >
          <Dna size={16} />
          <span>Coding DNA & Trends</span>
        </button>

        <button
          className={`nav-tab-btn ${activeTab === 'history' ? 'active' : ''}`}
          onClick={() => onSelectTab('history')}
        >
          <History size={16} />
          <span>Submission History</span>
        </button>
      </div>

      {/* Right User & Navigation Controls */}
      <div className="nav-actions">
        {user && (
          <div className="user-pill">
            <span style={{ fontWeight: 600 }}>{user.name}</span>
            <span className="badge badge-accent">{user.level || 'STUDENT'}</span>
          </div>
        )}

        <a href={getPortalUrl()} className="btn btn-secondary btn-sm" title="Return to Portal Dashboard">
          <ArrowLeft size={14} />
          <span>Portal</span>
        </a>

        <a href={getLogoutUrl()} className="btn btn-secondary btn-sm" title="Log Out">
          <LogOut size={14} />
        </a>
      </div>
    </nav>
  );
}
