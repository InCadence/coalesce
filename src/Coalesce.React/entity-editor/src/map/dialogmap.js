import * as React from "react";
import MapView from './mapview.js'
import { withStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Dialog from '@material-ui/core/Dialog'
import Button from '@material-ui/core/Button';
import Icon from '@material-ui/core/Icon';
import {Control} from 'ol/control';
import {inherits} from 'ol/index.js';
import PointsTable from './pointstable.js'

import 'common-components/css/mapping.css'

// Map Controls
import 'ol/ol.css';

export class DialogMap extends React.Component {

  constructor(props) {
    super(props)

    this.state = {
      open: false,
      value: 0,
    }

    this.handleOpen = this.handleOpen.bind(this)
    this.handleClose = this.handleClose.bind(this)
    this.handleChange = this.handleChange.bind(this)
    inherits(closeControl, Control)
    inherits(pointsControl, Control)

    this.opt_options = {
      controls: [
        new closeControl({function: this.handleClose}),
        new pointsControl({function: this.handleChange}),
      ],
    }
  }

  componentDidMount() {

  }

  handleOpen() {

    this.setState({
      open: true
    });
    //this.map.render()
  }

  handleClose() {
    this.setState({
      open: false
    });
    // this.setState({multipoint: this.getWKT(this.state.value)});
  }

  handleChange(value) {
    this.setState({value: value});
  };



  TabContainer(props) {
    return (
      <Typography component="div" style={{ padding: 8 * 3 }}>
        {props.children}
      </Typography>
    );
  }

  render() {
    var {value} = this.state
    return (
      <div>
      <button type="button" onClick={this.handleOpen}>{this.props.shape}</button>

      <Dialog
        open={this.state.open}
        onEscapeKeyDown={this.handleClose}
        bodyStyle={{padding: "0 px"}}
        fullScreen
        >

        {value == 0 && <div>
            <MapView
              opt_options={this.opt_options}
              configureMap={this.props.configureMap}
              handleClose={this.handleClose}
              uniqueID={this.props.uniqueID}/>
          </div>}

        {value == 1 &&
        <div>
          <button type='button' onClick={() => this.handleChange(0)}>Back to Map</button>
          {this.props.textInput}
          <PointsTable
            updateFeature={this.props.updateFeature}
            handleHashmap={this.props.handleHashmap}
            coordsHashmap={this.props.coordsHashmap}
            feature={this.props.feature}
            textStyle={this.props.textStyle}
            shape={this.props.shape}
            uniqueID={this.props.uniqueID}></PointsTable>
        </div>}

        {value == 2 && <div>How did you get here.gif </div>}

      </Dialog>

      </div>
    )
  }

}

//for the maps close Control
var closeControl = function(opt_options) {
  var options = opt_options || {};
  const this_ = this;

  const button = document.createElement('button');
  button.innerHTML = 'X';
  button.type = 'button';
  button.title = 'Close'

  button.onclick = () => options['function']();

  const element = document.createElement('div');
  element.className = 'ol-unselectable ol-control close-control';
  element.appendChild(button);

  Control.call(this, {
    element: element,
    target: options.target
  })
}

var pointsControl = function(opt_options) {
  var options = opt_options || {};

  const button = document.createElement('button');
  button.innerHTML = 'P';
  button.type = 'button';
  button.title = 'WKT/Points'

  const this_ = this;

  button.onclick = () => options['function'](1);

  const element = document.createElement('div');
  element.className = 'ol-unselectable ol-control points-control';
  element.appendChild(button);

  Control.call(this, {
    element: element,
    target: options.target
  })
}
