import React, { useState } from 'react';
import { Terminal, Clock, CheckCircle2, XCircle, AlertTriangle, ShieldAlert } from 'lucide-react';

export default function RunOutput({ result, isRunning, stdin, onStdinChange }) {
  const [showStdin, setShowStdin] = useState(false);

  const getStatusBadge = (status) => {
    switch (status) {
      case 'SUCCESS':
        return (
          <span className="badge badge-success" style={{ gap: '0.3rem' }}>
            <CheckCircle2 size={12} /> Success
          </span>
        );
      case 'COMPILATION_ERROR':
        return (
          <span className="badge badge-danger" style={{ gap: '0.3rem' }}>
            <XCircle size={12} /> Compilation Error
          </span>
        );
      case 'RUNTIME_ERROR':
        return (
          <span className="badge badge-danger" style={{ gap: '0.3rem' }}>
            <XCircle size={12} /> Runtime Error
          </span>
        );
      case 'TIMEOUT':
        return (
          <span className="badge badge-warning" style={{ gap: '0.3rem' }}>
            <AlertTriangle size={12} /> Timeout (&gt;5s)
          </span>
        );
      case 'SECURITY_VIOLATION':
        return (
          <span className="badge badge-danger" style={{ gap: '0.3rem' }}>
            <ShieldAlert size={12} /> Sandbox Security Alert
          </span>
        );
      default:
        return null;
    }
  };

  return (
    <div className="console-pane">
      <div className="console-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Terminal size={14} />
          <span>Execution Output</span>
          {result && getStatusBadge(result.status)}
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          {result && result.executionTimeMs !== undefined && (
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.75rem' }}>
              <Clock size={12} />
              {result.executionTimeMs} ms
            </span>
          )}

          <button
            className="btn btn-secondary btn-sm"
            style={{ padding: '0.15rem 0.45rem', fontSize: '0.7rem' }}
            onClick={() => setShowStdin(!showStdin)}
          >
            {showStdin ? 'Hide Stdin' : 'Custom Input'}
          </button>
        </div>
      </div>

      {showStdin && (
        <div style={{ padding: '0.4rem 0.85rem', background: '#0a0e17', borderBottom: '1px solid var(--border-color)' }}>
          <input
            type="text"
            style={{
              width: '100%',
              background: '#111827',
              border: '1px solid var(--border-color)',
              color: '#f8fafc',
              padding: '0.25rem 0.5rem',
              borderRadius: '4px',
              fontSize: '0.8rem',
              fontFamily: 'var(--font-mono)'
            }}
            placeholder="Standard Input (stdin) to feed into program..."
            value={stdin}
            onChange={(e) => onStdinChange(e.target.value)}
          />
        </div>
      )}

      <div className="console-body">
        {isRunning ? (
          <div style={{ color: '#818cf8', fontStyle: 'italic' }}>
            Compiling and executing in isolated process sandbox...
          </div>
        ) : !result ? (
          <div style={{ color: '#64748b' }}>
            Click "Run Code" to compile and execute in the sandbox, or "Analyze with AI Mentor" for progressive guidance.
          </div>
        ) : (
          <>
            {result.stdout && <div>{result.stdout}</div>}
            {result.stderr && (
              <div style={{ color: '#f87171', marginTop: result.stdout ? '0.5rem' : '0' }}>
                {result.stderr}
              </div>
            )}
            {!result.stdout && !result.stderr && (
              <div style={{ color: '#64748b', fontStyle: 'italic' }}>
                Program finished with exit code {result.exitCode} (no console output).
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
