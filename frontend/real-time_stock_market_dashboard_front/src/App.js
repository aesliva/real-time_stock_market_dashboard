import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import Navbar from './components/Navbar';
import BroadMarket from './components/BroadMarket';
import SectorAnalysis from './components/SectorAnalysis';
import InternationalMarkets from './components/InternationalMarkets';
import StockAnalysis from './components/StockAnalysis';
import Glossary from './components/Glossary';

function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Navbar />
        <Switch>
          <Route path="/broad-market" component={BroadMarket} />
          <Route path="/sector-analysis" component={SectorAnalysis} />
          <Route path="/international-markets" component={InternationalMarkets} />
          <Route path="/stock-analysis" component={StockAnalysis} />
          <Route path="/glossary" component={Glossary} />
          <Route exact path="/" component={BroadMarket} />
        </Switch>
      </div>
    </BrowserRouter>
  );
}

export default App;
