/**
 * Adaptive AI Coding Mentor — API Client
 * Wraps fetch with credentials: 'include' and automatic context path detection.
 */

function getApiBaseUrl() {
  const path = window.location.pathname;
  const appIndex = path.indexOf('/app');
  if (appIndex > 0) {
    return path.substring(0, appIndex) + '/api';
  }
  return '/api';
}

const API_BASE = getApiBaseUrl();

async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint.startsWith('/') ? endpoint : '/' + endpoint}`;
  const config = {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  try {
    const res = await fetch(url, config);
    const contentType = res.headers.get('content-type');
    const isJson = contentType && contentType.includes('application/json');
    const data = isJson ? await res.json() : await res.text();

    if (!res.ok) {
      const errorMsg = (data && data.error) ? data.error : `HTTP ${res.status}: ${res.statusText}`;
      const error = new Error(errorMsg);
      error.status = res.status;
      error.data = data;
      throw error;
    }

    return data;
  } catch (err) {
    if (err.status === 401) {
      // Redirect to login if unauthenticated
      const contextPrefix = API_BASE.replace('/api', '');
      window.location.href = `${contextPrefix}/login`;
    }
    throw err;
  }
}

export const api = {
  // Session
  getMe: () => request('/me'),

  // Code Execution
  runCode: (code, language = 'java', input = '') =>
    request('/run', {
      method: 'POST',
      body: JSON.stringify({ code, language, input }),
    }),

  // Official Graded Code Submission
  submitCode: (code, language = 'java', problemId = null, input = '') =>
    request('/submit', {
      method: 'POST',
      body: JSON.stringify({ code, language, problemId, input }),
    }),

  // AI Analysis (Level 1)
  analyzeCode: (code, language, problemId) =>
    request('/analyze', {
      method: 'POST',
      body: JSON.stringify({ code, language, problemId }),
    }),

  // Progressive Hint (Level 2 or 3)
  getNextHint: (hintSessionId) =>
    request('/hint', {
      method: 'POST',
      body: JSON.stringify({ hintSessionId }),
    }),

  // Solution Reveal (Level 4)
  revealSolution: (hintSessionId) =>
    request('/solution', {
      method: 'POST',
      body: JSON.stringify({ hintSessionId }),
    }),

  // Problem Catalog & Adaptive Next
  getProblems: (topic, difficulty) => {
    let q = '';
    const params = [];
    if (topic) params.push(`topic=${encodeURIComponent(topic)}`);
    if (difficulty) params.push(`difficulty=${encodeURIComponent(difficulty)}`);
    if (params.length > 0) q = '?' + params.join('&');
    return request(`/problems${q}`);
  },

  getNextProblem: () => request('/problems/next'),

  generateSimilarProblem: (sourceProblemId) =>
    request('/problems/generate', {
      method: 'POST',
      body: JSON.stringify({ sourceProblemId }),
    }),

  // Student DNA Profile & Mistake Memory
  getProfile: () => request('/profile'),

  getMistakeTrends: () => request('/mistakes'),

  getSubmissions: (limit = 30) => request(`/submissions?limit=${limit}`),
};
