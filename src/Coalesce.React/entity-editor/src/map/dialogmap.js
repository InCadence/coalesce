import * as React from "react";
import MapView from './mapview.js'
import Dialog from '@material-ui/core/Dialog'
import {Control} from 'ol/control';
import {inherits} from 'ol/index.js';
import PointsTable from './pointstable.js'
import { IconButton } from 'coalesce-components/lib/components'

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
    inherits(deletePointControl, Control)


    var controls = [
      new closeControl({function: this.handleClose}),
      new pointsControl({function: this.handleChange}),
    ]
    if(this.props.shape === 'MULTIPOINT' || this.props.shape === 'POINT') {
      controls.push(new deletePointControl({function: this.props.deleteFeature}))
    }
    this.opt_options = {
      controls: controls
    }
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

  render() {
    var {value} = this.state
    console.log(this.props.coordsHashmap);
    return (
      <div>
      <IconButton icon='/images/svg/map.svg' onClick={this.handleOpen} />

      <Dialog
        open={this.state.open}
        onEscapeKeyDown={this.handleClose}
        bodyStyle={{padding: "0 px"}}
        fullScreen
        >

        {value === 0 && <div>
            <MapView
              opt_options={this.opt_options}
              configureMap={this.props.configureMap}
              handleClose={this.handleClose}
              uniqueID={this.props.uniqueID}
              coords={this.props.convertCoordinates}
              shape={this.props.shape}/>
          </div>}

        {value === 1 &&
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

      </Dialog>

      </div>
    )
  }

}

//for the maps close Control
var closeControl = function(opt_options) {
  var options = opt_options || {};


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

  button.onclick = () => options['function'](1);

  const element = document.createElement('div');
  element.className = 'ol-unselectable ol-control points-control';
  element.appendChild(button);

  Control.call(this, {
    element: element,
    target: options.target
  })
}

var deletePointControl = function(opt_options) {
  var options = opt_options || {};

  const button = document.createElement('button');
  button.innerHTML = 'D';
  button.type = 'button';
  button.title = 'Delete chosen point'

  button.onclick = () => options['function']();

  const element = document.createElement('div');
  element.className = 'ol-unselectable ol-control delete-control';
  element.appendChild(button);

  Control.call(this, {
    element: element,
    target: options.target
  })
}
