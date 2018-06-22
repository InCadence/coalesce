import React from 'react';
//import Modal from 'material-ui/Modal';
import MapMaker from './Map.js';
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
import styles from './popup.css'
import Point from 'ol/geom/point';
import coordinate from 'ol/coordinate';
import Collection from 'ol/collection';
import 'common-components/css/ol.css'
import Modal from 'react-responsive-modal';


var mgrs = require('mgrs');

export default class MapPoint extends React.Component {

  constructor(props) {
    super(props);

    const uniqueID = Date.now();

    this.state = {
      features: [],
      vectorSource: new VectorSource({
        features: new Collection(),
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
    this.map.updateSize()
  }

  createFeature(coords) {
    var point = new Point(coords);
    var iconFeature = new Feature({
      geometry: point
    });

    this.stylizeFeature(iconFeature)
    return this.addFeature(iconFeature)
  }

  stylizeFeature(feature) {
    var iconStyle = new Style({
       image: new Icon({
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
      this.setState({visibility: 'hidden'});
      this.props.parent.handleDelete(this.props.parent, this);
    }

  }

  deleteFeatures() {
    this.map.getOverlays()[0].setPosition(undefined)
    this.state.vectorSource.clear()
  }


  convertCoordinates(coords) {
    //coords are lonlat
    //var lonLatElem = document.getElementById('lonlat');
    //lonLatElem.textContent ='LonLat: ' + coords[0].toFixed(4) + ", " + coords[1].toFixed(4);

    var hdms = coordinate.toStringHDMS(coords)
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
      console.log(error);
      this.props.parent.setState({wkt: this.wktSafe})
    }
  }

  handleOpen() {
    this.setState({
      open: true
    });
    this.map.render()
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

    var pop = new Overlay({
      element: document.getElementById('popup' + this.props.uniqueID),
      insertFirst: false,
  })

    var overlays = [pop];
    this.map = new MapMaker(layers, overlays, 'map' + this.props.uniqueID).getMap();

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
        floatingLabelText={label + " - MULTIPOINT (x1 y1 z1, x2 y2 z2, ...)"}
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
        <div id={'map' + this.props.uniqueID} className="map" ></div>

        <Modal
        center
          open={this.state.open}
          onClose={() => this.handleClose()}
          onEntered={this.reset}
        >


        </Modal>



      </div>
    );
  }

}
// <link
//   rel="stylesheet"
//   href="http://openlayers.org/en/v3.2.1/css/ol.css"
//   type="text/css"
// />
