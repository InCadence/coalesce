import React from 'react';
import TemplateWorkspace from './TemplateWorkspace.js';

import './App.css';

class App extends React.Component {

  render() {
    return (
      <div className="App">
        <TemplateWorkspace {...props}/>
      </div>
    );
  }
}

export default App;
