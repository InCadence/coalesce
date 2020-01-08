import React from 'react';
import TextField from "@material-ui/core/TextField";
import 'ol/ol.css';
import { DialogMap } from '../../map/dialogmap.js'
import MapMaker from '../../map/mapmaker.js';
import VectorSource from 'ol/source/Vector';
import Feature from 'ol/Feature';
import {toStringHDMS} from 'ol/coordinate';
import VectorLayer from 'ol/layer/Vector';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import Draw from 'ol/interaction/Draw';
import Modify from 'ol/interaction/Modify';
import WKT from 'ol/format/WKT';
import Circle from 'ol/geom/Circle';
import HashMap from 'hashmap/hashmap'

var mgrs = require('mgrs');


export default class Shape extends React.Component {

  constructor(props) {
    super(props);

    this.wktEmpty = (this.props.shape == 'Circle' ? 'POINT' : this.props.shape.toUpperCase())  + ' EMPTY';

    this.opts = this.props.opts;
    this.attr = this.opts['attr'];
    this.field = this.opts['field'];

    var vectorSource = new VectorSource(
      {
        wrapX: false,
      }
    )

    if(this.field[this.attr]) {

      this.state = {
        vectorSource: vectorSource,
        wkt: this.stripZAxis(this.field[this.attr]),
        fullWKT: this.field[this.attr],
        radius: this.field['radius'],
        coordsHashmap: this.coordsHashmap
      };
      console.log(this.props.shape);
      console.log(this.state.coordsHashmap);
    }
    else {
      this.state = {
        vectorSource: vectorSource,
        wkt: this.wktEmpty,
        fullWKT: this.wktEmpty,
        radius: '0',
        coordsHashmap: new HashMap(),
      };

    }



    this.handleInputFocus = this.handleInputFocus.bind(this)
    this.handleInputBlur = this.handleInputBlur.bind(this)
    this.handleInputChange = this.handleInputChange.bind(this)
    this.configureMap = this.configureMap.bind(this)
    this.handleHashmap = this.handleHashmap.bind(this)
    this.updateFeature = this.updateFeature.bind(this)
    this.deleteFeatures = this.deleteFeatures.bind(this)
  }

  componentWillReceiveProps(newProps) {
    this.opts = newProps.opts;
    this.attr = this.opts['attr'];
    this.field = this.opts['field'];


    if(this.field[this.attr] && this.field[this.attr] !== '') {

      this.setState({
        wkt: this.stripZAxis(this.field[this.attr]),
        fullWKT: this.field[this.attr],
        radius: this.field['radius'],
        coordsHashmap: this.coordsHashmap,
      });
      //this.state.coordsHashmap = this.coordsHashmap
    }
    else {
      this.clearFeatures(this.state.vectorSource)
      this.setState({
        wkt: this.wktEmpty,
        fullWKT: this.wktEmpty,
        radius: '0',
        coordsHashmap: new HashMap(),
      });
      console.log('else');
      console.log(this.state.vectorSource.getFeatures());
    }
  }

  stripZAxis(fullWKT) {
    var pattern = / ?-?[0-9]\d*(\.\d+)?/g //matches the point's 3 values

    if (this.props.shape === 'Circle') {
      var coords = fullWKT.match(pattern) //[x, y, z]

      this.coordsHashmap = new HashMap()
      this.coordsHashmap.set([parseFloat(coords[0]), parseFloat(coords[1])], parseFloat(coords[2]))
      var wkt = 'POINT(' + coords[0] + ' ' + coords[1] + ')'
      return wkt
    }
    else {
      var coords = fullWKT.match(pattern) //[x, y, z]
      var coordPos = 1;

      this.coordsHashmap = new HashMap()

      var wkt = this.props.shape +  '('
      if (this.props.shape === 'Polygon') {
        wkt += '('
      }
      wkt += coords[0] + ' ' + coords[1]
      this.coordsHashmap.set([parseFloat(coords[0]), parseFloat(coords[1])], parseFloat(coords[2]) )

      for (let i = 3; i < coords.length; i++) {
        if (coordPos === 1) {
          wkt += ',' + coords[i]
          this.coordsHashmap.set([parseFloat(coords[i]), parseFloat(coords[i+1])], parseFloat(coords[i+2]) )
          coordPos++;
        }
        else if (coordPos === 2) {
          wkt += ' ' + coords[i]
          coordPos++;
        }
        else if ( coordPos === 3) {
          coordPos = 1;
          continue
        }
      }
      wkt += ')'
      if (this.props.shape === 'Polygon') {
        wkt += ')'
      }
      return wkt
    }
  }

  configureMap(opt_options) {
    var self = this;

    this.state.vectorSource.clear()
    if (this.state.wkt !== this.wktEmpty)
    {
      console.log(this.state.wkt);
      var feature = new WKT().readFeature(this.state.wkt)

      if (this.props.shape === 'Circle') {
        feature = new Feature({
          geometry: new Circle(feature.getGeometry().getCoordinates(), this.state.radius || 10)
        })
      }
      this.state.vectorSource.addFeature(feature)
    }
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

    var maker = new MapMaker(layers, overlays, null, controls,  'map' + this.field.key)

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

    console.log(this.state.wkt);
    this.setState({map: this.map})
    return this.map;

  }

  handleDrawstart(self) {

    self.clearFeatures(self.state.vectorSource);

    self.setState( {wkt: self.wktEmpty})
    if (this.props.shape == 'Circle') {
      self.setState( {radius: '0'} )
    }
  }

  handleAddfeature(self, feature) {
    self.setFeatureToWKT(feature);
  }

  handleChangeFeature(self, feature) {
    self.setFeatureToWKT(feature)
  }

  handleInputFocus(event) {
    if (this.props.shape === 'Circle') {
      var centerElem = document.getElementById(this.field.key)
      var radiusElem = document.getElementById('radius' + this.field.key)
      this.wktSafe = [centerElem.value, radiusElem.value]
    }
    else {
      this.wktSafe = event.target.value;
    }
  }

  handleInputChange(event, opts) {
    if (opts == 'radius') {
      this.setState({radius: event.target.value})
      this.props.handleOnChange('radius', event.target.value)
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
        this.props.handleOnChange(this.attr, this.wktEmpty)
        this.props.handleOnChange('radius', '0')
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
              this.props.handleOnChange('radius', radiusAsString)
              console.log(radiusAsString);
              this.setState({radius: radiusAsString})
            }
          }
          else {
            throw new TypeError("Radius must be a number!")
          }
          features = vecSource.getFeatures();
          if(features.length > 0) {
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

  handleRadiusBlur() {
    try {
      var vecSource = this.state.vectorSource;
      const NOTHING = '';
      const MAPORIGIN = [0, 0]

      const ZERO = '0';


      //read the center point input as a Feature (wkt -> Feature)
      //  get its geometry (Point)
      //  get coordinates from geometry ( [x, y] format )
      var centerCoords = new WKT().readFeature(this.state.wkt).getGeometry().getCoordinates();
      //if coords array is length of 0 (meaning the array is [])
      //  set it to [0, 0], otherwise keep it the same
      centerCoords = (centerCoords.length === 0) ? MAPORIGIN : centerCoords
      //if the radius from text field is '', set it to '0'
      var radiusAsString = this.state.radius || ZERO;
      var radiusAsNum = 0;
      var features = []
      if(!isNaN(radiusAsString)) {
        //if the radius IS a string/number
        //turn string to number
        radiusAsNum = parseFloat(radiusAsString);

        if (this.state.radius === NOTHING && radiusAsString === ZERO) {
          //if the radius was '' and is therefore turned into '0',
          //    update the text field to be '0' instead of '' (for aesthetics)
          //this won't set the text field to '0' if it was already a number
          this.props.handleOnChange('radius', radiusAsString)
          console.log(radiusAsString);
          this.setState({radius: radiusAsString})
        }
      }
      else {
        throw new TypeError("Radius must be a number!")
      }
      features = vecSource.getFeatures();
      // if(features.length > 0) {
      //   //get the circle, set points
      //   console.log(centerCoords);
      //   console.log(radiusAsNum);
      //   features[0].getGeometry().setCenter(centerCoords)
      //   features[0].getGeometry().setRadius(radiusAsNum)
      // }
      // else {
      //   var feat = new Feature({
      //     geometry: new Circle(centerCoords, radiusAsNum)
      //   });
      //
      //   vecSource.addFeature(feat)
      // }
      this.deleteFeatures()
      var feat = new Feature({
        geometry: new Circle(centerCoords, radiusAsNum)
      });

      vecSource.addFeature(feat)
      console.log(vecSource.getFeatures()[0].getGeometry().getRadius());
    }
    catch (error) {
     console.log(error);
     this.setState({
       wkt: this.wktSafe[0],
       radius: this.wktSafe[1]
     })
    }
  }
  handleHashmap(newHashmap) {
    this.setState({coordsHashmap: newHashmap})
  }

  clearFeatures(vectorSource) {
    vectorSource.clear()
  }

  updateFeature(newXYZCoordsDict, coordsHashmap, index) {
    //function updates the hashmap so it can be passed in again to PointsTable
    //calls setCoordinates as well for the feature
    //takes in the entire new set of coordinates (only one should be changed theoretically)
    //grab the current feature coordinates, shallow copy, update one index, feature/geometry.setCoordinates

    //index is '<x|y|z><indexNum>' (like 'x10' or 'y3')

    var indexNum = parseInt(index.slice(1));

    var axis = index[0] //get the letter (x y or z)
    var oldFeatureCoordinates = null
    if (this.props.shape == 'Circle') {
      oldFeatureCoordinates = [this.state.vectorSource.getFeatures()[0].getGeometry().getCenter().slice(0)]
    }
    else if (this.props.shape == 'Polygon') {
      oldFeatureCoordinates = this.state.vectorSource.getFeatures()[0].getGeometry().getCoordinates()[0].slice(0)
    }
    else {
      oldFeatureCoordinates = this.state.vectorSource.getFeatures()[0].getGeometry().getCoordinates().slice(0)
    }

    if (axis == 'z') {
      //get the [x, y] array so the z can be set in the hashmap
      var xy = oldFeatureCoordinates[indexNum]

      //set() returns the hashmap so programmers can chain functions
      var newCoordsHashmap = coordsHashmap.set(xy, newXYZCoordsDict[index])

      this.setState({
        coordsHashmap: newCoordsHashmap,
        fullWKT: this.getFullWKT(this.state.wkt)
      })
    }
    else {
      var oldXY = oldFeatureCoordinates[indexNum].slice(0) //grab a copy of the old [X,Y] pair so Z can be retrieved
      if(axis == 'x') {
        oldFeatureCoordinates[indexNum][0] = newXYZCoordsDict[index] //update the x value of the shallow copy's xy pair
        console.log(newXYZCoordsDict[index]);
      }
      else if (axis == 'y') {
        oldFeatureCoordinates[indexNum][1] = newXYZCoordsDict[index] //update the y value of the shallow copy's xy pair
      }
      var oldZ = coordsHashmap.get(oldXY)
      coordsHashmap.remove(oldXY).set(oldFeatureCoordinates[indexNum], oldZ)

      //set the feature/geometry's coordinates
      if(this.props.shape == 'Polygon') {
        oldFeatureCoordinates = [oldFeatureCoordinates]
      }
      var feature = this.state.vectorSource.getFeatures()[0]
      feature.getGeometry().setCoordinates(oldFeatureCoordinates)

      //set the wkt field, and create the fullwkt (fullwkt is wkt with z axis)
      return this.setFeatureToWKT(feature)
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
      var wkt = `POINT (${center[0]} ${center[1]})`;

      var fullWKT = this.getFullWKT(wkt)
      this.setState({
        wkt: wkt,
        radius: radius,
        fullWKT: fullWKT,
      });
      this.props.handleOnChange(this.attr, fullWKT)
      this.props.handleOnChange('radius', radius)
    }
    else {
      var wkt = new WKT().writeFeature(feature);
      var fullWKT = this.getFullWKT(wkt)
      this.setState({
        wkt: wkt,
        fullWKT: fullWKT
      });
      this.props.handleOnChange(this.attr, fullWKT)

    }
  }

  getFullWKT(wkt) {
    if (wkt == this.wktEmpty) {
      this.props.handleOnChange(this.attr, wkt)
      return wkt
    }

    if (this.props.shape == 'Circle')
    {
      var lastParen = wkt.length-1
      var featureCoords = this.state.vectorSource.getFeatures()[0].getGeometry().getCenter()
      var zCoord = this.state.coordsHashmap.get(featureCoords) || 0;
      var fullWKT = [wkt.slice(0, lastParen), (' ' + zCoord), wkt.slice(lastParen)].join('')
      return fullWKT;
    }

    var wktSplit = wkt.split(',')
    var fullWKT = wktSplit[0] //initialize with the first part of the wkt to avoid fence post problem
    var featureCoords = this.state.vectorSource.getFeatures()[0].getGeometry().getCoordinates()

    var isPolygon = this.props.shape == 'Polygon';
    if (isPolygon) {
      featureCoords = featureCoords[0]
    }
    for (let i = 0; i < featureCoords.length-1; i++) {
      var zCoord = (this.state.coordsHashmap.get(featureCoords[i]) || 0);
      fullWKT += ' ' + zCoord  + ',' + wktSplit[i+1]
    }


    var lastParen = fullWKT.length-1
    //insert the last zCoord into the string
    if (isPolygon) {
      lastParen = fullWKT.length-2
      //polygons have the same zCoord for first and last coord
      var zCoord = this.state.coordsHashmap.get(featureCoords[0]) || 0;
      fullWKT = [fullWKT.slice(0, lastParen), (' ' + zCoord), fullWKT.slice(lastParen)].join('')
    }
    else {
      var zCoord = this.state.coordsHashmap.get(featureCoords[featureCoords.length-1]) || 0;
      fullWKT = [fullWKT.slice(0, lastParen), (' ' + zCoord), fullWKT.slice(lastParen)].join('')
    }
    return fullWKT;
  }

  shapeLabeler(format) {
    if(this.props.shape == 'Polygon') {
      return `(${format})`
    }
    return format;
  }

  deleteFeatures() {
    if(this.map) {
      this.state.vectorSource.clear()
      this.state.coordsHashmap.clear()
      this.props.handleOnChange(this.attr, this.wktEmpty)
      this.setState({
        wkt: this.wktEmpty,
        fullWKT: this.wktEmpty
      })
    }
  }

  render() {
    var opts = this.props.opts;
    var field = opts['field'];
    var style = opts['style'];
    var label = opts['label'];
    var attr = opts['attr'];

    var self = this;

    var feature = this.state.vectorSource.getFeatures()[0]
    return (
      <table width="100%" className={this.props.css}>
        <tbody>
          <tr>
            <td width="100%">
              <TextField
                id={this.field.key}
                fullWidth={true}
                label={this.props.showLabels ? label + " - " + this.props.shape + this.shapeLabeler(" (x1 y1 z1, x2 y2 z2, ...)") : null}
                underlineShow={this.props.showLabels}
                style={style.root}
                value={this.field[this.attr]}
                readOnly={true}
                defaultValue={this.field.defaultValue}
              />
            </td>
            <td width="30px">
              <DialogMap
                feature={feature}
                configureMap={this.configureMap}
                uniqueID={this.field.key}
                shape={this.props.shape}
                handleHashmap={this.handleHashmap}
                updateFeature={this.updateFeature}
                coordsHashmap={this.state.coordsHashmap}
                textStyle={style.root}
                textInput={
                  (this.props.shape === 'Circle' &&
                      <TextField
                        id={'radius' + this.field.key}
                        fullWidth={true}
                        label={"Circle - RADIUS"}
                        underlineShow={this.props.showLabels}
                        style={style.root}
                        value={this.state.radius}
                        defaultValue={field.defaultValue}
                        onFocus={this.handleInputFocus}
                        onChange={(e) => this.handleInputChange(e, 'radius')}
                        onBlur={this.handleRadiusBlur.bind(this)}
                      />
                    )

              }/>
            </td>
          </tr>
        </tbody>
      </table>
    );
  }
  //  // text input for editing wkt, removed from all map fields
  // <TextField
  //   id={'center' +   this.props.uniqueID}
  //   fullWidth={true}
  //   floatingLabelText={"Circle - POINT (x1 y1 z1)"}
  //   underlineShow={this.props.showLabels}
  //   style={style.root}
  //   value={this.state.wkt}
  //   defaultValue={field.defaultValue}
  //   onFocus={this.handleInputFocus}
  //   onChange={this.handleInputChange}
  //   onBlur={this.handleInputBlur}
  // />
  // (this.props.shape != 'Circle' &&
  //   <TextField
  //     id={field.key}
  //     fullWidth={true}
  //     floatingLabelText={label + " - " + self.props.shape.toUpperCase() + this.shapeLabeler(" (x1 y1 z1, x2 y2 z2, ...)")}
  //     underlineShow={this.props.showLabels}
  //     style={style.root}
  //     value={this.state.wkt}
  //     defaultValue={field.defaultValue}
  //     onFocus={this.handleInputFocus}
  //     onChange={this.handleInputChange}
  //     onBlur={this.handleInputBlur}
  //   />)
}
