import React from 'react';
//import Modal from 'material-ui/Modal';
import styles from './popup.css';
import MapMaker from './Map.js';
import Map from 'ol/map';
import Multipoint from './Multipoint.js';
import Point from './Point.js';
import coordinate from 'ol/coordinate';
import {default as VectorLayer} from 'ol/layer/vector';
import {default as VectorSource} from 'ol/source/vector';
import Feature from 'ol/feature';
import Style from 'ol/style/style';
import Icon from 'ol/style/icon';
import Tile from 'ol/layer/tile';
import OSM from 'ol/source/osm';
import Overlay from 'ol/overlay';
import WKT from 'ol/format/wkt';
import TextField from 'material-ui/TextField';
import Dialog from 'material-ui/Dialog';

var mgrs = require('mgrs');

export default class MarkerMap extends React.Component {

  constructor(props) {
    super(props);
    const uniqueID = Date.now();
    this.state = {
      list: (this.props.list), //can be either 'Multipoint' or 'Point'
      features: this.props.value || [],
      uniqueID: uniqueID,
      vectorSource: new VectorSource({
        features: this.props.features
      }),
    };

    this.handleOpen = this.handleOpen.bind(this)
    this.handleClose = this.handleClose.bind(this)


  }

  createFeature(coords) {
    var point = new Point(coords);
    var iconFeature = new Feature({
      geometry: point
    });
    var iconStyle = new Style({
       image: new Icon({
         anchor: [.5, 33],
         anchorXUnits: 'fraction',
         anchorYUnits: 'pixels',
         opacity: 0.75,
         src: 'https://i.imgur.com/mDsYhky.png'
       })
     })
    iconFeature.setStyle(iconStyle);

    this.state.vectorSource.addFeature(iconFeature);
    return iconFeature;
  }

  deleteFeature() {

  }

  deleteFeatures() {
    this.state.vectorSource.clear()
  }


  convertCoordinates(coords) {
    //coords are lonlat
    var uniqueID = this.state.uniqueID;

    var lonLatElem = document.getElementById('lonLat' + uniqueID);
    lonLatElem.innerHTML ='LonLat: ' + coords[0].toFixed(4) + ", " + coords[1].toFixed(4);

    var hdms = coords.toStringHDMS(coords)
    var hdmsElem = document.getElementById('hdms' + uniqueID);
    hdmsElem.innerHTML = 'HDMS: ' + hdms;

    var _mgrs = mgrs.forward(coords);
    var mgrsElem = document.getElementById('mgrs' + uniqueID)
    mgrsElem.innerHTML = 'MGRS: ' + _mgrs;
  }

  componentDidMount() {
    var vectorLayer = new VectorLayer({
      name: 'markers',
      source: this.state.vectorSource
    });

    var layers = [
      new Tile({
        source: new OSM()
      }),
      vectorLayer
    ];

    var overlays = [new Overlay({
      element: document.getElementById('popup')
    })];

    var map = new MapMaker(layers, overlays, 'map');

  }

  handleInput() {

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
    this.setState({multipoint: this.getWKT(this.state.value)});
  }

  getWKT(value) {
    if (value.length > 0) {
      var multi =  new WKT().writeFeatures(value, {
        decimals: 4
      });
      return multi;
    }
    return "MULTIPOINT ()";
  }

  render() {
    var uniqueID = this.state.uniqueID;

    var opts = this.props.opts;
    var field = opts['field'];
    var style = opts['style'];
    var label = opts['label'];
    var attr = opts['attr'];

    const PointTag =  `${this.state.list}`

    return (
      <div>
      <TextField
        id={field.key}
        fullWidth={true}
        floatingLabelText={label + " - MULTIPOINT (x1 y1 z1, x2 y2 z2, ...)"}
        underlineShow={this.props.showLabels}
        style={style.root}
        value={this.state.multipoint}
        defaultValue={field.defaultValue}
        onBlur={this.handleInput}
      />
      <button type="button" onClick={this.handleOpen}>{PointTag}</button>
      <div id='map' tabIndex="0" className="map" ref="olmap"></div>

      <Dialog
        open={this.state.open}
        onRequestClose={() => this.handleClose()}
        title='Choose Points'
      >

        <PointTag
          id='pointing'
          uniqueID={uniqueID}
          setCoordinates={(coords) => this.setCoordinates(coords)}
          createFeature={(coords) => this.createFeature(coords)}
          tabIndex="0" className="map" ref="olmap"
          deleteFeatures={() => this.deleteFeatures}
        >

        </PointTag>
        <div id={"popup" + uniqueID} className="ol-popup">
          <div id="popup-content">
            <p id={'lonlat' + uniqueID}></p>
            <p id={'hdms' + uniqueID}></p>
            <p id={'mgrs' + uniqueID}></p>
            <p id={'delete' + uniqueID}>

            </p>
          </div>
        </div>
      </Dialog>
      </div>
    );
  }

}
///<button onClick={this.deleteFeature('clicked' + uniqueID)}>
  ///Delete
///</button>
