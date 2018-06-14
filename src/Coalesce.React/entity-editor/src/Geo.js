import React from 'react';
import Map from 'ol/map';
import OLMap from './OpenLayerMap.js';
import Dialog from 'material-ui/Dialog';
import DatePicker from 'material-ui/DatePicker';
import FlatButton from 'material-ui/FlatButton';


export default class Geoinput extends React.Component {

  constructor(props) {
    super(props);
    //"list" is true if choosing multiple points, false if otherwise
    if (this.props.list == 'true') {
      this.state = {
        list: this.props.list,
        button: "Set Points",
      };
    }
    else {
      this.state = {
        list: this.props.list,
        button: "Set Point",
      };
    }

    this.handleOpen = this.handleOpen.bind(this);
    this.handleClose = this.handleClose.bind(this);

    var divStyle = {
      width: '100% !important',
      height: '100% !important',
      position: 'absolute',
      top: 0,
      left: 0,
    }
  }

  handleOpen() {
    this.setState({
      open: true
    });
  }

  handleClose() {
    this.setState({
      open: false
    });
  }

  render() {

    return (
      <div style={this.divStyle}>
        <button type="button" onClick={this.handleOpen}>{this.state.button}</button>
        <OLMap list={this.state.list} value={this.props.value} handleOnChange={this.props.handleOnChange}
        ></OLMap>
        <Dialog
          open={this.state.open}
          onRequestClose={() => this.handleClose()}
          title='Choose Points'
          fullScreen>

        </Dialog>
      </div>
    );
  }

}
