import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Workspace from './pages/Workspace';
import Practice from './pages/Practice';
import Profile from './pages/Profile';
import History from './pages/History';
import { api } from './api/client';
import { RefreshCw } from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState('workspace');
  const [user, setUser] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);
  const [currentProblem, setCurrentProblem] = useState(null);

  // Sync with URL hash
  useEffect(() => {
    const handleHashChange = () => {
      const hash = window.location.hash.replace('#/', '');
      if (['workspace', 'practice', 'profile', 'history'].includes(hash)) {
        setActiveTab(hash);
      }
    };

    window.addEventListener('hashchange', handleHashChange);
    handleHashChange(); // initial check

    // Check user authentication
    api.getMe()
      .then((res) => {
        if (res.authenticated) {
          setUser(res.user);
        }
      })
      .catch((err) => {
        console.warn('Authentication check notice:', err);
      })
      .finally(() => {
        setLoadingUser(false);
      });

    // Pre-load default starter problem
    api.getNextProblem()
      .then((prob) => {
        if (prob && prob.id) {
          setCurrentProblem(prob);
        }
      })
      .catch((err) => {
        console.warn('Initial problem load note:', err);
      });

    return () => window.removeEventListener('hashchange', handleHashChange);
  }, []);

  const handleSelectTab = (tab) => {
    setActiveTab(tab);
    window.location.hash = `#/${tab}`;
  };

  const handleSelectProblem = (problem) => {
    setCurrentProblem(problem);
    handleSelectTab('workspace');
  };

  const handleLoadSubmissionIntoWorkspace = (sub) => {
    setCurrentProblem({
      id: sub.problemId,
      title: sub.problemTitle || 'Restored Submission',
      language: sub.language,
      starterCode: sub.code,
      difficulty: 'PRACTICE',
      topic: 'Review',
    });
    handleSelectTab('workspace');
  };

  const handleSelectNextProblem = async () => {
    try {
      const nextP = await api.getNextProblem();
      if (nextP && nextP.id) {
        setCurrentProblem(nextP);
      }
    } catch (err) {
      console.warn('Could not load next problem:', err);
    }
  };

  if (loadingUser) {
    return (
      <div style={{ height: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', background: '#0a0e1a', color: '#818cf8' }}>
        <RefreshCw size={36} className="animate-spin mb-3" style={{ animation: 'spin 1s linear infinite' }} />
        <p style={{ color: '#94a3b8', fontSize: '0.9rem' }}>Initializing Adaptive AI Coding Mentor...</p>
      </div>
    );
  }

  return (
    <div className="app-container">
      <Navbar activeTab={activeTab} onSelectTab={handleSelectTab} user={user} />

      {activeTab === 'workspace' && (
        <Workspace
          currentProblem={currentProblem}
          onSelectNextProblem={handleSelectNextProblem}
        />
      )}

      {activeTab === 'practice' && (
        <Practice onSelectProblem={handleSelectProblem} />
      )}

      {activeTab === 'profile' && (
        <Profile />
      )}

      {activeTab === 'history' && (
        <History onOpenInWorkspace={handleLoadSubmissionIntoWorkspace} />
      )}
    </div>
  );
}
