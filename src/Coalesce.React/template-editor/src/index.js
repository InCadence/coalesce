import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
//import registerServiceWorker from './registerServiceWorker';
import { createMuiTheme } from '@material-ui/core/styles'
import { loadJSON } from 'common-components/lib/js/propertyController'

import 'common-components/bootstrap/css/bootstrap.min.css'
import 'common-components/css/coalesce.css'
import './index.css';

var pjson = require('../package.json');
document.title = pjson.title;

loadJSON('theme').then((theme) => {
  ReactDOM.render(
    <App icon={pjson.icon} title={pjson.title} theme={createMuiTheme(theme)}/>,
    document.getElementById('main')
  );
}).catch((err) => {
  console.log("Loading Theme: " + err);
})

//registerServiceWorker();
