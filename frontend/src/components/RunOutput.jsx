import React, { useState, useEffect } from 'react';
import { 
  Terminal, 
  Clock, 
  CheckCircle2, 
  XCircle, 
  AlertTriangle, 
  ShieldAlert, 
  Keyboard, 
  Trash2, 
  Sparkles,
  HelpCircle,
  ArrowRight
} from 'lucide-react';

export default function RunOutput({ result, isRunning, stdin, onStdinChange }) {
  const [activeTab, setActiveTab] = useState('output'); // 'output' | 'input'

  // Automatically switch to output tab when code starts executing or receives new results
  useEffect(() => {
    if (isRunning) {
      setActiveTab('output');
    }
  }, [isRunning]);

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

  const hasInput = stdin && stdin.trim().length > 0;
  const isInputNeeded = result && (
    result.status === 'TIMEOUT' ||
    (result.stderr && (
      result.stderr.includes('NoSuchElementException') ||
      result.stderr.includes('EOFError') ||
      result.stderr.includes('Input Required')
    ))
  );

  return (
    <div className="console-pane">
      {/* Console Header with Navigation Tabs */}
      <div className="console-header" style={{ padding: '0 0.5rem', height: '36px' }}>
        <div style={{ display: 'flex', alignItems: 'stretch', height: '100%', gap: '0.25rem' }}>
          {/* Tab 1: Output */}
          <button
            type="button"
            className={`console-tab-btn ${activeTab === 'output' ? 'active' : ''}`}
            onClick={() => setActiveTab('output')}
            title="View program stdout and stderr"
          >
            <Terminal size={13} />
            <span>Execution Output</span>
            {result && getStatusBadge(result.status)}
          </button>

          {/* Tab 2: Standard Input */}
          <button
            type="button"
            className={`console-tab-btn ${activeTab === 'input' ? 'active' : ''}`}
            onClick={() => setActiveTab('input')}
            title="Provide standard input (stdin) for Scanner / input()"
          >
            <Keyboard size={13} />
            <span>Standard Input (stdin)</span>
            {hasInput ? (
              <span className="badge badge-success" style={{ padding: '0.1rem 0.4rem', fontSize: '0.65rem' }}>
                Active
              </span>
            ) : (
              <span style={{ color: '#64748b', fontSize: '0.7rem' }}>
                (Empty)
              </span>
            )}
          </button>
        </div>

        {/* Right Header Controls */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
          {activeTab === 'output' && result && result.executionTimeMs !== undefined && (
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.75rem', color: '#94a3b8' }}>
              <Clock size={12} />
              {result.executionTimeMs} ms
            </span>
          )}

          {activeTab === 'input' && hasInput && (
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              style={{ padding: '0.15rem 0.5rem', fontSize: '0.7rem', display: 'flex', alignItems: 'center', gap: '0.3rem' }}
              onClick={() => onStdinChange('')}
              title="Clear all input text"
            >
              <Trash2 size={11} style={{ color: '#f87171' }} />
              <span>Clear</span>
            </button>
          )}
        </div>
      </div>

      {/* Tab 1 Body: Output Console */}
      {activeTab === 'output' && (
        <div className="console-body">
          {/* Helpful alert if the program timed out or crashed due to missing Scanner/input */}
          {isInputNeeded && (
            <div style={{
              background: 'rgba(234, 179, 8, 0.12)',
              border: '1px solid rgba(234, 179, 8, 0.35)',
              borderRadius: '6px',
              padding: '0.6rem 0.8rem',
              marginBottom: '0.75rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '0.75rem'
            }}>
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '0.5rem', color: '#fef08a', fontSize: '0.8rem' }}>
                <AlertTriangle size={16} style={{ color: '#facc15', flexShrink: 0, marginTop: '2px' }} />
                <div>
                  <strong>Does your program use <code>Scanner</code> (Java) or <code>input()</code> (Python)?</strong>
                  <div style={{ color: '#e2e8f0', fontSize: '0.75rem', marginTop: '2px' }}>
                    Online code runners do not have interactive popup terminals. Enter your test input beforehand in the <b>Standard Input (stdin)</b> tab!
                  </div>
                </div>
              </div>
              <button
                type="button"
                className="btn btn-sm"
                style={{
                  background: '#facc15',
                  color: '#0f172a',
                  fontWeight: 600,
                  fontSize: '0.75rem',
                  padding: '0.3rem 0.65rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.3rem',
                  flexShrink: 0
                }}
                onClick={() => setActiveTab('input')}
              >
                <span>Enter Input</span>
                <ArrowRight size={12} />
              </button>
            </div>
          )}

          {isRunning ? (
            <div style={{ color: '#818cf8', fontStyle: 'italic', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <div className="spinner-sm" style={{
                width: '14px',
                height: '14px',
                border: '2px solid rgba(129, 140, 248, 0.2)',
                borderTopColor: '#818cf8',
                borderRadius: '50%',
                animation: 'spin 0.8s linear infinite'
              }} />
              <span>Compiling and executing in isolated sandbox...</span>
            </div>
          ) : !result ? (
            <div style={{ color: '#64748b', lineHeight: 1.6 }}>
              Click <b>"Run Code"</b> to execute in sandbox, or switch to <b>"Standard Input (stdin)"</b> to supply input values.
            </div>
          ) : (
            <>
              {result.stdout && (
                <div style={{ color: '#f8fafc', whiteSpace: 'pre-wrap' }}>
                  {result.stdout}
                </div>
              )}
              {result.stderr && (
                <div style={{ color: '#f87171', marginTop: result.stdout ? '0.6rem' : '0', whiteSpace: 'pre-wrap' }}>
                  {result.stderr}
                </div>
              )}
              {!result.stdout && !result.stderr && (
                <div style={{ color: '#64748b', fontStyle: 'italic' }}>
                  Program finished with exit code {result.exitCode} (no output printed to console).
                </div>
              )}
            </>
          )}
        </div>
      )}

      {/* Tab 2 Body: Standard Input (stdin) */}
      {activeTab === 'input' && (
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: '0.6rem 0.85rem', background: '#080c14', overflowY: 'auto' }}>
          {/* Educational explanation for students */}
          <div style={{
            background: 'rgba(30, 41, 59, 0.6)',
            border: '1px solid rgba(148, 163, 184, 0.15)',
            borderRadius: '6px',
            padding: '0.5rem 0.75rem',
            marginBottom: '0.5rem',
            fontSize: '0.78rem',
            color: '#cbd5e1',
            lineHeight: 1.45
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontWeight: 600, color: '#38bdf8', marginBottom: '0.2rem' }}>
              <HelpCircle size={13} />
              <span>How Standard Input (stdin) Works:</span>
            </div>
            <div>
              When your code calls <code>Scanner.nextInt()</code>, <code>Scanner.nextLine()</code> (Java) or <code>input()</code> (Python), it reads directly from the text box below.
              Provide your input values here <b>before clicking "Run Code"</b>. Separate multiple values with spaces or newlines.
            </div>

            {/* Quick Preset Buttons */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginTop: '0.4rem', flexWrap: 'wrap' }}>
              <span style={{ fontSize: '0.72rem', color: '#94a3b8' }}>Quick Presets:</span>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                style={{ fontSize: '0.7rem', padding: '0.1rem 0.45rem' }}
                onClick={() => onStdinChange("5\n10 20 30 40 50")}
              >
                Array Input (5 \n 10 20 30 40 50)
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                style={{ fontSize: '0.7rem', padding: '0.1rem 0.45rem' }}
                onClick={() => onStdinChange("15 25")}
              >
                Two Numbers (15 25)
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                style={{ fontSize: '0.7rem', padding: '0.1rem 0.45rem' }}
                onClick={() => onStdinChange("Hello World")}
              >
                Text ("Hello World")
              </button>
            </div>
          </div>

          {/* Multi-line Stdin Textarea */}
          <textarea
            className="console-stdin-textarea"
            placeholder="Type standard input values here (e.g. 42 or multiple lines for Scanner)..."
            value={stdin}
            onChange={(e) => onStdinChange(e.target.value)}
            rows={4}
            style={{
              width: '100%',
              flex: 1,
              minHeight: '80px',
              background: '#0d1321',
              border: hasInput ? '1px solid rgba(16, 185, 129, 0.4)' : '1px solid var(--border-color)',
              color: '#f8fafc',
              padding: '0.5rem 0.75rem',
              borderRadius: '6px',
              fontSize: '0.82rem',
              fontFamily: 'var(--font-mono)',
              lineHeight: 1.5,
              resize: 'none',
              outline: 'none'
            }}
          />
        </div>
      )}
    </div>
  );
}
