import React, { useState, useEffect } from 'react';
import CodeEditor from '../components/CodeEditor';
import RunOutput from '../components/RunOutput';
import MentorPanel from '../components/MentorPanel';
import { api } from '../api/client';
import { CheckCircle2, AlertTriangle, ArrowRight, Dna, Sparkles } from 'lucide-react';

export default function Workspace({ currentProblem, onSelectNextProblem }) {
  // Empty workspace by default: student writes code from scratch
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState(currentProblem?.language || 'java');
  const [stdin, setStdin] = useState('');

  // Code runner state
  const [isRunning, setIsRunning] = useState(false);
  const [runResult, setRunResult] = useState(null);

  // Code submission state
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submissionModal, setSubmissionModal] = useState(null);

  // AI analysis state
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysis, setAnalysis] = useState(null);

  // Progressive hints state (1-4)
  const [currentLevel, setCurrentLevel] = useState(1);
  const [hintsByLevel, setHintsByLevel] = useState({});
  const [canAdvance, setCanAdvance] = useState(false);
  const [isAdvancingHint, setIsAdvancingHint] = useState(false);
  const [isRevealingSolution, setIsRevealingSolution] = useState(false);

  // When currentProblem changes, open workspace for the student
  useEffect(() => {
    if (currentProblem) {
      setCode(''); // Empty workspace for student to write solution
      setLanguage(currentProblem.language || 'java');
      // Reset hints & analysis for new problem
      setAnalysis(null);
      setRunResult(null);
      setSubmissionModal(null);
      setCurrentLevel(1);
      setHintsByLevel({});
      setCanAdvance(false);
    }
  }, [currentProblem?.id]);

  // Handle Quick Code Execution (Test in Sandbox without official grading)
  const handleRunCode = async () => {
    setIsRunning(true);
    try {
      const res = await api.runCode(code, language, stdin);
      setRunResult(res);
    } catch (err) {
      setRunResult({
        status: 'INTERNAL_ERROR',
        stdout: '',
        stderr: err.message || 'Execution failed',
        executionTimeMs: 0,
        exitCode: -1,
      });
    } finally {
      setIsRunning(false);
    }
  };

  // Handle Official Code Submission (Grades code, updates problem Solved status, and increments Dashboard metrics)
  const handleSubmitCode = async () => {
    setIsSubmitting(true);
    try {
      const res = await api.submitCode(code, language, currentProblem?.id);

      // 1. Update output console pane with execution & verdict
      setRunResult({
        status: res.isCorrect ? 'SUCCESS' : (res.stderr ? 'RUNTIME_ERROR' : 'REVISION_NEEDED'),
        stdout: res.stdout || (res.isCorrect ? '✓ Verification passed! All test invariants satisfied.' : 'Submission evaluated. See diagnosed flaws in AI Mentor panel.'),
        stderr: res.stderr || '',
        executionTimeMs: res.executionTimeMs || 0,
        exitCode: res.exitCode || (res.isCorrect ? 0 : 1),
        isSubmission: true,
      });

      // 2. Update AI Mentor panel
      setAnalysis({
        isCorrect: res.isCorrect,
        concept: res.concept,
        hint: res.hint,
        mistakes: res.mistakes,
        masteryScore: res.masteryScore,
        topic: res.topic,
        submissionId: res.submissionId,
        hintSessionId: res.hintSessionId,
      });

      if (!res.isCorrect) {
        setCurrentLevel(1);
        setHintsByLevel({ 1: res.hint });
        setCanAdvance(true);
      } else {
        setCanAdvance(false);
        if (currentProblem) {
          currentProblem.isSolved = true;
        }
      }

      // 3. Display explicit submission verdict modal
      setSubmissionModal({
        show: true,
        isCorrect: res.isCorrect,
        problemTitle: currentProblem?.title || 'Ad-Hoc Exercise',
        topic: res.topic || currentProblem?.topic || 'General',
        topicSolved: res.topicSolved !== undefined ? res.topicSolved : (res.isCorrect ? 1 : 0),
        topicAttempts: res.topicAttempts || 1,
        totalSolved: res.totalSolved !== undefined ? res.totalSolved : (res.isCorrect ? 1 : 0),
        totalSubmissions: res.totalSubmissions || 1,
        masteryScore: res.masteryScore,
        hint: res.hint,
        mistakesCount: res.mistakes?.length || 0,
      });

    } catch (err) {
      alert('Submission error: ' + (err.message || 'Failed to submit code'));
    } finally {
      setIsSubmitting(false);
    }
  };

  // Handle Socratic AI Code Analysis (Level 1)
  const handleAnalyzeCode = async () => {
    setIsAnalyzing(true);
    try {
      const res = await api.analyzeCode(code, language, currentProblem?.id);
      setAnalysis(res);
      setCurrentLevel(1);
      setHintsByLevel({ 1: res.hint });
      setCanAdvance(!res.isCorrect); // if wrong, can request higher hints
    } catch (err) {
      alert('Analysis error: ' + (err.message || 'Failed to analyze code'));
    } finally {
      setIsAnalyzing(false);
    }
  };

  // Handle Progressive Hint Request (Level 2 or 3)
  const handleRequestNextHint = async () => {
    if (!analysis?.hintSessionId) return;
    setIsAdvancingHint(true);
    try {
      const res = await api.getNextHint(analysis.hintSessionId);
      setCurrentLevel(res.level);
      setHintsByLevel((prev) => ({
        ...prev,
        [res.level]: res.hint,
      }));
      setCanAdvance(res.canAdvance);
    } catch (err) {
      alert(err.message || 'Could not advance hint');
    } finally {
      setIsAdvancingHint(false);
    }
  };

  // Handle Solution Reveal (Level 4)
  const handleRevealSolution = async () => {
    if (!analysis?.hintSessionId) return;
    setIsRevealingSolution(true);
    try {
      const res = await api.revealSolution(analysis.hintSessionId);
      setCurrentLevel(4);
      setHintsByLevel((prev) => ({
        ...prev,
        4: res.solution,
      }));
      setCanAdvance(false);
    } catch (err) {
      alert(err.message || 'Could not reveal solution');
    } finally {
      setIsRevealingSolution(false);
    }
  };

  // Apply solution to editor directly
  const handleApplySolutionToEditor = (solutionCode, targetLang) => {
    setCode(solutionCode);
    if (targetLang && (targetLang === 'java' || targetLang === 'python')) {
      setLanguage(targetLang);
    }
  };

  const handleResetCode = () => {
    if (currentProblem && currentProblem.starterCode) {
      setCode(currentProblem.starterCode);
    }
  };

  return (
    <div className="workspace-layout">
      {/* Left Column: Monaco Code Editor + Output Console */}
      <div style={{ display: 'flex', flexDirection: 'column', height: '100%', overflow: 'hidden' }}>
        <CodeEditor
          code={code}
          onChange={setCode}
          language={language}
          onLanguageChange={setLanguage}
          onRun={handleRunCode}
          onSubmit={handleSubmitCode}
          onAnalyze={handleAnalyzeCode}
          onReset={handleResetCode}
          isRunning={isRunning}
          isSubmitting={isSubmitting}
          isAnalyzing={isAnalyzing}
          currentProblem={currentProblem}
        />

        <RunOutput
          result={runResult}
          isRunning={isRunning}
          stdin={stdin}
          onStdinChange={setStdin}
        />
      </div>

      {/* Right Column: AI Mentor Socratic Scaffolding Panel */}
      <MentorPanel
        analysis={analysis}
        isAnalyzing={isAnalyzing}
        currentLevel={currentLevel}
        hintsByLevel={hintsByLevel}
        canAdvance={canAdvance}
        onRequestNextHint={handleRequestNextHint}
        onRevealSolution={handleRevealSolution}
        isAdvancingHint={isAdvancingHint}
        isRevealingSolution={isRevealingSolution}
        onApplySolutionToEditor={handleApplySolutionToEditor}
        onNextProblem={onSelectNextProblem}
        language={language}
      />

      {/* Submission Result Modal */}
      {submissionModal?.show && (
        <div className="modal-overlay" style={{ zIndex: 1000 }}>
          <div
            className="modal-card"
            style={{
              maxWidth: '520px',
              textAlign: 'center',
              padding: '1.75rem',
              border: submissionModal.isCorrect
                ? '1px solid rgba(16, 185, 129, 0.4)'
                : '1px solid rgba(245, 158, 11, 0.4)',
              boxShadow: submissionModal.isCorrect
                ? '0 20px 45px rgba(16, 185, 129, 0.15)'
                : '0 20px 45px rgba(245, 158, 11, 0.15)',
            }}
          >
            <div
              style={{
                width: '60px',
                height: '60px',
                borderRadius: '50%',
                background: submissionModal.isCorrect
                  ? 'rgba(16, 185, 129, 0.15)'
                  : 'rgba(245, 158, 11, 0.15)',
                color: submissionModal.isCorrect ? '#34d399' : '#fbbf24',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 1.1rem',
              }}
            >
              {submissionModal.isCorrect ? (
                <CheckCircle2 size={34} />
              ) : (
                <AlertTriangle size={34} />
              )}
            </div>

            <h3 style={{ fontSize: '1.3rem', fontWeight: 800, color: '#ffffff', marginBottom: '0.4rem' }}>
              {submissionModal.isCorrect ? 'Submission Accepted! 🎉' : 'Submission Recorded: Revision Needed'}
            </h3>

            <p style={{ color: '#cbd5e1', fontSize: '0.85rem', lineHeight: 1.5, marginBottom: '1.3rem' }}>
              {submissionModal.isCorrect
                ? `Excellent job! Your code for "${submissionModal.problemTitle}" satisfies all invariant checks. Problem is now marked Solved!`
                : `Your solution was recorded in your attempt telemetry. The AI Mentor has analyzed your code and provided guidance below.`
              }
            </p>

            {/* Dashboard Telemetry Cards */}
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: '0.85rem',
                background: '#090d1a',
                padding: '1rem',
                borderRadius: '8px',
                border: '1px solid var(--border-color)',
                marginBottom: '1.4rem',
                textAlign: 'left',
              }}
            >
              <div>
                <span style={{ fontSize: '0.72rem', color: '#94a3b8', textTransform: 'uppercase', fontWeight: 600 }}>
                  {submissionModal.topic} Progress
                </span>
                <div
                  style={{
                    fontSize: '1.15rem',
                    fontWeight: 700,
                    color: submissionModal.isCorrect ? '#34d399' : '#f8fafc',
                    marginTop: '0.2rem',
                  }}
                >
                  {submissionModal.topicSolved} solved / {submissionModal.topicAttempts} attempts
                </div>
              </div>

              <div>
                <span style={{ fontSize: '0.72rem', color: '#94a3b8', textTransform: 'uppercase', fontWeight: 600 }}>
                  Overall Solved Total
                </span>
                <div style={{ fontSize: '1.15rem', fontWeight: 700, color: '#818cf8', marginTop: '0.2rem' }}>
                  {submissionModal.totalSolved} Solved
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center' }}>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setSubmissionModal(null)}
              >
                Close & Review
              </button>

              {submissionModal.isCorrect && onSelectNextProblem && (
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => {
                    setSubmissionModal(null);
                    onSelectNextProblem();
                  }}
                >
                  <span>Next Challenge</span>
                  <ArrowRight size={14} />
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
