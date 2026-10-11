import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { History as HistoryIcon, Code2, CheckCircle2, XCircle, Clock, Eye, X } from 'lucide-react';

export default function History({ onOpenInWorkspace }) {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedSubmission, setSelectedSubmission] = useState(null);

  useEffect(() => {
    loadSubmissions();
  }, []);

  const loadSubmissions = async () => {
    setLoading(true);
    try {
      const res = await api.getSubmissions(50);
      setSubmissions(res.submissions || []);
    } catch (err) {
      console.error('Failed to load submissions', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    if (status === 'CORRECT') {
      return (
        <span className="badge badge-success" style={{ display: 'inline-flex', gap: '0.25rem' }}>
          <CheckCircle2 size={11} /> Correct
        </span>
      );
    }
    return (
      <span className="badge badge-danger" style={{ display: 'inline-flex', gap: '0.25rem' }}>
        <XCircle size={11} /> Incorrect
      </span>
    );
  };

  return (
    <div className="page-view">
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
          <HistoryIcon size={22} style={{ color: '#818cf8' }} />
          <div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#fff' }}>
              Submission History & Progress Trail
            </h2>
            <p style={{ fontSize: '0.82rem', color: '#94a3b8' }}>
              Chronological log of your code submissions, sandbox executions, and AI diagnoses
            </p>
          </div>
        </div>
      </div>

      <div className="card">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
            Loading submission logs...
          </div>
        ) : submissions.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
            No submissions recorded yet. Try solving problems in the IDE workspace!
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.85rem' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)', color: '#94a3b8', fontSize: '0.75rem', textTransform: 'uppercase' }}>
                  <th style={{ padding: '0.75rem 0.5rem' }}>Submission ID</th>
                  <th style={{ padding: '0.75rem 0.5rem' }}>Problem</th>
                  <th style={{ padding: '0.75rem 0.5rem' }}>Language</th>
                  <th style={{ padding: '0.75rem 0.5rem' }}>Result Status</th>
                  <th style={{ padding: '0.75rem 0.5rem' }}>Submitted At</th>
                  <th style={{ padding: '0.75rem 0.5rem', textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {submissions.map((sub) => (
                  <tr key={sub.id} style={{ borderBottom: '1px solid rgba(148, 163, 184, 0.08)' }}>
                    <td style={{ padding: '0.75rem 0.5rem', fontFamily: 'var(--font-mono)', color: '#818cf8' }}>
                      #{sub.id}
                    </td>
                    <td style={{ padding: '0.75rem 0.5rem', fontWeight: 600, color: '#fff' }}>
                      {sub.problemTitle || 'Ad-Hoc Playground'}
                    </td>
                    <td style={{ padding: '0.75rem 0.5rem' }}>
                      <span className="badge badge-secondary">{sub.language}</span>
                    </td>
                    <td style={{ padding: '0.75rem 0.5rem' }}>
                      {getStatusBadge(sub.status)}
                    </td>
                    <td style={{ padding: '0.75rem 0.5rem', color: '#94a3b8', fontSize: '0.8rem' }}>
                      {sub.createdAt}
                    </td>
                    <td style={{ padding: '0.75rem 0.5rem', textAlign: 'right' }}>
                      <button
                        className="btn btn-secondary btn-sm"
                        style={{ padding: '0.2rem 0.5rem' }}
                        onClick={() => setSelectedSubmission(sub)}
                      >
                        <Eye size={12} />
                        <span>View Code</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Code Viewer Modal */}
      {selectedSubmission && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '650px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <div>
                <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: '#fff' }}>
                  Submission #{selectedSubmission.id}: {selectedSubmission.problemTitle}
                </h3>
                <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                  {selectedSubmission.createdAt} &bull; {selectedSubmission.language.toUpperCase()}
                </span>
              </div>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setSelectedSubmission(null)}
              >
                <X size={14} />
              </button>
            </div>

            <div style={{ background: '#0a0e1a', borderRadius: '6px', padding: '1rem', border: '1px solid var(--border-color)', marginBottom: '1rem' }}>
              <pre style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8rem', color: '#f8fafc', whiteSpace: 'pre-wrap', maxHeight: '300px', overflowY: 'auto' }}>
                {selectedSubmission.code}
              </pre>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setSelectedSubmission(null)}
              >
                Close
              </button>
              {onOpenInWorkspace && (
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => {
                    const sub = selectedSubmission;
                    setSelectedSubmission(null);
                    onOpenInWorkspace(sub);
                  }}
                >
                  <Code2 size={13} />
                  <span>Load into Workspace</span>
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
