import React, { useState, useEffect } from 'react';
import CodeEditor from '../components/CodeEditor';
import RunOutput from '../components/RunOutput';
import MentorPanel from '../components/MentorPanel';
import { api } from '../api/client';

export default function Workspace({ currentProblem, onSelectNextProblem }) {
  // Empty workspace by default as requested: student writes code from scratch
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState(currentProblem?.language || 'java');
  const [stdin, setStdin] = useState('');

  // Code runner state
  const [isRunning, setIsRunning] = useState(false);
  const [runResult, setRunResult] = useState(null);

  // AI analysis state
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysis, setAnalysis] = useState(null);

  // Progressive hints state (1-4)
  const [currentLevel, setCurrentLevel] = useState(1);
  const [hintsByLevel, setHintsByLevel] = useState({});
  const [canAdvance, setCanAdvance] = useState(false);
  const [isAdvancingHint, setIsAdvancingHint] = useState(false);
  const [isRevealingSolution, setIsRevealingSolution] = useState(false);

  // When currentProblem changes, open a completely empty workspace for the student to write code
  useEffect(() => {
    if (currentProblem) {
      setCode(''); // Full empty workspace for the student to write their own solution
      setLanguage(currentProblem.language || 'java');
      // Reset hints & analysis for new problem
      setAnalysis(null);
      setRunResult(null);
      setCurrentLevel(1);
      setHintsByLevel({});
      setCanAdvance(false);
    }
  }, [currentProblem?.id]);

  // Handle Code Execution
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
          onAnalyze={handleAnalyzeCode}
          onReset={handleResetCode}
          isRunning={isRunning}
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
    </div>
  );
}
