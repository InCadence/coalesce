import * as React from "react";
import Dialog from 'material-ui/Dialog';
import 'common-components/css/mapping.css'
import Popup from './popup.js';

// Map Controls
import 'ol/ol.css';

export default class MapView extends React.Component {

  constructor (props) {
    super(props);
  }

  componentDidMount() {
    this.props.configureMap(this.props.opt_options)
  }

  render() {
    return (
      <div>
      <div id={'map' + this.props.uniqueID} ></div>
      {this.props.shape === 'POINT' || this.props.shape === 'MULTIPOINT' &&
        <Popup uniqueID={this.props.uniqueID} coords={this.props.coords}/>
      }

      </div>
    )
  }

}
