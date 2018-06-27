import * as React from "react";
import * as ol from 'openlayers';
import MapMaker from '../../Map.js';
import Dialog from 'material-ui/Dialog';

// Map Controls
import 'openlayers/css/ol.css';

export class MapView extends React.Component {

  componentDidMount() {
    this.props.configureMap()
    //new MapMaker(null, null, null, 'map').getMap();

  }

  render() {
    return (
      <div id={'map' + this.props.uniqueID} ></div>
    )
  }

}
