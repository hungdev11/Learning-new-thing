import React, {useState, useEffect} from 'react'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'

export default function App(){
  const [apiKey, setApiKey] = useState('')
  const [topic, setTopic] = useState('Docker')
  const [language, setLanguage] = useState('việt')
  const [level, setLevel] = useState('BEGINNER')
  const [markdown, setMarkdown] = useState('')
  const [loading, setLoading] = useState(false)
  const [wordCount, setWordCount] = useState(0)
  const [mode, setMode] = useState('default')
  const [llmUsed, setLlmUsed] = useState(false)
  const [llmProvider, setLlmProvider] = useState(null)
  const [error, setError] = useState(null)

  const backendUrl = 'https://learning-new-thing.onrender.com';

  useEffect(() => {
    const savedKey = localStorage.getItem('gemini_api_key')
    if (savedKey) {
      setApiKey(savedKey)
    }
  }, [])

  function handleApiKeyChange(e) {
    const newKey = e.target.value
    setApiKey(newKey)
    localStorage.setItem('gemini_api_key', newKey)
  }

  async function handleGenerate(){
    setLoading(true)
    setError(null)
    const res = await fetch(`${backendUrl}/api/generate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ apiKey, topic, language, level })
    })
    if (!res.ok) {
      const text = await res.text()
      setError(`Generation failed: ${res.status} ${text}`)
      setLoading(false)
      setMarkdown('')
      return
    }
    const data = await res.json()
    if (data.errorMessage) {
      setError(data.errorMessage)
      setMarkdown('')
    } else {
      setMarkdown(data.markdown)
      setWordCount(data.markdown ? data.markdown.split(/\s+/).length : 0)
    }
    setLlmUsed(data.usedLlm || false)
    setLlmProvider(data.provider || null)
    setLoading(false)
  }

  async function handleExport(type){
    const endpoint = type === 'docx' ? `${backendUrl}/api/export/docx` : ''
    if (!endpoint) return;

    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ markdown })
    })
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = type === 'pdf' ? 'guide.pdf' : 'guide.docx'
    a.click()
    URL.revokeObjectURL(url)
  }

  return (
    <div className="container">
      <header className="app-header">
        <h1>Learning Guide Generator</h1>
        <p>Your personal AI-powered learning curriculum designer.</p>
      </header>

      <div className="layout">
        <aside className="left">
          <div className="form">
            <div className="form-group">
              <label>Gemini API Key</label>
              <input value={apiKey} onChange={handleApiKeyChange} placeholder="Enter and we will save for next time" />
            </div>

            <div className="form-group">
              <label>Topic</label>
              <input value={topic} onChange={e=>setTopic(e.target.value)} />
            </div>

            <div className="form-group">
              <label>Language</label>
              <select value={language} onChange={e=>setLanguage(e.target.value)}>
                <option value="việt">Tiếng Việt</option>
                <option value="english">English</option>
                <option value="mix">Mix</option>
              </select>
            </div>

            <div className="form-group">
              <label>Your Level</label>
              <select value={level} onChange={e=>setLevel(e.target.value)}>
                <option>BEGINNER</option>
                <option>INTERMEDIATE</option>
                <option>ADVANCED</option>
              </select>
            </div>

            <div className="actions">
              <button onClick={handleGenerate} disabled={loading}>{loading? 'Generating...' : 'Generate Guide'}</button>
            </div>
          </div>
        </aside>

        <main className="right">
          <div className="preview">
            <div className="preview-header">
              <h2>Preview</h2>
              <div className="export-actions">
                <button onClick={()=>handleExport('docx')} disabled={!markdown}>Export DOCX</button>
              </div>
            </div>

            {error && <div className="error-box">Error: {error}</div>}
            <div style={{marginBottom:8}}>Words: {wordCount} {llmUsed ? `(Using AI: ${llmProvider||'unknown'})` : ''}</div>
            {loading ? (
              <div className="loading-overlay">
                <div className="spinner"></div>
                <p>Generating your guide... this may take a moment.</p>
              </div>
            ) : (
              <div className="markdown-area">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{markdown}</ReactMarkdown>
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  )
}
