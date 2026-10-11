import React, { useState, useEffect } from 'react';
import ProblemList from '../components/ProblemList';
import { api } from '../api/client';
import { RefreshCw } from 'lucide-react';

export default function Practice({ onSelectProblem }) {
  const [problems, setProblems] = useState([]);
  const [topics, setTopics] = useState([]);
  const [selectedTopic, setSelectedTopic] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProblems();
  }, [selectedTopic, selectedDifficulty]);

  const loadProblems = async () => {
    setLoading(true);
    try {
      const res = await api.getProblems(selectedTopic, selectedDifficulty);
      setProblems(res.problems || []);
      if (res.topics && res.topics.length > 0) {
        setTopics(res.topics);
      }
    } catch (err) {
      console.error('Failed to load problems', err);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateSimilar = async (sourceProblemId) => {
    try {
      const res = await api.generateSimilarProblem(sourceProblemId);
      if (res && res.problem) {
        // Reload list and highlight new problem
        await loadProblems();
        alert(`Successfully generated isomorphic problem: "${res.problem.title}"!`);
      }
    } catch (err) {
      alert('Error generating problem: ' + err.message);
    }
  };

  if (loading && problems.length === 0) {
    return (
      <div className="page-view" style={{ textAlign: 'center', padding: '4rem 0' }}>
        <RefreshCw size={32} className="animate-spin" style={{ color: '#818cf8', animation: 'spin 1s linear infinite', margin: '0 auto 1rem' }} />
        <p style={{ color: '#94a3b8' }}>Loading Practice Problem Catalog...</p>
      </div>
    );
  }

  return (
    <div className="page-view">
      <ProblemList
        problems={problems}
        topics={topics}
        selectedTopic={selectedTopic}
        onSelectTopic={setSelectedTopic}
        selectedDifficulty={selectedDifficulty}
        onSelectDifficulty={setSelectedDifficulty}
        onSelectProblem={onSelectProblem}
        onGenerateSimilar={handleGenerateSimilar}
      />
    </div>
  );
}
