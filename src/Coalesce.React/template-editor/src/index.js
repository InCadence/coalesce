import React from 'react';
import ReactDOM from 'react-dom';
import TemplateWorkspace from './TemplateWorkspace';
//import registerServiceWorker from './registerServiceWorker';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles'
import { loadJSON } from 'coalesce-components/lib/js/propertyController'

import 'coalesce-components/bootstrap/css/bootstrap.min.css'
import 'coalesce-components/css/coalesce.css'
import './App.css';

var pjson = require('../package.json');
document.title = pjson.title;

function loadApplication(theme) {
  ReactDOM.render(
    <MuiThemeProvider theme={createMuiTheme(theme)}>
        <TemplateWorkspace icon={pjson.icon} title={pjson.title} />,
    </MuiThemeProvider>,
    document.getElementById('main')
  );
}

loadJSON('theme').then((theme) => {
  loadApplication(createMuiTheme(theme));
}).catch((err) => {
  console.log("Loading Theme: " + err);
  loadApplication(createMuiTheme({}));
})
