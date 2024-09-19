import React, { useState } from 'react';
import axios from 'axios';
import './StockAnalysis.css';

const StockAnalysis = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [stockData, setStockData] = useState(null);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    setError('');
    setStockData(null);

    try {
      const response = await axios.get(`http://localhost:8080/stock/${searchTerm}`);
      setStockData(response.data);
    } catch (error) {
      setError('Error fetching stock data. Please try again.');
      console.error('Error fetching stock data:', error);
    }
  };

  return (
    <div className="stock-analysis">
      <h1>Stock Analysis</h1>
      <form onSubmit={handleSearch}>
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Enter stock symbol (e.g., AAPL)"
        />
        <button type="submit">Search</button>
      </form>
      {error && <p className="error">{error}</p>}
      {stockData && (
        <div className="stock-info">
          <h2>{stockData.name} ({stockData.symbol})</h2>
          <p><strong>Price:</strong> ${stockData.price}</p>
          <p><strong>Change:</strong> ${stockData.change} ({parseFloat(stockData.changePercent).toFixed(2)}%)</p>
          <p><strong>Exchange:</strong> {stockData.exchange}</p>
          <p><strong>Sector:</strong> {stockData.sector}</p>
          <p><strong>Industry:</strong> {stockData.industry}</p>
          <p><strong>Market Cap:</strong> {stockData.marketCap}</p>
          <p><strong>P/E Ratio:</strong> {stockData.peRatio}</p>
          <p><strong>EPS:</strong> ${stockData.eps}</p>
          <p><strong>Dividend Yield:</strong> {stockData.dividendYield}</p>
          <p><strong>52 Week High:</strong> ${stockData['52WeekHigh']}</p>
          <p><strong>52 Week Low:</strong> ${stockData['52WeekLow']}</p>
          <h3>Description</h3>
          <p>{stockData.description}</p>
        </div>
      )}
    </div>
  );
};

export default StockAnalysis;
