import React, { useState } from 'react';
import Editor from '@monaco-editor/react';
import { 
  Play, 
  Sparkles, 
  RotateCcw, 
  Code, 
  FileText, 
  ChevronDown, 
  ChevronUp, 
  Trash2, 
  FileCode2 
} from 'lucide-react';

export default function CodeEditor({
  code,
  onChange,
  language,
  onLanguageChange,
  onRun,
  onAnalyze,
  onReset,
  isRunning,
  isAnalyzing,
  currentProblem
}) {
  const [fontSize, setFontSize] = useState(14);
  const [monacoFailed, setMonacoFailed] = useState(false);
  const [showProblemDetails, setShowProblemDetails] = useState(true);

  const handleEditorMount = (editor, monaco) => {
    // Configure Monaco options
    editor.updateOptions({
      minimap: { enabled: false },
      scrollBeyondLastLine: false,
      tabSize: 4,
      smoothScrolling: true,
      formatOnPaste: true,
    });
  };

  const handleClearCode = () => {
    onChange('');
  };

  const handleInsertStarter = () => {
    if (currentProblem && currentProblem.starterCode) {
      onChange(currentProblem.starterCode);
    } else {
      onChange(
        language === 'python'
          ? 'class Solution:\n    def solve(self):\n        pass\n'
          : 'public class Solution {\n    public static void main(String[] args) {\n        // Write your solution here\n    }\n}'
      );
    }
  };

  return (
    <div className="editor-pane">
      {/* Problem Title Bar */}
      <div className="problem-header">
        <div className="problem-info">
          <Code size={18} style={{ color: '#818cf8' }} />
          <span className="problem-title">
            {currentProblem ? currentProblem.title : 'Scratchpad Playground'}
          </span>
          {currentProblem && (
            <span className={`badge ${
              currentProblem.difficulty === 'BEGINNER' || currentProblem.difficulty === 'EASY'
                ? 'badge-success'
                : currentProblem.difficulty === 'MEDIUM'
                ? 'badge-warning'
                : 'badge-danger'
            }`}>
              {currentProblem.difficulty}
            </span>
          )}
          {currentProblem && (
            <span className="badge badge-secondary">{currentProblem.topic}</span>
          )}
          {currentProblem?.isGenerated && (
            <span className="badge badge-accent" title="Dynamically generated challenge">
              Isomorphic AI
            </span>
          )}
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
          {currentProblem && (
            <button
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '0.75rem', padding: '0.25rem 0.6rem' }}
              onClick={() => setShowProblemDetails(!showProblemDetails)}
              title="Toggle problem statement & examples"
            >
              <FileText size={13} style={{ color: '#818cf8' }} />
              <span>{showProblemDetails ? 'Hide Problem' : 'View Problem'}</span>
              {showProblemDetails ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
            </button>
          )}

          {currentProblem?.isSolved && (
            <span className="badge badge-success">
              Solved ✓
            </span>
          )}
        </div>
      </div>

      {/* Expandable Problem Statement Drawer */}
      {currentProblem && showProblemDetails && (
        <div
          style={{
            background: 'rgba(10, 15, 29, 0.95)',
            borderBottom: '1px solid var(--border-color)',
            padding: '1rem 1.25rem',
            maxHeight: '180px',
            overflowY: 'auto',
            fontSize: '0.85rem',
            lineHeight: 1.55,
            color: '#cbd5e1',
          }}
        >
          <div style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit' }}>
            {currentProblem.description}
          </div>
        </div>
      )}

      {/* Editor Controls Toolbar */}
      <div className="editor-toolbar">
        <div className="editor-toolbar-left">
          <select
            className="lang-select"
            value={language}
            onChange={(e) => onLanguageChange(e.target.value)}
          >
            <option value="java">Java (OpenJDK 17)</option>
            <option value="python">Python 3</option>
          </select>

          <select
            className="lang-select"
            value={fontSize}
            onChange={(e) => setFontSize(Number(e.target.value))}
          >
            <option value={13}>13px</option>
            <option value={14}>14px</option>
            <option value={16}>16px</option>
          </select>

          <button
            className="btn btn-secondary btn-sm"
            onClick={handleClearCode}
            title="Clear the workspace to a blank empty canvas"
          >
            <Trash2 size={13} style={{ color: '#f87171' }} />
            <span>Clear (Blank)</span>
          </button>

          <button
            className="btn btn-secondary btn-sm"
            onClick={handleInsertStarter}
            title="Insert a starter method template"
          >
            <FileCode2 size={13} style={{ color: '#38bdf8' }} />
            <span>Starter Stub</span>
          </button>
        </div>

        <div className="editor-toolbar-right">
          <button
            className="btn btn-secondary btn-sm"
            onClick={onRun}
            disabled={isRunning || isAnalyzing}
          >
            <Play size={14} style={{ color: '#10b981' }} />
            <span>{isRunning ? 'Running...' : 'Run Code'}</span>
          </button>

          <button
            className="btn btn-primary btn-sm"
            onClick={onAnalyze}
            disabled={isRunning || isAnalyzing}
          >
            <Sparkles size={14} />
            <span>{isAnalyzing ? 'Analyzing...' : 'Analyze with AI Mentor'}</span>
          </button>
        </div>
      </div>

      {/* Code Editor Body */}
      <div className="monaco-container">
        {!monacoFailed ? (
          <Editor
            height="100%"
            language={language === 'python' ? 'python' : 'java'}
            theme="vs-dark"
            value={code}
            onChange={(val) => onChange(val || '')}
            onMount={handleEditorMount}
            loading={
              <div style={{ padding: '2rem', color: '#94a3b8', textAlign: 'center' }}>
                Loading Monaco IDE Editor...
              </div>
            }
            options={{
              fontSize: fontSize,
              fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
              fontLigatures: true,
              automaticLayout: true,
              cursorBlinking: 'smooth',
            }}
          />
        ) : (
          <textarea
            style={{
              width: '100%',
              height: '100%',
              backgroundColor: '#0f172a',
              color: '#f8fafc',
              fontFamily: "'JetBrains Mono', monospace",
              fontSize: `${fontSize}px`,
              padding: '1rem',
              border: 'none',
              outline: 'none',
              resize: 'none',
            }}
            value={code}
            onChange={(e) => onChange(e.target.value)}
          />
        )}
      </div>
    </div>
  );
}
