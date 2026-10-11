import React, { useState } from 'react';
import { 
  HelpCircle, 
  Lightbulb, 
  Search, 
  KeyRound, 
  Lock, 
  Unlock, 
  ChevronRight, 
  AlertCircle,
  Copy,
  Check
} from 'lucide-react';

export default function HintLadder({
  hintSessionId,
  currentLevel,
  hintsByLevel,
  canAdvance,
  onRequestNextHint,
  onRevealSolution,
  isAdvancingHint,
  isRevealingSolution,
  onApplySolutionToEditor,
  language = 'java'
}) {
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [copied, setCopied] = useState(false);
  const [inserted, setInserted] = useState(false);

  const levelsConfig = [
    {
      level: 1,
      title: 'Level 1: Guiding Question',
      sub: 'Points toward suspect area without revealing the fix',
      icon: HelpCircle,
      badge: 'Socratic Clue',
    },
    {
      level: 2,
      title: 'Level 2: Conceptual Insight',
      sub: 'Explains underlying computing principle & invariants',
      icon: Lightbulb,
      badge: 'Concept Refresher',
    },
    {
      level: 3,
      title: 'Level 3: Deep Failure Diagnosis',
      sub: 'Explains why the logic fails and steps to resolve it',
      icon: Search,
      badge: 'Detailed Diagnosis',
    },
    {
      level: 4,
      title: 'Level 4: Corrected Solution Code',
      sub: 'Reference code implementation with explanations',
      icon: KeyRound,
      badge: 'Explicit Solution',
    },
  ];

  const handleCopyCode = (text) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="hint-ladder">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.85rem' }}>
        <h3 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#fff' }}>
          Progressive Hint Ladder (4 Levels)
        </h3>
        <span className="badge badge-accent">
          Active Level: {currentLevel} of 4
        </span>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column' }}>
        {levelsConfig.map((cfg) => {
          const isUnlocked = currentLevel >= cfg.level;
          const isCurrent = currentLevel === cfg.level;
          const hintText = hintsByLevel[cfg.level];
          const Icon = cfg.icon;

          return (
            <div key={cfg.level} className="ladder-step">
              <div
                className={`ladder-number ${
                  cfg.level === 4 && isUnlocked
                    ? 'solution'
                    : isUnlocked
                    ? 'unlocked'
                    : 'locked'
                }`}
              >
                {isUnlocked ? (
                  cfg.level === 4 ? <KeyRound size={14} /> : cfg.level
                ) : (
                  <Lock size={12} />
                )}
              </div>

              <div className="ladder-body">
                <div className="ladder-title">
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <Icon size={14} style={{ color: isUnlocked ? '#818cf8' : '#64748b' }} />
                    <span style={{ color: isUnlocked ? '#fff' : '#64748b' }}>
                      {cfg.title}
                    </span>
                  </div>
                  <span className={`badge ${isUnlocked ? 'badge-accent' : 'badge-secondary'}`}>
                    {isUnlocked ? 'Unlocked' : 'Locked'}
                  </span>
                </div>

                <div className="ladder-desc">
                  {isUnlocked && hintText ? (
                    <div style={{ marginTop: '0.4rem' }}>
                      {cfg.level === 4 ? (
                        <SolutionViewer
                          solutionText={hintText}
                          activeEditorLang={language}
                          onApplySolutionToEditor={onApplySolutionToEditor}
                        />
                      ) : (
                        <p style={{ color: '#e2e8f0', background: 'rgba(255, 255, 255, 0.04)', padding: '0.5rem 0.75rem', borderRadius: '6px', borderLeft: '3px solid #6366f1' }}>
                          {hintText}
                        </p>
                      )}
                    </div>
                  ) : (
                    <span style={{ color: '#64748b', fontSize: '0.78rem' }}>
                      {cfg.sub}
                    </span>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Ladder Action Controls */}
      <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', paddingTop: '0.75rem', borderTop: '1px solid var(--border-color)' }}>
        {canAdvance && currentLevel < 3 && (
          <button
            className="btn btn-primary btn-sm"
            style={{ flex: 1 }}
            onClick={onRequestNextHint}
            disabled={isAdvancingHint || isRevealingSolution}
          >
            <Lightbulb size={13} />
            <span>{isAdvancingHint ? 'Generating Hint...' : `Need a stronger hint (Level ${currentLevel + 1})`}</span>
          </button>
        )}

        {currentLevel < 4 && (
          <button
            className="btn btn-danger btn-sm"
            onClick={() => setShowConfirmModal(true)}
            disabled={isAdvancingHint || isRevealingSolution}
          >
            <KeyRound size={13} />
            <span>Show Solution</span>
          </button>
        )}
      </div>

      {/* Confirmation Modal for Solution Reveal */}
      {showConfirmModal && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', color: '#f59e0b', marginBottom: '0.75rem' }}>
              <AlertCircle size={22} />
              <h4 style={{ fontSize: '1.05rem', fontWeight: 700, color: '#fff' }}>
                Reveal Solution Code?
              </h4>
            </div>

            <p style={{ color: '#cbd5e1', fontSize: '0.85rem', lineHeight: 1.5, marginBottom: '1.25rem' }}>
              Our AI Mentor is designed to guide your problem-solving struggle. 
              Revealing the solution will unlock Level 4 and slightly calibrate your topic mastery score down.
              Are you sure you want to view the reference code now?
            </p>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setShowConfirmModal(false)}
              >
                Keep Trying Myself
              </button>
              <button
                className="btn btn-danger btn-sm"
                onClick={() => {
                  setShowConfirmModal(false);
                  onRevealSolution();
                }}
              >
                Yes, Show Solution
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function SolutionViewer({ solutionText, activeEditorLang, onApplySolutionToEditor }) {
  const [activeTab, setActiveTab] = React.useState(activeEditorLang === 'python' ? 'python' : 'java');
  const [copied, setCopied] = React.useState(false);
  const [inserted, setInserted] = React.useState(false);

  // Sync tab if active editor language changes
  React.useEffect(() => {
    if (activeEditorLang === 'python') setActiveTab('python');
    else if (activeEditorLang === 'java') setActiveTab('java');
  }, [activeEditorLang]);

  // Parse dual solution (Java + Python)
  const parsed = React.useMemo(() => {
    if (!solutionText) return { java: '', python: '', both: '' };
    const pyMarker = '# ==========================================';
    const pyIdx = solutionText.indexOf(pyMarker);
    if (pyIdx !== -1) {
      let javaPart = solutionText.substring(0, pyIdx)
        .replace('// ==========================================', '')
        .replace('// JAVA (OpenJDK 17)', '')
        .trim();
      let pythonPart = solutionText.substring(pyIdx)
        .replace('# ==========================================', '')
        .replace('# PYTHON 3', '')
        .trim();
      return { java: javaPart, python: pythonPart, both: solutionText };
    }
    return { java: solutionText, python: solutionText, both: solutionText };
  }, [solutionText]);

  const displayedCode = activeTab === 'java' ? parsed.java : activeTab === 'python' ? parsed.python : parsed.both;

  const handleCopy = () => {
    navigator.clipboard.writeText(displayedCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleInsert = () => {
    if (onApplySolutionToEditor) {
      const codeToApply = activeTab === 'python' ? parsed.python : activeTab === 'java' ? parsed.java : parsed.both;
      const targetLang = activeTab === 'python' ? 'python' : activeTab === 'java' ? 'java' : activeEditorLang;
      onApplySolutionToEditor(codeToApply, targetLang);
      setInserted(true);
      setTimeout(() => setInserted(false), 2500);
    }
  };

  return (
    <div style={{ background: '#0a0e1a', borderRadius: '6px', padding: '0.75rem', border: '1px solid var(--border-color)' }}>
      {/* Top Bar: Language Switcher Tabs & Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.6rem', flexWrap: 'wrap', gap: '0.4rem' }}>
        {/* Language Tabs */}
        <div style={{ display: 'flex', gap: '0.3rem', background: 'rgba(255,255,255,0.06)', padding: '0.2rem', borderRadius: '6px' }}>
          <button
            type="button"
            className={`btn btn-sm ${activeTab === 'java' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ fontSize: '0.72rem', padding: '0.2rem 0.5rem', height: 'auto', fontWeight: activeTab === 'java' ? 700 : 500 }}
            onClick={() => setActiveTab('java')}
          >
            ☕ Java
          </button>
          <button
            type="button"
            className={`btn btn-sm ${activeTab === 'python' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ fontSize: '0.72rem', padding: '0.2rem 0.5rem', height: 'auto', fontWeight: activeTab === 'python' ? 700 : 500 }}
            onClick={() => setActiveTab('python')}
          >
            🐍 Python 3
          </button>
          <button
            type="button"
            className={`btn btn-sm ${activeTab === 'both' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ fontSize: '0.72rem', padding: '0.2rem 0.5rem', height: 'auto', fontWeight: activeTab === 'both' ? 700 : 500 }}
            onClick={() => setActiveTab('both')}
          >
            📄 Both (Java & Python)
          </button>
        </div>

        {/* Copy & Insert in Editor Buttons */}
        <div style={{ display: 'flex', gap: '0.4rem' }}>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            style={{ padding: '0.15rem 0.45rem', fontSize: '0.7rem' }}
            onClick={handleCopy}
            title="Copy displayed solution to clipboard"
          >
            {copied ? <Check size={11} /> : <Copy size={11} />}
            <span>{copied ? 'Copied' : 'Copy'}</span>
          </button>
          {onApplySolutionToEditor && (
            <button
              type="button"
              className={`btn ${inserted ? 'btn-success' : 'btn-primary'} btn-sm`}
              style={{ padding: '0.15rem 0.55rem', fontSize: '0.7rem' }}
              onClick={handleInsert}
              title={`Insert ${activeTab === 'python' ? 'Python' : activeTab === 'java' ? 'Java' : 'Solution'} into editor and switch language mode`}
            >
              {inserted && <Check size={11} />}
              <span>{inserted ? 'Inserted in Editor ✓' : `Insert ${activeTab === 'python' ? 'Python' : activeTab === 'java' ? 'Java' : 'Solution'} in Editor`}</span>
            </button>
          )}
        </div>
      </div>

      {/* Code Display Area */}
      <pre style={{ fontFamily: 'var(--font-mono)', fontSize: '0.78rem', color: '#e2e8f0', whiteSpace: 'pre-wrap', maxHeight: '220px', overflowY: 'auto', margin: 0, padding: '0.6rem', background: '#060913', borderRadius: '4px', border: '1px solid rgba(255,255,255,0.04)' }}>
        {displayedCode}
      </pre>
    </div>
  );
}
