import * as React from "react";
import Dialog from 'material-ui/Dialog';
import 'common-components/css/mapping.css'

// Map Controls
import 'ol/ol.css';

export default class MapView extends React.Component {

  constructor (props) {
    super(props)

  }

  componentDidMount() {
    this.props.configureMap(this.props.opt_options)
  }

  render() {
    return (
      <div id={'map' + this.props.uniqueID} ></div>
    )
  }

}
