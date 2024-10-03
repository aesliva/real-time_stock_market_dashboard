import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ETFTable = () => {
  const [etfs, setEtfs] = useState([]);

  useEffect(() => {
    const fetchETFs = async () => {
      try {
        const response = await axios.get('http://localhost:8080/indexes');
        setEtfs(response.data);
      } catch (error) {
        console.error('Error fetching ETF data:', error);
      }
    };

    fetchETFs();
  }, []);

  return (
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
        {etfs.map((etf) => (
          <tr key={etf.symbol}>
            <td>{etf.symbol}</td>
            <td>${etf.price.toFixed(2)}</td>
            <td>${etf.change.toFixed(2)}</td>
            <td>{etf.changePercent.toFixed(2)}%</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default ETFTable;