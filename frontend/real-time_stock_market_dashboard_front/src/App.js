import React from 'react';
import './App.css';
import ETFTable from './components/ETFTable';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>ETF Dashboard</h1>
      </header>
      <main>
        <ETFTable />
      </main>
    </div>
  );
}

export default App;
