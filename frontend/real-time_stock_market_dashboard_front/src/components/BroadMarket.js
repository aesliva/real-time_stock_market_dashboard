import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './BroadMarket.css';

const BroadMarket = () => {
  const [indexes, setIndexes] = useState([]);

  useEffect(() => {
    const fetchIndexes = async () => {
      try {
        const response = await axios.get('http://localhost:8080/indexes');
        setIndexes(response.data);
      } catch (error) {
        console.error('Error fetching index data:', error);
      }
    };

    fetchIndexes();
    const intervalId = setInterval(fetchIndexes, 300000);
    return () => clearInterval(intervalId);
  }, []);

  return (
    <div className="broad-market">
      <h1>Broad Market</h1>
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Symbol</th>
              <th>Price</th>
              <th>Change</th>
              <th>Change %</th>
            </tr>
          </thead>
          <tbody>
            {indexes.map((index) => (
              <tr key={index.symbol}>
                <td>{index.symbol}</td>
                <td>${index.price.toFixed(2)}</td>
                <td className={index.change >= 0 ? 'positive' : 'negative'}>
                  {index.change >= 0 ? '+' : ''}{index.change.toFixed(2)}
                </td>
                <td className={index.changePercent >= 0 ? 'positive' : 'negative'}>
                  {index.changePercent >= 0 ? '+' : ''}{index.changePercent.toFixed(2)}%
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default BroadMarket;