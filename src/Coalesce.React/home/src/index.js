import React from "react";
import ReactDOM from "react-dom";

import { createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles';

import { loadJSON } from 'coalesce-components/lib/js/propertyController';
import App from './App'

import 'coalesce-components/css/coalesce.css'

function loadApplication(theme) {
  loadJSON("home").then((data) => {
    ReactDOM.render(
      <MuiThemeProvider theme={createMuiTheme(theme)}>
        <App settings={data} />
      </MuiThemeProvider>,
      document.getElementById('main')
    );
  }).catch(err => {
    console.log(`Loading layout ${err}`);
  });
}

loadJSON('theme').then((theme) => {
  loadApplication(createMuiTheme(theme));
}).catch((err) => {
  console.log(`Loading theme ${err}`);
  loadApplication(createMuiTheme({}));
})
