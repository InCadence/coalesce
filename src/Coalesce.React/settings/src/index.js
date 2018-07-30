import React from "react";
import ReactDOM from "react-dom";
import App from './app'

import { createMuiTheme } from '@material-ui/core/styles'
import { loadJSON, loadAllProperties } from 'common-components/lib/js/propertyController'

var pjson = require('../package.json');
document.title = pjson.title;

function loadApplication(theme) {
  loadAllProperties().then((data) => {
  ReactDOM.render(
    <App data={data} icon={pjson.icon} title={pjson.title} theme={createMuiTheme(theme)}/>,
    document.getElementById('main')
  );
})
}

loadJSON('theme').then((theme) => {
    loadApplication(createMuiTheme(theme));
}).catch((err) => {
  console.log("Loading Theme: " + err);
  loadApplication(createMuiTheme({}));
})
