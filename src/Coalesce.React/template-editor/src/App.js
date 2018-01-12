import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import TemplateWorkspace from './TemplateWorkspace.js';
import TemplateMenu from './TemplateMenu.js';
import TemplateOutline from './TemplateOutline.js';
import TemplateNavBar from './TemplateNavBar.js';
import { Panel, Grid, Row, Col } from 'react-bootstrap';

class App extends Component {

  render() {
    const names = ["template1", "test2"];

    return (
      <div className="App">
        <TemplateWorkspace />
      </div>
    );
  }
}

export default App;
