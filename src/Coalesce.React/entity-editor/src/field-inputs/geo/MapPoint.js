import React from 'react';
//import Modal from 'material-ui/Modal';
import TextField from 'material-ui/TextField';
import Dialog from 'material-ui/Dialog';
import Modal from 'react-responsive-modal';
import 'common-components/css/map_popup.css'
import 'common-components/css/ol.css';
import { DialogMap } from '../../map/dialogmap.js'
import MapMaker from '../../map/mapmaker.js';
import VectorSource from 'ol/source/Vector';
import Collection from 'ol/Collection';
import Point from 'ol/geom/Point';
import Feature from 'ol/Feature';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import {toStringHDMS} from 'ol/coordinate';
import VectorLayer from 'ol/layer/Vector';
import TileLayer from 'ol/layer/Tile';
import Overlay from 'ol/Overlay';
import OSM from 'ol/source/OSM';
import HashMap from 'hashmap/hashmap'
import MultiPoint from 'ol/geom/MultiPoint';
import WKT from 'ol/format/WKT';

var mgrs = require('mgrs');

export default class MapPoint extends React.Component {

  constructor(props) {
    super(props);

    this.opts = this.props.opts;
    this.attr = this.opts['attr'];
    this.field = this.opts['field'];

    this.state = {
      vectorSource: new VectorSource({
        wrapX: false,
        features: new Collection(),
      }),
      clickEvt: this.props.clickEvt,
      open: false,
      coords: ['', '', ''],
      visibility: 'hidden',
      wkt: this.props.wkt,
      coordsHashmap: this.props.coordsHashmap || new HashMap(),
    };

    this.fullWKT = ''
    this.wktEmpty = this.props.wktEmpty


    this.deleteFeature = this.deleteFeature.bind(this)
    this.handleInputFocus = this.handleInputFocus.bind(this)
    this.handleInputBlur = this.handleInputBlur.bind(this)
    this.handleInputChange = this.handleInputChange.bind(this)
    this.configureMap = this.configureMap.bind(this)
    this.updateFeature = this.updateFeature.bind(this)

  }

  componentDidMount() {
    //this.map.updateSize()
  }

  createFeature(coords) {
    var point = new Point(coords);
    var iconFeature = new Feature({
      geometry: point
    });

    this.stylizeFeature(iconFeature)
    this.setState({visibility: "hidden"})

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
    this.setState({visibility: "hidden"})
    return feature
  }

  deleteFeature() {
    this.map.getOverlays().item(0).setPosition(undefined);
    var vecSource = this.state.vectorSource;
    var clicked = vecSource.getFeatureById('clicked')
    if (clicked) {
      vecSource.removeFeature(clicked);
      this.props.parent.handleChangeFeature(this.props.parent, this);
    }
    this.setState({visibility: "hidden"})


  }

  deleteFeatures() {
    //unused
    this.map.getOverlays()[0].setPosition(undefined)
    this.state.vectorSource.clear()
    this.setState({visibility: "hidden"})

  }


  convertCoordinates(coords) {
    //coords are lonlat
    //var lonLatElem = document.getElementById('lonlat');
    //lonLatElem.textContent ='LonLat: ' + coords[0].toFixed(4) + ", " + coords[1].toFixed(4);

    var hdms = toStringHDMS(coords)
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


  configureMap(opt_options) {

    this.state.vectorSource.clear()
    var feature = new WKT().readFeature(this.props.wkt)

    if (this.props.shape == 'MULTIPOINT') {
      var multicoords = feature.getGeometry().getCoordinates()
      for (let i = 0; i < multicoords.length; i++) {
        this.createFeature(multicoords[i])
      }
    }
    else if (this.props.shape === 'POINT' && this.props.wkt != this.wktEmpty) {
      this.createFeature(feature.getGeometry().getCoordinates())
    }

    var controls = opt_options['controls']
    var vectorLayer = new VectorLayer({
      name: 'markers',
      source: this.state.vectorSource
    });

    var layers = [
      new TileLayer({
        source: new OSM({
          wrapX: false
        }),
      }),
      vectorLayer
    ];

    var pop = new Overlay({
      element: document.getElementById('popup' + this.props.uniqueID),
      insertFirst: false,
  })

    var overlays = [pop];

    var interactions = null;

    this.map = new MapMaker(layers, overlays, interactions, controls, 'map' + this.props.uniqueID).getMap();

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
    if(this.props.shape == 'MULTIPOINT') {
      return " ((x1 y1 z1), (x2 y2 z2), ...)"
    }
    return format;
  }

  getFullWKT(wkt) {

    if (wkt == this.wktEmpty) {
      this.props.handleOnChange(this.attr, wkt)
      return wkt
    }

    var fullWKT = ''
    if (this.props.shape == 'POINT') {
      var featureCoords = this.state.vectorSource.getFeatures()[0].getGeometry().getCoordinates()
      console.log(this.state.coordsHashmap);
      fullWKT = wkt.slice(0, wkt.length-1) + ' ' + (this.state.coordsHashmap.get(featureCoords) || 0) + ')'

    }
    else if (this.props.shape == 'MULTIPOINT') {
      var wktSplit = wkt.split(',')
      //initialize with the first part of the wkt to avoid fence post problem

      var featureCoords = this.props.parent.multipoint.getCoordinates();
      var firstZCoord = (this.state.coordsHashmap.get(featureCoords[0]) || 0);
      // 'MULTIPOINT((x0 y0 z0)'

      if (featureCoords.length == 1) {
        fullWKT = wktSplit[0].slice(0, fullWKT.length-2)+ ' ' + firstZCoord + '))'
      }
      else if (featureCoords.length > 1) {
        fullWKT = wktSplit[0]
        fullWKT = fullWKT.slice(0, fullWKT.length-1) + ' ' + firstZCoord + ')';

        for (let i = 0; i < featureCoords.length-2; i++) {
          var zCoord = (this.state.coordsHashmap.get(featureCoords[i+1]) || 0);
          var wktNextPart = wktSplit[i+1];
          fullWKT += ',' + wktNextPart.slice(0, wktNextPart.length-1) + ' ' + zCoord + ')';
        }

        var wktLastPart = wktSplit[featureCoords.length-1];
        var lastZCoord = this.state.coordsHashmap.get(featureCoords[featureCoords.length-1]) || 0;
        fullWKT += ',' + wktLastPart.slice(0, wktLastPart.length-2) + ' ' + lastZCoord + '))';
      }
      else {
        fullWKT += ')';
      }

    }

    this.props.handleOnChange(this.attr, fullWKT)
    console.log(fullWKT);

    return fullWKT;

  }

  updateFeature(newXYZCoordsDict, coordsHashmap, index) {

    var axis = index[0]
    var indexNum = parseInt(index.slice(1))
    var oldFeatureCoordinates = []
    if (this.props.shape == 'MULTIPOINT') {
      oldFeatureCoordinates = this.props.parent.multipoint.getCoordinates()
    }
    else if (this.props.shape === 'POINT') {
      oldFeatureCoordinates = [this.state.vectorSource.getFeatures()[0].getGeometry().getCoordinates()]
    }

    if (axis == 'z') {
      var xy = oldFeatureCoordinates[indexNum]
      var newZ = newXYZCoordsDict[index]
      var newCoordsHashmap = this.state.coordsHashmap.set(xy, newZ)
      this.setState({coordsHashmap: newCoordsHashmap})
      this.fullWKT = this.getFullWKT(this.props.wkt)
    }
    else {
      //keep old xy
      var xy = oldFeatureCoordinates[indexNum].slice(0)

      var oldZ = coordsHashmap.get(xy)
      console.log(oldZ);

      var newXY = xy.slice(0)
      if (axis == 'x') {

        var currentFeatures = this.state.vectorSource.getFeatures()
        newXY[0] = parseFloat(newXYZCoordsDict[index])
        currentFeatures[indexNum].getGeometry().setCoordinates(newXY)
      }
      else if (axis == 'y') {
        var currentFeatures = this.state.vectorSource.getFeatures()
        newXY[1] = parseFloat(newXYZCoordsDict[index])
        currentFeatures[indexNum].getGeometry().setCoordinates(newXY)
      }

      coordsHashmap.remove(xy).set(newXY, oldZ)
      this.props.parent.handleChangeFeature(this.props.parent, this)

    }
  }

  render() {
    var style = this.opts['style'];
    var label = this.opts['label'];

    var self = this;

    var feature = this.state.vectorSource.getFeatures()[0];

    if (this.props.shape == 'MULTIPOINT' && this.state.vectorSource.getFeatures().length > 0) {
      feature = new Feature({
        geometry: this.props.parent.multipoint
      });
    }

    return (
      <div>

        <div id={"popup" + this.props.uniqueID} className="ol-popup">
          <p onClick="this.select();"  id={'lonlat' + this.props.uniqueID}>{this.state.coords[0]}</p>
          <p onClick="this.select();"  readonly id={'hdms' + this.props.uniqueID}>{this.state.coords[1]}</p>
          <p onClick="this.select();" readonly id={'mgrs' + this.props.uniqueID}>{this.state.coords[2]}</p>

        </div>
        <button id={'button' + this.props.uniqueID} type="button" onClick={self.deleteFeature} style={{visibility: this.state.visibility}}>
          Delete
        </button>

        <DialogMap
          feature={feature}
          configureMap={this.configureMap}
          uniqueID={this.props.uniqueID}
          shape={this.props.shape}
          updateFeature={this.updateFeature}
          coordsHashmap={this.state.coordsHashmap}
          textInput={
            <TextField
              id={this.field.key}
              fullWidth={true}
              floatingLabelText={label + " - " + this.props.shape + this.shapeLabeler(" (x1 y1 z1, x2 y2 z2, ...)")}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={this.props.wkt}
              onFocus={this.handleInputFocus}
              onChange={this.handleInputChange}
              onBlur={this.handleInputBlur}
              defaultValue={this.field.defaultValue}></TextField>
          }
        />


      </div>
    );
  }

}
