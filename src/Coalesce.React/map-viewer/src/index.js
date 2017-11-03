import * as React from "react";
import * as ReactDOM from "react-dom";
import {Menu} from 'common-components/lib/menu.js'
import Popup from 'react-popup';
import {MapView} from './map.js'

import './index.css'

ReactDOM.render(
  React.createElement(MapView, {
    geoserver: 'http://bdpgeoserver.bdpdev.incadencecorp.com:8181/geoserver',
    workspace: 'OE_Repository'
  }),
  document.getElementById('main')
);

ReactDOM.render(
  <Popup />,
  document.getElementById('popupContainer')
);

ReactDOM.render(
  <Menu items={[/*No Menu Items*/]}/>,
  document.getElementById('myNavbar')
);
