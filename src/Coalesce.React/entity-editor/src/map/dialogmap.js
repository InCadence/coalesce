import * as React from "react";
import * as ol from 'openlayers';
import MapView from './mapview.js'
import Dialog from 'material-ui/Dialog';

// Map Controls
import 'openlayers/css/ol.css';

export class DialogMap extends React.Component {

  constructor(props) {
    super(props)

    this.state = {
      open: false,

    }

    this.handleOpen = this.handleOpen.bind(this)
    this.handleClose = this.handleClose.bind(this)
  }
  componentDidMount(props) {
    this.props.configureMap()
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

  render() {
    return (
      <div>
      <button type="button" onClick={this.handleOpen}>{this.props.shape}</button>

      <Dialog
        open={this.state.open}
        onRequestClose={() => this.handleClose()}
        bodyStyle={{padding: "0 px"}}
        fullScreen>
          <MapView configureMap={this.props.configureMap} uniqueID={this.props.uniqueID}/>
      </Dialog>
      </div>
    )
  }

}
