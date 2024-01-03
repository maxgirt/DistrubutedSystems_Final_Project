import React, {useEffect, useState} from 'react';
import axios from 'axios';
import './App.css';
import 'codemirror/theme/material.css';
import CodeMirror from '@uiw/react-codemirror';
import 'codemirror/theme/material.css';
import { python } from '@codemirror/lang-python';
import { java } from '@codemirror/lang-java';
import { cpp } from '@codemirror/lang-cpp';
import './SubmissionResults.css';
const SubmissionResults = ({ results }) => {
  return (
      <div className="results-container">
        {results.map((result, index) => (
            <div key={index} className={`result-item ${result.flag}`}>
              <p>Test Case {index + 1}: {result.flag === 'corect' ? 'Passed' : 'Failed'}</p>
              <p>Input: {result.input}</p>
              <p>Expected Output: {result.output}</p>
              {result.flag !== 'corect' && (
                  <p>Output: {result.expectedOutput}</p>
              )}
            </div>
        ))}
      </div>
  );
};


function App() {
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('python');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [problemId, setProblemId] = useState(1);
  const [results, setResults] = useState([])

  const handleChangeLanguage = (event) => {
    setLanguage(event.target.value);
  };

  const handleProblemIdChange = (event) => {
    setProblemId(event.target.value);
  };

  const getMode = () => {
    switch (language) {
      case 'python': return python();
      case 'c++': return cpp();
      case 'java': return java();
      default: return python();
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    console.log(code, "sent to broker")
    const payload = {
      idProblem: problemId,
      code: code,
      progLanguage: language === 'python' ? 0 : language === 'java' ? 1 : 2 // Assuming 0: Python, 1: Java, 2: C++
    };

    try {
      let response;
      response = await axios.post('http://localhost:8080/submission', payload);
      // Handle the response as needed
      console.log(response)
      setResults(response.data.results)

    } catch (error) {
      console.error('Submission error:', error);
    }
    setIsSubmitting(false);
  };

  const [problemDescription, setProblemDescription] = useState('');

  useEffect(() => {
    const fetchProblemDescription = async () => {
      try {
        const response = await axios.get(`/problems/${problemId}`);
        setProblemDescription(response.data.description);
      } catch (error) {
        console.error('Error fetching problem description:', error);
        setProblemDescription('Error fetching problem description.');
      }
    };

    if (problemId) {
      fetchProblemDescription();
    }
  }, [problemId]);


  const onChange = React.useCallback((val, viewUpdate) => {
    console.log('val:', val);
    setCode(val);
  }, []);

  return (
      <div className="App">
        <header className="App-header">
          <h1>COM41720 - Final Project - Simple Code Judge</h1>
          <p>Max, Natael, Dorian, Colman, Paul</p>
        </header>
        <div className="content">
          <div className="problem-section">
            <select value={problemId} onChange={handleProblemIdChange}>
              {/* Populate with actual problem IDs */}
              <option value="1">Problem 1</option>
              <option value="2">Problem 2</option>
              {/* Add more options as needed */}
            </select>
            <h2>Problem Name</h2>
            <p>Description of the problem...</p>
            <SubmissionResults results={results} />
          </div>
          <div className="editor-section">
            <select value={language} onChange={handleChangeLanguage}>
              <option value="python">Python</option>
              <option value="c++">C++</option>
              <option value="java">Java</option>
            </select>
            <CodeMirror
                value={code}
                height="400px"
                theme="light"
                extensions={[getMode()]}
                onChange={(value, viewUpdate) => {
                  setCode(value);
                }}
                />
            <div className="buttons">
              <button onClick={handleSubmit} disabled={isSubmitting}>Submit Code</button>
              <button>Get AI Assistance</button>
            </div>
          </div>
        </div>
      </div>
  );
}

export default App;



