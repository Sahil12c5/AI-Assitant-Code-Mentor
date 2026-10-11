import React, { useState } from 'react';
import { 
  BookOpen, 
  CheckCircle2, 
  Sparkles, 
  Code2, 
  Search, 
  Filter, 
  ArrowRight,
  Shuffle
} from 'lucide-react';

export default function ProblemList({
  problems,
  topics,
  selectedTopic,
  onSelectTopic,
  selectedDifficulty,
  onSelectDifficulty,
  onSelectProblem,
  onGenerateSimilar,
  isGeneratingSimilar
}) {
  const [searchQuery, setSearchQuery] = useState('');
  const [generatingId, setGeneratingId] = useState(null);

  const filteredProblems = problems.filter((p) => {
    if (searchQuery) {
      const match = p.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    p.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    p.topic.toLowerCase().includes(searchQuery.toLowerCase());
      if (!match) return false;
    }
    return true;
  });

  const handleGenerate = async (problemId) => {
    setGeneratingId(problemId);
    try {
      await onGenerateSimilar(problemId);
    } finally {
      setGeneratingId(null);
    }
  };

  const getDifficultyBadge = (diff) => {
    switch (diff) {
      case 'BEGINNER':
      case 'EASY':
        return <span className="badge badge-success">{diff}</span>;
      case 'MEDIUM':
        return <span className="badge badge-warning">{diff}</span>;
      case 'HARD':
      case 'INTERVIEW':
        return <span className="badge badge-danger">{diff}</span>;
      default:
        return <span className="badge badge-secondary">{diff}</span>;
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
      {/* Header & Filter Controls */}
      <div className="card" style={{ padding: '1.25rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', marginBottom: '1rem' }}>
          <div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#fff' }}>
              Adaptive Problem Catalog ({problems.length})
            </h2>
            <p style={{ fontSize: '0.82rem', color: '#94a3b8' }}>
              Select an exercise to load into your Monaco workspace, or synthesize an isomorphic variation.
            </p>
          </div>

          {/* Search Box */}
          <div style={{ position: 'relative', width: '260px' }}>
            <Search size={14} style={{ position: 'absolute', left: '10px', top: '10px', color: '#64748b' }} />
            <input
              type="text"
              style={{
                width: '100%',
                background: 'var(--bg-input)',
                border: '1px solid var(--border-color)',
                borderRadius: '6px',
                padding: '0.45rem 0.75rem 0.45rem 2rem',
                color: '#fff',
                fontSize: '0.82rem',
                outline: 'none',
              }}
              placeholder="Search problems or concepts..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>

        {/* Filter Badges: Topics & Difficulty */}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', alignItems: 'center' }}>
          <span style={{ fontSize: '0.75rem', fontWeight: 600, color: '#94a3b8', marginRight: '0.25rem' }}>
            Topic:
          </span>
          <button
            className={`btn btn-sm ${!selectedTopic ? 'btn-primary' : 'btn-secondary'}`}
            style={{ fontSize: '0.72rem', padding: '0.2rem 0.5rem' }}
            onClick={() => onSelectTopic('')}
          >
            All
          </button>
          {topics.map((t) => (
            <button
              key={t}
              className={`btn btn-sm ${selectedTopic === t ? 'btn-primary' : 'btn-secondary'}`}
              style={{ fontSize: '0.72rem', padding: '0.2rem 0.5rem' }}
              onClick={() => onSelectTopic(t)}
            >
              {t}
            </button>
          ))}
        </div>
      </div>

      {/* Problems Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '1rem' }}>
        {filteredProblems.map((problem) => (
          <div
            key={problem.id}
            className="card"
            style={{
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              padding: '1.25rem',
              transition: 'transform 0.2s ease, border-color 0.2s ease',
            }}
          >
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                <div style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap' }}>
                  {getDifficultyBadge(problem.difficulty)}
                  <span className="badge badge-secondary">{problem.topic}</span>
                  {problem.isGenerated && (
                    <span className="badge badge-accent" style={{ display: 'inline-flex', gap: '0.25rem' }}>
                      <Shuffle size={10} /> Isomorphic
                    </span>
                  )}
                </div>

                {problem.isSolved && (
                  <span className="badge badge-success" style={{ display: 'inline-flex', gap: '0.25rem' }}>
                    <CheckCircle2 size={11} /> Solved
                  </span>
                )}
              </div>

              <h3 style={{ fontSize: '1rem', fontWeight: 700, color: '#fff', marginBottom: '0.4rem' }}>
                {problem.title}
              </h3>

              <p style={{ color: '#94a3b8', fontSize: '0.8rem', lineHeight: 1.4, marginBottom: '1rem', maxHeight: '60px', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {problem.description}
              </p>
            </div>

            <div style={{ display: 'flex', gap: '0.5rem', paddingTop: '0.75rem', borderTop: '1px solid rgba(148, 163, 184, 0.08)' }}>
              <button
                className="btn btn-primary btn-sm"
                style={{ flex: 1 }}
                onClick={() => onSelectProblem(problem)}
              >
                <Code2 size={13} />
                <span>Open in Workspace</span>
              </button>

              <button
                className="btn btn-secondary btn-sm"
                title="Synthesize an isomorphic 'Similar but Different' problem targeting the same concept"
                onClick={() => handleGenerate(problem.id)}
                disabled={generatingId === problem.id}
              >
                <Shuffle size={13} style={{ color: '#f59e0b' }} />
                <span>{generatingId === problem.id ? 'Generating...' : 'Similar'}</span>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
