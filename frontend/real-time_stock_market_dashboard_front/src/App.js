import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import BroadMarket from './components/BroadMarket';
import SectorAnalysis from './components/SectorAnalysis';
import InternationalMarkets from './components/InternationalMarkets';
import StockAnalysis from './components/StockAnalysis';
import Glossary from './components/Glossary';

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <Routes>
          <Route path="/broad-market" element={<BroadMarket />} />
          <Route path="/sector-analysis" element={<SectorAnalysis />} />
          <Route path="/international-markets" element={<InternationalMarkets />} />
          <Route path="/stock-analysis" element={<StockAnalysis />} />
          <Route path="/glossary" element={<Glossary />} />
          <Route path="/" element={<BroadMarket />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
