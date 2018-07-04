import React from 'react';
import TextField from 'material-ui/TextField';
import 'ol/ol.css';
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
import Draw from 'ol/interaction/Draw';
import Modify from 'ol/interaction/Modify';
import WKT from 'ol/format/WKT';
import Circle from 'ol/geom/Circle';
import Snap from 'ol/interaction/Snap';
import HashMap from 'hashmap/hashmap'

var mgrs = require('mgrs');


export default class Shape extends React.Component {

  constructor(props) {
    super(props);
    this.wktEmpty = (this.props.shape == 'Circle' ? 'POINT' : this.props.shape.toUpperCase())  + ' EMPTY';
    this.state = {
      vectorSource: new VectorSource({
        wrapX: false,
      }),
      wkt: this.wktEmpty,
      radius: '0',
      coordsHashmap: new HashMap(),
    };


    this.handleInputFocus = this.handleInputFocus.bind(this)
    this.handleInputBlur = this.handleInputBlur.bind(this)
    this.handleInputChange = this.handleInputChange.bind(this)
    this.configureMap = this.configureMap.bind(this)
    this.handleHashmap = this.handleHashmap.bind(this)
    this.updateFeature = this.updateFeature.bind(this)

  }

  configureMap(opt_options) {
    var self = this;
    //controls should be passed into the dictionary opt_options as a list
    var controls = opt_options['controls']

    var vectorLayer = new VectorLayer({
      name: 'markers',
      source: self.state.vectorSource
    });
    //null for defaults
    var layers = [
      new TileLayer({
        source: new OSM({
          wrapX: false,
        })
      }),
      vectorLayer
    ];
    var overlays = null;



    var maker = new MapMaker(layers, overlays, null, controls,  'map' + this.props.uniqueID)
    this.map = maker.getMap();

    var drawInteraction = new Draw({
      source: this.state.vectorSource,
      type: this.props.shape
    })

    drawInteraction.on('drawstart', function(evt) {
      self.handleDrawstart(self);
    });

    this.state.vectorSource.on('addfeature', function(evt) {
      self.handleAddfeature(self, evt.feature)
    })

    var modifyInteraction = new Modify({
      source: this.state.vectorSource
    })

    modifyInteraction.on('modifyend', function(evt) {
      var feature = evt.features.item(0)
      self.handleChangeFeature(self, feature)
    })


    modifyInteraction.setActive(true)

    this.map.addInteraction(drawInteraction)
    this.map.addInteraction(modifyInteraction)



    return this.map;

  }

  handleDrawstart(self) {

    self.clearFeatures(self.state.vectorSource);

    self.setState( {wkt: self.wktEmpty})
    if (this.props.shape == 'Circle') {
      self.setState( {radius: 'None'} )
    }
  }

  handleAddfeature(self, feature) {
    self.setFeatureToWKT(feature);
  }

  handleChangeFeature(self, feature) {
    self.setFeatureToWKT(feature)
  }

  handleInputFocus(event) {
    if (this.props.shape == 'Circle') {
      var centerElem = document.getElementById('center' + this.props.uniqueID)
      var radiusElem = document.getElementById('radius' + this.props.uniqueID)
      this.wktSafe = [centerElem.value, radiusElem.value]
    }
    else {
      this.wktSafe = event.target.value;
    }
  }

  handleInputChange(event, opts) {
    if (opts == 'radius') {
      this.setState({radius: event.target.value})
    }
    else {
      this.setState({wkt: event.target.value})
    }
  }

  handleInputBlur() {
    try {
      const NOTHING = '';
      const MAPORIGIN = [0, 0]
      if (this.state.wkt == NOTHING) {
        this.setState({
          wkt: this.wktEmpty,
          radius: '0'
        })
        this.clearFeatures(this.state.vectorSource)
      }
      else {
        if (this.props.shape == 'Circle') {
          const ZERO = '0';
          var vecSource = this.state.vectorSource;
          var circleExisted = true;

          //read the center point input as a Feature (wkt -> Feature)
          //  get its geometry (Point)
          //  get coordinates from geometry ( [x, y] format )
          var centerCoords = new WKT().readFeature(this.state.wkt).getGeometry().getCoordinates();
          //if coords array is length of 0 (meaning the array is [])
          //  set it to [0, 0], otherwise keep it the same
          centerCoords = (centerCoords.length == 0) ? MAPORIGIN : centerCoords

          //if the radius from text field is '', set it to '0'
          var radiusAsString = this.state.radius || ZERO;
          var radiusAsNum = 0;
          if(!isNaN(radiusAsString)) {
            //if the radius IS a string/number
            //turn string to number
            radiusAsNum = parseFloat(radiusAsString);


            if (this.state.radius == NOTHING && radiusAsString == ZERO) {
              //if the radius was '' and is therefore turned into '0',
              //    update the text field to be '0' instead of '' (for aesthetics)
              //this won't set the text field to '0' if it was already a number
              this.setState({radius: radiusAsString})
            }
          }
          else {
            throw new TypeError("Radius must be a number!")
          }
          var features = vecSource.getFeatures();
          if(features.length > 0 && !this.props.multi) {
            //get the circle, set points
            features[0].getGeometry().setCenterAndRadius(centerCoords, radiusAsNum)
          }
          else {
            var feat = new Feature({
              geometry: new Circle(centerCoords, radiusAsNum)
            });

            vecSource.addFeature(feat)
          }

        }
        else { //shape != 'Circle'
          var vecSource = this.state.vectorSource;
          var opts = this.props.opts;
          var field = opts['field'];

          var features = vecSource.getFeatures();


          var inputElem = document.getElementById(field.key)
          var feature = new WKT().readFeature(inputElem.value)
          var newCoords = feature.getGeometry().getCoordinates()

          this.clearFeatures(this.state.vectorSource)
          this.state.vectorSource.addFeature(feature)

        }
      }
    }
    catch (error) {
     console.log(error);
     if (this.props.shape == 'Circle') {
       this.setState({
         wkt: this.wktSafe[0],
         radius: this.wktSafe[1]
       })
     }
     else {
       this.setState({wkt: this.wktSafe})
      }
    }
  }

  handleHashmap(newHashmap) {
    this.setState({coordsHashmap: newHashmap})
  }

  clearFeatures(vectorSource) {
    vectorSource.clear()
    this.state.coordsHashmap.clear()
    }

  updateFeature(newXYZCoordsDict, coordsHashmap, index) {
    //function updates the hashmap so it can be passed in again to PointsTable
    //calls setCoordinates as well for the feature
    //takes in the entire new set of coordinates (only one should be changed theoretically)
    //grab the current feature coordinates, shallow copy, update one index, feature/geometry.setCoordinates

    //index is '<x|y|z><indexNum>' (like 'x10' or 'y3')

    var indexNum = parseInt(index.slice(1));

    var axis = index[0]
    var oldFeatureCoordinates = this.state.vectorSource.getFeatures()[0].getGeometry().getCoordinates()
    if (this.props.shape == 'Circle') {
      oldFeatureCoordinates = this.state.vectorSource.getFeatures()[0].getGeometry().getCenter()
    }
    if (axis == 'z') {
      //get the [x, y] array so the z can be set in the hashmap
      var xy = oldFeatureCoordinates[indexNum]
      if(this.props.shape == 'Polygon') {
        xy = oldFeatureCoordinates[0][indexNum]
      }
      //set() returns the hashmap so programmers can chain functions
      var newCoordsHashmap = coordsHashmap.set(xy, newXYZCoordsDict[index])

      this.setState({coordsHashmap: newCoordsHashmap})
    }
    else {
      var newFeatureCoordinates = this.state.vectorSource.getFeatures()[0].getGeometry().getCoordinates() //shallow copy of old Coordinates
                                                                                            //this array will be updated ^
      if(axis == 'x') {
        newFeatureCoordinates[indexNum][0] = newXYZCoordsDict[index] //update the x value of the shallow copy's xy pair
      }
      else if (axis == 'y') {
        newFeatureCoordinates[indexNum][1] = newXYZCoordsDict[index] //update the y value of the shallow copy's xy pair
      }
      var oldXY = oldFeatureCoordinates[indexNum]
      var oldZ = coordsHashmap.get(oldXY)
      coordsHashmap.remove(oldXY).set(newFeatureCoordinates[indexNum], oldZ)

      //set the feature/geometry's coordinates
      this.state.vectorSource.getFeatures()[0].getGeometry().setCoordinates(newFeatureCoordinates)
    }
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

  setFeatureToWKT(feature) {
    if (this.props.shape == 'Circle') {
      var circle = feature.getGeometry();
      var center = circle.getCenter();
      var radius = circle.getRadius();
      this.setState({
        wkt: `POINT (${center[0]} ${center[1]})`,
        radius: radius
      })
    }
    else {
      var wkt = new WKT().writeFeature(feature);
      this.setState( {wkt: wkt} );
    }
  }

  shapeLabeler(format) {
    if(this.props.shape == 'Polygon') {
      return `(${format})`
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

        <DialogMap
          feature={this.state.vectorSource.getFeatures()[0]}
          configureMap={this.configureMap}
          uniqueID={this.props.uniqueID}
          shape={this.props.shape}
          handleHashmap={this.handleHashmap}
          updateFeature={this.updateFeature}
          coordsHashmap={this.state.coordsHashmap}
          textStyle={style.root}
          textInput={
            (this.props.shape == 'Circle' &&
              <div>
                <TextField
                  id={'center' + this.props.uniqueID}
                  fullWidth={true}
                  floatingLabelText={"Circle - POINT (x1 y1 z1)"}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  value={this.state.wkt}
                  defaultValue={field.defaultValue}
                  onFocus={this.handleInputFocus}
                  onChange={this.handleInputChange}
                  onBlur={this.handleInputBlur}
                />
                <TextField
                  id={'radius' + this.props.uniqueID}
                  fullWidth={true}
                  floatingLabelText={"Circle - RADIUS"}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  value={this.state.radius}
                  defaultValue={field.defaultValue}
                  onFocus={this.handleInputFocus}
                  onChange={(e) => this.handleInputChange(e, 'radius')}
                  onBlur={this.handleInputBlur}
                />
              </div>)  ||


            (this.props.shape != 'Circle' &&
              <TextField
                id={field.key}
                fullWidth={true}
                floatingLabelText={label + " - " + self.props.shape.toUpperCase() + this.shapeLabeler(" (x1 y1 z1, x2 y2 z2, ...)")}
                underlineShow={this.props.showLabels}
                style={style.root}
                value={this.state.wkt}
                defaultValue={field.defaultValue}
                onFocus={this.handleInputFocus}
                onChange={this.handleInputChange}
                onBlur={this.handleInputBlur}
              />)
        }/>
      </div>
    );
  }

}
