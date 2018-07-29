import React from 'react';
import ReactDOM from 'react-dom';
import TemplateWorkspace from './TemplateWorkspace';
//import registerServiceWorker from './registerServiceWorker';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles'
import { MuiThemeProvider as V0MuiThemeProvider} from 'material-ui';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import { loadJSON } from 'common-components/lib/js/propertyController'

import 'common-components/bootstrap/css/bootstrap.min.css'
import 'common-components/css/coalesce.css'
import './App.css';

var pjson = require('../package.json');
document.title = pjson.title;

function loadApplication(theme) {
  ReactDOM.render(
    <MuiThemeProvider theme={createMuiTheme(theme)}>
      <V0MuiThemeProvider muiTheme={getMuiTheme(theme)}>
        <TemplateWorkspace icon={pjson.icon} title={pjson.title} />,
      </V0MuiThemeProvider>
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
