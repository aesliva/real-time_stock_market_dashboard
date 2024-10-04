import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './SectorAnalysis.css';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, TimeScale } from 'chart.js';
import 'chartjs-adapter-date-fns';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, TimeScale);

const SectorAnalysis = () => {
  const [sectors, setSectors] = useState([]);
  const [view, setView] = useState('table');
  const [selectedSector, setSelectedSector] = useState('');
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    const fetchSectors = async () => {
      try {
        const response = await axios.get('https://0tnr4jx1b5.execute-api.us-west-1.amazonaws.com/prod/sectors');
        setSectors(response.data);
        if (response.data.length > 0 && !selectedSector) {
          setSelectedSector(response.data[0].symbol);
        }
      } catch (error) {
        console.error('Error fetching sector data:', error);
      }
    };

    fetchSectors();
    const intervalId = setInterval(fetchSectors, 300000);
    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    const fetchChartData = async () => {
      if (!selectedSector) return;

      try {
        const response = await axios.get(`https://0tnr4jx1b5.execute-api.us-west-1.amazonaws.com/prod/etf-data/${selectedSector}`);
        const data = response.data;
        setChartData({
          labels: data.map(item => item.date),
          datasets: [{
            label: selectedSector,
            data: data.map(item => item.close),
            borderColor: 'rgb(7, 62, 126)',
            tension: 0.1
          }]
        });
      } catch (error) {
        console.error('Error fetching ETF data:', error);
      }
    };

    fetchChartData();
  }, [selectedSector]);

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
        text: `${selectedSector} Price Chart`,
      },
      tooltip: {
        callbacks: {
          title: function(tooltipItems) {
            // Format the price as currency and make it the title
            return new Intl.NumberFormat('en-US', { 
              style: 'currency', 
              currency: 'USD',
              minimumFractionDigits: 2,
              maximumFractionDigits: 2
            }).format(tooltipItems[0].parsed.y);
          },
          label: function(context) {
            // Format the date to show only YYYY-MM-DD
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
    <div className="sector-analysis">
      <h1>Sector Analysis</h1>
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
                <th>Sector</th>
                <th>Price</th>
                <th>Change</th>
                <th>Change %</th>
              </tr>
            </thead>
            <tbody>
              {sectors.map((sector) => (
                <tr key={sector.symbol}>
                  <td>{sector.symbol}</td>
                  <td>{sector.name}</td>
                  <td>${sector.price.toFixed(2)}</td>
                  <td className={sector.change >= 0 ? 'positive' : 'negative'}>
                    {sector.change >= 0 ? '+' : ''}{sector.change.toFixed(2)}
                  </td>
                  <td className={sector.changePercent >= 0 ? 'positive' : 'negative'}>
                    {sector.changePercent >= 0 ? '+' : ''}{sector.changePercent.toFixed(2)}%
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="chart-container">
          <div className="dropdown-container">
            <select value={selectedSector} onChange={(e) => setSelectedSector(e.target.value)}>
              {sectors.map((sector) => (
                <option key={sector.symbol} value={sector.symbol}>{sector.name}</option>
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

export default SectorAnalysis;