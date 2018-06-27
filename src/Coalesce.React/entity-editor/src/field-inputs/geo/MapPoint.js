import React from 'react';
//import Modal from 'material-ui/Modal';
import MapMaker from '../../Map.js';
import TextField from 'material-ui/TextField';
import Dialog from 'material-ui/Dialog';
import Modal from 'react-responsive-modal';
import 'common-components/css/map_popup.css'
import 'openlayers/css/ol.css';
import * as ol from 'openlayers'
import { MapView } from './map'


var mgrs = require('mgrs');

export default class MapPoint extends React.Component {

  constructor(props) {
    super(props);


    this.state = {
      features: [],
      vectorSource: new ol.source.Vector({
        wrapX: false,
        features: new ol.Collection(),
      }),
      clickEvt: this.props.clickEvt,
      open: false,
      coords: ['', '', ''],
      visibility: 'hidden',
      wkt: this.props.wkt,
    };

    this.handleOpen = this.handleOpen.bind(this)
    this.handleClose = this.handleClose.bind(this)
    this.deleteFeature = this.deleteFeature.bind(this)
    this.handleInputFocus = this.handleInputFocus.bind(this)
    this.handleInputBlur = this.handleInputBlur.bind(this)
    this.handleInputChange = this.handleInputChange.bind(this)
    this.reset = this.reset.bind(this)

    this.field = this.props.opts['field'];

  }

  componentDidMount() {
    this.configureMap()
    //this.map.updateSize()
  }

  createFeature(coords) {
    var point = new ol.geom.Point(coords);
    var iconFeature = new ol.Feature({
      geometry: point
    });

    this.stylizeFeature(iconFeature)
    return this.addFeature(iconFeature)
  }

  stylizeFeature(feature) {
    var iconStyle = new ol.style.Style({
       image: new ol.style.Icon({
         anchor: [.5, 33],
         anchorXUnits: 'fraction',
         anchorYUnits: 'pixels',
         opacity: 0.75,
         src: 'https://i.imgur.com/mDsYhky.png'
       })
     })
    feature.setStyle(iconStyle);

  }

  addFeature(feature) {
    this.state.vectorSource.addFeature(feature);
    return feature
  }


  deleteFeature() {
    this.map.getOverlays().item(0).setPosition(undefined);
    var vecSource = this.state.vectorSource;
    var clicked = vecSource.getFeatureById('clicked')
    if (clicked) {
      vecSource.removeFeature(clicked);
      this.props.parent.handleDelete(this.props.parent, this);
    }

  }

  deleteFeatures() {
    //unused
    this.map.getOverlays()[0].setPosition(undefined)
    this.state.vectorSource.clear()
  }


  convertCoordinates(coords) {
    //coords are lonlat
    //var lonLatElem = document.getElementById('lonlat');
    //lonLatElem.textContent ='LonLat: ' + coords[0].toFixed(4) + ", " + coords[1].toFixed(4);

    var hdms = ol.coordinate.toStringHDMS(coords)
    //var hdmsElem = document.getElementById('hdms' );
    //hdmsElem.textContent = 'HDMS: ' + hdms;

    var _mgrs = mgrs.forward(coords);
    //var mgrsElem = document.getElementById('mgrs' )
    //mgrsElem.textContent = 'MGRS: ' + _mgrs;
    this.setState({coords: [
      'LonLat: ' + coords[0].toFixed(4) + ", " + coords[1].toFixed(4),
      'HDMS: ' + hdms,
      'MGRS: ' + _mgrs,

    ]})
  }

  handleInputFocus(event) {
    this.wktSafe = event.target.value;
  }

  handleInputChange(event) {
    this.props.parent.setState({wkt: event.target.value})
  }

  handleInputBlur() {
    try {

      var clicked = this.state.vectorSource.getFeatureById('clicked')
      if (clicked) {
        clicked.setId('')
        this.map.getOverlays().item(0).setPosition(undefined);
      }
      this.props.parent.handleInput(this);
    }
    catch (error) {
      this.props.parent.setState({wkt: this.wktSafe})
    }
  }

  handleOpen() {
    this.setState({
      open: true
    });
    //this.map.render()
  }

  reset() {
    this.map.setTarget('map' + this.props.uniqueID)
    this.map.render()
    this.map.updateSize()
  }

  handleClose() {
    this.setState({
      open: false
    });
    // this.setState({multipoint: this.getWKT(this.state.value)});
  }

  configureMap() {
    var vectorLayer = new ol.layer.Vector({
      name: 'markers',
      source: this.state.vectorSource
    });

    var layers = [
      new ol.layer.Tile({
        source: new ol.source.OSM({
          wrapX: false
        }),
      }),
      vectorLayer
    ];

    var pop = new ol.Overlay({
      element: document.getElementById('popup' + this.props.uniqueID),
      insertFirst: false,
  })

    var overlays = [pop];

    var interactions = null;

    this.map = new MapMaker(layers, overlays, interactions, 'map' + this.props.uniqueID).getMap();

    var self = this;
    this.map.on('click',
    function(evt) {
      var point = self.props.clickEvt(evt, self);
      if(point) {
        self.props.parent.handlePoint(point, self.props.parent, self);
      }
    });
    return this.map;
  }

  shapeLabeler(format) {
    if(this.props.tag == 'MULTIPOINT') {
      return " ((x1 y1 z1), (x2 y2 z2), ...)"
    }
    return format;
  }

  render() {
    var opts = this.props.opts;
    var field = opts['field'];
    var style = opts['style'];
    var label = opts['label'];
    var attr = opts['attr'];

    var self = this;

    return (
      <div>
      <TextField
        id={field.key}
        fullWidth={true}
        floatingLabelText={label + " - " + this.props.tag + this.shapeLabeler(" (x1 y1 z1, x2 y2 z2, ...)")}
        underlineShow={this.props.showLabels}
        style={style.root}
        value={this.props.wkt}
        onFocus={this.handleInputFocus}
        onChange={this.handleInputChange}
        onBlur={this.handleInputBlur}
        defaultValue={field.defaultValue}></TextField>

        <button type="button" onClick={this.handleOpen}>{this.props.tag}</button>


        <div id={"popup" + this.props.uniqueID} className="ol-popup">
          <p onClick="this.select();"  id={'lonlat' + this.props.uniqueID}>{this.state.coords[0]}</p>
          <p onClick="this.select();"  readonly id={'hdms' + this.props.uniqueID}>{this.state.coords[1]}</p>
          <p onClick="this.select();" readonly id={'mgrs' + this.props.uniqueID}>{this.state.coords[2]}</p>

        </div>
        <button id={'button' + this.props.uniqueID} type="button" onClick={self.deleteFeature} style={{visibility: this.state.visibility}}>
          Delete
        </button>

        <Dialog
          open={this.state.open}
          onRequestClose={() => this.handleClose()}
          title='Choose Points'
          fullScreen>
            <MapView configureMap={this.configureMap.bind(this)} uniqueID={this.props.uniqueID}/>
        </Dialog>



      </div>
    );
  }

}
// <link
//   rel="stylesheet"
//   href="http://openlayers.org/en/v3.2.1/css/ol.css"
//   type="text/css"
// />
