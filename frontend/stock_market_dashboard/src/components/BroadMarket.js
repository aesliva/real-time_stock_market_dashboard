import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './BroadMarket.css';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, TimeScale } from 'chart.js';
import 'chartjs-adapter-date-fns';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, TimeScale);

const BroadMarket = () => {
  const [indexes, setIndexes] = useState([]);
  const [view, setView] = useState('table');
  const [selectedETF, setSelectedETF] = useState('SPY');
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    const fetchIndexes = async () => {
      try {
        const response = await axios.get('https://your-backend-url.com/indexes');
        setIndexes(response.data);
      } catch (error) {
        console.error('Error fetching index data:', error);
      }
    };

    fetchIndexes();
    const intervalId = setInterval(fetchIndexes, 300000);
    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    const fetchChartData = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/etf-data/${selectedETF}`);
        const data = response.data;
        setChartData({
          labels: data.map(item => item.date),
          datasets: [{
            label: selectedETF,
            data: data.map(item => item.close),
            borderColor: 'rgb(7, 62, 126)',
            tension: 0.1
          }]
        });
      } catch (error) {
        console.error('Error fetching ETF data:', error);
      }
    };

    if (view === 'chart') {
      fetchChartData();
    }
  }, [selectedETF, view]);

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      title: {
        font: {
          size: 14,
        },
        display: true,
        text: `${selectedETF} Price Chart`,
      },
      tooltip: {
        callbacks: {
          title: function(tooltipItems) {
            return new Intl.NumberFormat('en-US', { 
              style: 'currency', 
              currency: 'USD',
              minimumFractionDigits: 2,
              maximumFractionDigits: 2
            }).format(tooltipItems[0].parsed.y);
          },
          label: function(context) {
            return new Date(context.parsed.x).toISOString().split('T')[0];
          }
        },
        titleFont: {
          size: 14,
          weight: 'bold',
        },
        bodyFont: {
          size: 12,
        },
        padding: 10,
        displayColors: false
      }
    },
    scales: {
      x: {
        type: 'time',
        time: {
          unit: 'day',
        },
      },
      y: {
        beginAtZero: false,
      },
    },
  };

  return (
    <div className="broad-market">
      <h1>Broad Market</h1>
      <div className="view-options">
        <button onClick={() => setView('table')} className={view === 'table' ? 'active' : ''}>Table View</button>
        <button onClick={() => setView('chart')} className={view === 'chart' ? 'active' : ''}>Chart View</button>
      </div>
      {view === 'table' ? (
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
      ) : (
        <div className="chart-container">
          <div className="dropdown-container">
            <select value={selectedETF} onChange={(e) => setSelectedETF(e.target.value)}>
              {indexes.map((index) => (
                <option key={index.symbol} value={index.symbol}>{index.symbol}</option>
              ))}
            </select>
          </div>
          {chartData && (
            <div style={{ width: '100%', height: '400px' }}>
              <Line data={chartData} options={chartOptions} />
            </div>
          )}
        </div>
      )}
    </div>
  );
};


export default BroadMarket;