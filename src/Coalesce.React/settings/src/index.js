import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {registerLoader, registerErrorPrompt, registerPrompt} from 'common-components/lib/register.js'
import {Menu} from 'common-components/lib/menu.js'
import {SettingsView} from './settings.js'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = 'http://' + window.location.hostname + ':' + window.location.port;
}

registerLoader(Popup);
registerErrorPrompt(Popup);
registerPrompt(Popup);

function saveCallback(data) {

  Popup.plugins().loader('Saving...');

  fetch(rootUrl + '/cxf/data/property', {
    method: "POST",
    body: JSON.stringify(data),
    headers: new Headers({
      'content-type': 'application/json; charset=utf-8'
    }),
  }).then(res => {
      Popup.close();
  }).catch(function(error) {
      Popup.plugins().promptError("Saving: " + error);
  });
}

function loadSettings() {
  fetch(rootUrl + '/cxf/data/property')
    .then(res => res.json())
    .then(data => {
      ReactDOM.render(
        <SettingsView settings={data} saveCallback={saveCallback} />,
        document.getElementById('main')
      );
  }).catch(function(error) {
    Popup.plugins().promptError("Loading Settings: " + error);
  });
}

// Default Component
ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

ReactDOM.render(
    <Menu items={[/* No Options */]}/>,
    document.getElementById('myNavbar')
);

loadSettings();
