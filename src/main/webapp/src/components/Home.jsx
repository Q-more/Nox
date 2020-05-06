import React from 'react';

import logo from "../logo.svg";

import './Home.css';

import {Link} from 'react-router-dom';

class HomePage extends React.Component {

  render() {
    return (
      <div className={"Home Home-header"}>
        <img src={logo} className="App-logo" alt="logo"/>
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>

        <Link to={"/login"}>
          <h1>Login</h1>
        </Link>
      </div>
    )
  }
}

export {HomePage};