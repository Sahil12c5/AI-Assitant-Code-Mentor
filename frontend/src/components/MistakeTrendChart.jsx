import React from 'react';
import { 
  TrendingDown, 
  TrendingUp, 
  Minus, 
  AlertTriangle, 
  BookOpen, 
  Clock, 
  ShieldAlert 
} from 'lucide-react';

export default function MistakeTrendChart({ trends, typeCounts }) {
  if (!trends || trends.length === 0) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '2rem', color: '#64748b' }}>
        No mistake telemetry recorded yet. Continue solving to build mistake trajectory history.
      </div>
    );
  }

  const deterioratingTrends = trends.filter((t) => t.direction === 'DETERIORATING');

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Deteriorating Alert Banner */}
      {deterioratingTrends.length > 0 && (
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', borderRadius: '10px', padding: '1.25rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', color: '#f87171', marginBottom: '0.5rem' }}>
            <AlertTriangle size={20} />
            <h4 style={{ fontWeight: 700, fontSize: '0.95rem', color: '#fff' }}>
              Deteriorating Mistake Patterns Detected ({deterioratingTrends.length})
            </h4>
          </div>
          <p style={{ color: '#cbd5e1', fontSize: '0.82rem', marginBottom: '1rem', lineHeight: 1.4 }}>
            The Mistake Memory engine detected rising mistake frequencies over the last 3-week window.
            Here are your recommended pedagogical refreshers:
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
            {deterioratingTrends.map((d, i) => (
              <div
                key={i}
                style={{
                  background: 'rgba(15, 23, 42, 0.6)',
                  padding: '0.75rem 1rem',
                  borderRadius: '6px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  border: '1px solid rgba(239, 68, 68, 0.2)',
                }}
              >
                <div>
                  <span className="badge badge-danger" style={{ marginRight: '0.6rem' }}>
                    {d.mistakeType}
                  </span>
                  <span style={{ fontSize: '0.85rem', fontWeight: 600, color: '#f8fafc' }}>
                    {d.recommendedLesson}
                  </span>
                </div>
                <span className="badge badge-secondary">{d.topic}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 3-Week Trajectory Table & Comparison */}
      <div className="card">
        <div style={{ marginBottom: '1.25rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#fff' }}>
            3-Week Longitudinal Mistake Trajectories
          </h3>
          <p style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
            Evaluates recurring anti-patterns across Week 3 (oldest), Week 2, and Week 1 (recent)
          </p>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.85rem' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)', color: '#94a3b8', fontSize: '0.75rem', textTransform: 'uppercase' }}>
                <th style={{ padding: '0.75rem 0.5rem' }}>Mistake Category</th>
                <th style={{ padding: '0.75rem 0.5rem', textAlign: 'center' }}>Week 3 (Past)</th>
                <th style={{ padding: '0.75rem 0.5rem', textAlign: 'center' }}>Week 2</th>
                <th style={{ padding: '0.75rem 0.5rem', textAlign: 'center' }}>Week 1 (Current)</th>
                <th style={{ padding: '0.75rem 0.5rem', textAlign: 'center' }}>3-Week Trend</th>
                <th style={{ padding: '0.75rem 0.5rem' }}>Pedagogical Recommendation</th>
              </tr>
            </thead>
            <tbody>
              {trends.map((t, idx) => {
                let badgeClass = 'badge-secondary';
                let Icon = Minus;
                let label = 'Stable';

                if (t.direction === 'IMPROVING') {
                  badgeClass = 'badge-success';
                  Icon = TrendingDown; // Fewer mistakes is good!
                  label = 'Improving';
                } else if (t.direction === 'DETERIORATING') {
                  badgeClass = 'badge-danger';
                  Icon = TrendingUp; // More mistakes is deteriorating
                  label = 'Deteriorating';
                }

                return (
                  <tr key={idx} style={{ borderBottom: '1px solid rgba(148, 163, 184, 0.08)' }}>
                    <td style={{ padding: '0.75rem 0.5rem', fontWeight: 600, color: '#f8fafc' }}>
                      {t.mistakeType}
                    </td>

                    {/* Week 3 */}
                    <td style={{ padding: '0.75rem 0.5rem', textAlign: 'center', color: '#94a3b8' }}>
                      {t.oldestWeekCount}
                    </td>

                    {/* Week 2 */}
                    <td style={{ padding: '0.75rem 0.5rem', textAlign: 'center', color: '#cbd5e1' }}>
                      {t.previousWeekCount}
                    </td>

                    {/* Week 1 */}
                    <td style={{ padding: '0.75rem 0.5rem', textAlign: 'center', fontWeight: 700, color: '#fff' }}>
                      {t.currentWeekCount}
                    </td>

                    {/* Trend Badge */}
                    <td style={{ padding: '0.75rem 0.5rem', textAlign: 'center' }}>
                      <span className={`badge ${badgeClass}`} style={{ display: 'inline-flex', gap: '0.3rem' }}>
                        <Icon size={12} />
                        <span>{label}</span>
                      </span>
                    </td>

                    {/* Recommended Lesson */}
                    <td style={{ padding: '0.75rem 0.5rem', color: '#94a3b8', fontSize: '0.8rem' }}>
                      {t.recommendedLesson}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
