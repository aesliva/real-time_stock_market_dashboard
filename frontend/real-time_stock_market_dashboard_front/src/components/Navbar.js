import React from 'react';
import { NavLink } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <ul>
        <li><NavLink to="/broad-market" activeClassName="active">Broad Market</NavLink></li>
        <li><NavLink to="/sector-analysis" activeClassName="active">Sector Analysis</NavLink></li>
        <li><NavLink to="/international-markets" activeClassName="active">International Markets</NavLink></li>
        <li><NavLink to="/stock-analysis" activeClassName="active">Stock Analysis</NavLink></li>
        <li><NavLink to="/glossary" activeClassName="active">Glossary</NavLink></li>
      </ul>
    </nav>
  );
};

export default Navbar;
