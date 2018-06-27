import * as React from "react";
import * as ol from 'openlayers';
import Dialog from 'material-ui/Dialog';

// Map Controls
import 'openlayers/css/ol.css';

export default class MapView extends React.Component {

  componentDidMount() {
    this.props.configureMap()

  }

  render() {
    return (
      <div id={'map' + this.props.uniqueID} ></div>
    )
  }

}
