import React, { useState, useEffect } from 'react';
import ProfileDashboard from '../components/ProfileDashboard';
import MistakeTrendChart from '../components/MistakeTrendChart';
import { api } from '../api/client';
import { RefreshCw } from 'lucide-react';

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [mistakeData, setMistakeData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [pRes, mRes] = await Promise.all([
        api.getProfile(),
        api.getMistakeTrends(),
      ]);
      setProfile(pRes);
      setMistakeData(mRes);
    } catch (err) {
      setError(err.message || 'Failed to load profile data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="page-view" style={{ textAlign: 'center', padding: '4rem 0' }}>
        <RefreshCw size={32} className="animate-spin" style={{ color: '#818cf8', animation: 'spin 1s linear infinite', margin: '0 auto 1rem' }} />
        <p style={{ color: '#94a3b8' }}>Loading Student Coding DNA & Mistake Memory...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-view">
        <div className="card" style={{ textAlign: 'center', borderColor: 'rgba(239, 68, 68, 0.4)' }}>
          <p style={{ color: '#f87171', marginBottom: '1rem' }}>{error}</p>
          <button className="btn btn-secondary btn-sm" onClick={loadData}>
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-view" style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
      <ProfileDashboard profile={profile} />
      <MistakeTrendChart
        trends={mistakeData?.trends || []}
        typeCounts={mistakeData?.typeCounts || {}}
      />
    </div>
  );
}
