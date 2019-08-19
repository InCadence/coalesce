import * as React from "react";
import * as ReactDOM from "react-dom";
import { loadJSON } from 'coalesce-components/lib/js/propertyController'

import { App } from './app'

import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles'; // v1.x

import 'coalesce-components/bootstrap/css/bootstrap.min.css'
import 'coalesce-components/css/coalesce.css'

var pjson = require('../package.json');
document.title = pjson.title;

function loadEnumerations(theme) {

  ReactDOM.render(
    <MuiThemeProvider theme={createMuiTheme(theme)}>
      <App icon={pjson.icon} title={pjson.title} theme={theme} />,
    </MuiThemeProvider>,
    document.getElementById('myNavbar')
  );
}

loadJSON('theme').then((data) => {
  loadEnumerations(createMuiTheme(data));
}).catch((err) => {
  console.log(`Failed Loading Theme: ${err.message}`);
  loadEnumerations();
})
