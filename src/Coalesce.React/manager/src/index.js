import React from "react";
import ReactDOM from "react-dom";
import {App} from './App.js'

import { createMuiTheme } from '@material-ui/core/styles'
import { loadJSON } from 'common-components/lib/js/propertyController'

import "react-table/react-table.css";
import 'common-components/bootstrap/css/bootstrap.min.css'
import 'common-components/css/coalesce.css'

var pjson = require('../package.json');
document.title = pjson.title;

function loadApplication(theme) {
  ReactDOM.render(
    <App icon={pjson.icon} title={pjson.title} theme={theme}/>,
    document.getElementById('main')
  );
}

loadJSON('theme').then((theme) => {
  loadApplication(createMuiTheme(theme));
}).catch((err) => {
  console.log("Loading Theme: " + err);
  loadApplication(createMuiTheme({}));
})
