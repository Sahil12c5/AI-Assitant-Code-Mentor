import React from 'react';
import { Dna, Award, CheckCircle, TrendingUp, Sparkles, BookOpen } from 'lucide-react';

export default function ProfileDashboard({ profile }) {
  if (!profile) return null;

  const { name, email, overallLevel, averageMastery, totalSubmissions, solvedProblems, topicMastery } = profile;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Overview Metric Banner */}
      <div className="card" style={{ background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.12), rgba(139, 92, 246, 0.05))' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <div className="badge badge-accent mb-2" style={{ display: 'inline-flex', gap: '0.3rem' }}>
              <Dna size={12} /> Student Coding DNA Profile
            </div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: '#fff' }}>
              {name}
            </h2>
            <p style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
              {email} &bull; Calibrated Level: <strong style={{ color: '#818cf8' }}>{overallLevel}</strong>
            </p>
          </div>

          <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#38bdf8' }}>
                {averageMastery}%
              </div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>
                Avg Mastery
              </div>
            </div>

            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#34d399' }}>
                {solvedProblems}
              </div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>
                Solved
              </div>
            </div>

            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#a78bfa' }}>
                {totalSubmissions}
              </div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>
                Submissions
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Topic Mastery Radar / Grid */}
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
          <div>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#fff' }}>
              Topic Mastery & Pedagogical Scaffolding
            </h3>
            <p style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
              Real-time calibration used by the AI prompt to adjust hint depth
            </p>
          </div>
        </div>

        {(!topicMastery || topicMastery.length === 0) ? (
          <div style={{ textAlign: 'center', padding: '2rem', color: '#64748b' }}>
            No topic telemetry recorded yet. Complete coding exercises in the workspace to construct your DNA.
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1rem' }}>
            {topicMastery.map((item, idx) => {
              const score = Math.round(item.score);
              const assistance = item.score < 30 ? 'HIGH' : item.score < 60 ? 'MEDIUM' : 'LOW';
              const assistanceColor = item.score < 30 ? '#ef4444' : item.score < 60 ? '#f59e0b' : '#10b981';

              return (
                <div
                  key={idx}
                  style={{
                    background: 'rgba(15, 23, 42, 0.6)',
                    border: '1px solid var(--border-color)',
                    borderRadius: '8px',
                    padding: '1rem',
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                    <span style={{ fontWeight: 600, fontSize: '0.9rem', color: '#f8fafc' }}>
                      {item.topic}
                    </span>
                    <span style={{ fontWeight: 700, fontSize: '0.95rem', color: '#818cf8' }}>
                      {score}%
                    </span>
                  </div>

                  {/* Progress Bar */}
                  <div style={{ width: '100%', height: '8px', background: '#334155', borderRadius: '4px', overflow: 'hidden', marginBottom: '0.6rem' }}>
                    <div
                      style={{
                        height: '100%',
                        width: `${score}%`,
                        background: 'linear-gradient(90deg, #6366f1, #06b6d4)',
                        borderRadius: '4px',
                        transition: 'width 0.5s ease',
                      }}
                    />
                  </div>

                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.75rem', color: '#94a3b8' }}>
                    <span>{item.successes || 0} solved / {item.attempts || 0} attempts</span>
                    <span>
                      Guidance: <strong style={{ color: assistanceColor }}>{assistance}</strong>
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
