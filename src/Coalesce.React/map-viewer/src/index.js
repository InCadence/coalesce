import React from "react";
import ReactDOM from "react-dom";
import App from './app'
import Popup from 'react-popup';

import { createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles'
import { loadJSON, loadProperty } from 'common-components/lib/js/propertyController'

var pjson = require('../package.json');
document.title = pjson.title;

function loadApplication(theme) {

  loadProperty('geoserver.url')
    .then((data) => {

      ReactDOM.render(
        <MuiThemeProvider theme={theme}>
          <App
            geoserver={data}
            icon={pjson.icon}
            title={pjson.title}
            theme={theme}
          />
        </MuiThemeProvider>,
        document.getElementById('main')
      );
  }).catch(function(error) {
    ReactDOM.render(
      <App error={`(FAILED) Retrieving Geo Server URL: ${error.message}`} />,
      document.getElementById('main')
    );
  });
}

loadJSON('theme').then((theme) => {
  loadApplication(createMuiTheme(theme))
}).catch((err) => {
  console.log(`Failed Loading Theme`);
  loadApplication()
})

ReactDOM.render(
  <Popup />,
  document.getElementById('popupContainer')
);
