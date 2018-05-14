import * as React from "react";
import * as ReactDOM from "react-dom";
import {App} from './App.js'

import "react-table/react-table.css";
import 'common-components/bootstrap/css/bootstrap.min.css'
import 'common-components/css/coalesce.css'

var pjson = require('../package.json');
document.title = pjson.title;

ReactDOM.render(
  <App icon={pjson.icon} title={pjson.title}/>,
  document.getElementById('main')
);
