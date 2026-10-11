import React from 'react';
import { 
  Brain, 
  Sparkles, 
  CheckCircle2, 
  AlertTriangle, 
  Layers, 
  BookMarked,
  ArrowRight,
  RefreshCw
} from 'lucide-react';
import HintLadder from './HintLadder';

export default function MentorPanel({
  analysis,
  isAnalyzing,
  currentLevel,
  hintsByLevel,
  canAdvance,
  onRequestNextHint,
  onRevealSolution,
  isAdvancingHint,
  isRevealingSolution,
  onApplySolutionToEditor,
  onNextProblem,
  language = 'java'
}) {
  return (
    <div className="mentor-pane">
      {/* Mentor Header */}
      <div className="mentor-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
          <Brain size={20} style={{ color: '#818cf8' }} />
          <div>
            <h2 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#fff' }}>
              Adaptive AI Mentor
            </h2>
            <p style={{ fontSize: '0.72rem', color: '#94a3b8' }}>
              Socratic Scaffolding & Mistake Diagnosis
            </p>
          </div>
        </div>

        {analysis && (
          <span className={`badge ${analysis.isCorrect ? 'badge-success' : 'badge-warning'}`}>
            {analysis.isCorrect ? 'Solved' : 'Revision Needed'}
          </span>
        )}
      </div>

      <div className="mentor-content">
        {isAnalyzing ? (
          <div style={{ textAlign: 'center', padding: '3rem 1rem', color: '#94a3b8' }}>
            <RefreshCw size={28} className="animate-spin mb-3" style={{ color: '#818cf8', animation: 'spin 1s linear infinite', margin: '0 auto 0.75rem' }} />
            <p style={{ fontWeight: 600, color: '#fff' }}>Synthesizing Socratic Diagnosis...</p>
            <p style={{ fontSize: '0.78rem', color: '#64748b', marginTop: '0.25rem' }}>
              Checking AST invariants, edge cases, and student coding DNA
            </p>
          </div>
        ) : !analysis ? (
          /* Initial Empty State */
          <div style={{ padding: '2rem 1rem', textAlign: 'center', color: '#64748b' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '50%', background: 'rgba(99, 102, 241, 0.1)', color: '#818cf8', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem' }}>
              <Sparkles size={24} />
            </div>
            <h4 style={{ color: '#fff', fontSize: '0.95rem', fontWeight: 600, marginBottom: '0.4rem' }}>
              Ready for Socratic Mentorship
            </h4>
            <p style={{ fontSize: '0.8rem', lineHeight: 1.5, color: '#94a3b8' }}>
              Write your implementation and click <strong>"Analyze with AI Mentor"</strong>. 
              The system will detect flaws, log mistake trajectories, and release progressive hints level by level.
            </p>
          </div>
        ) : (
          <>
            {/* Correct Solution State */}
            {analysis.isCorrect ? (
              <div style={{ background: 'rgba(16, 185, 129, 0.1)', border: '1px solid rgba(16, 185, 129, 0.3)', borderRadius: '8px', padding: '1rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#34d399', marginBottom: '0.5rem' }}>
                  <CheckCircle2 size={20} />
                  <h4 style={{ fontWeight: 700, fontSize: '0.95rem', color: '#fff' }}>
                    Correct Implementation!
                  </h4>
                </div>
                <p style={{ color: '#cbd5e1', fontSize: '0.82rem', lineHeight: 1.4, marginBottom: '0.75rem' }}>
                  {analysis.hint || 'Excellent work! Your code passes the invariant tests and fulfills the problem specification.'}
                </p>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '0.5rem', borderTop: '1px solid rgba(16, 185, 129, 0.2)' }}>
                  <span style={{ fontSize: '0.75rem', color: '#34d399', fontWeight: 600 }}>
                    Mastery: {analysis.masteryScore}% (+10%)
                  </span>
                  {onNextProblem && (
                    <button className="btn btn-success btn-sm" onClick={onNextProblem}>
                      <span>Next Adaptive Problem</span>
                      <ArrowRight size={13} />
                    </button>
                  )}
                </div>
              </div>
            ) : (
              /* Incorrect / Buggy State: Concept + Mistakes */
              <>
                {/* Concept Banner */}
                {analysis.concept && (
                  <div style={{ background: 'rgba(6, 182, 212, 0.1)', border: '1px solid rgba(6, 182, 212, 0.25)', borderRadius: '8px', padding: '0.75rem 1rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#22d3ee', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase' }}>
                      <BookMarked size={14} />
                      <span>Pedagogical Core Concept</span>
                    </div>
                    <p style={{ color: '#f1f5f9', fontWeight: 600, fontSize: '0.85rem', marginTop: '0.2rem' }}>
                      {analysis.concept}
                    </p>
                  </div>
                )}

                {/* Detected Mistakes List */}
                {analysis.mistakes && analysis.mistakes.length > 0 && (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#f87171', fontSize: '0.78rem', fontWeight: 700 }}>
                      <AlertTriangle size={14} />
                      <span>Diagnosed Semantic Flaws ({analysis.mistakes.length})</span>
                    </div>

                    {analysis.mistakes.map((m, idx) => (
                      <div key={idx} className="mistake-card">
                        <div className="mistake-header">
                          <span>{m.mistakeType || m.type}</span>
                          {m.lineNumber && (
                            <span className="badge badge-secondary" style={{ fontSize: '0.68rem' }}>
                              Line {m.lineNumber}
                            </span>
                          )}
                        </div>
                        <p style={{ fontSize: '0.78rem', color: '#e2e8f0', lineHeight: 1.4 }}>
                          {m.description}
                        </p>
                      </div>
                    ))}
                  </div>
                )}
              </>
            )}

            {/* Progressive 4-Tier Hint Ladder */}
            <HintLadder
              hintSessionId={analysis.hintSessionId}
              currentLevel={currentLevel}
              hintsByLevel={hintsByLevel}
              canAdvance={canAdvance}
              onRequestNextHint={onRequestNextHint}
              onRevealSolution={onRevealSolution}
              isAdvancingHint={isAdvancingHint}
              isRevealingSolution={isRevealingSolution}
              onApplySolutionToEditor={onApplySolutionToEditor}
              language={language}
            />
          </>
        )}
      </div>
    </div>
  );
}
