import React from 'react';
import { HashRouter as Router, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import BroadMarket from './components/BroadMarket';
import SectorAnalysis from './components/SectorAnalysis';
import StockAnalysis from './components/StockAnalysis';

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <Routes>
          <Route path="/broad-market" element={<BroadMarket />} />
          <Route path="/sector-analysis" element={<SectorAnalysis />} />
          <Route path="/stock-analysis" element={<StockAnalysis />} />
          <Route path="/" element={<BroadMarket />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
