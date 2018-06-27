import React from 'react';
import TextField from 'material-ui/TextField';
import MapMaker from '../../Map.js';
import * as ol from 'openlayers'
import Modal from 'react-responsive-modal';
import 'openlayers/css/ol.css';
var mgrs = require('mgrs');


export default class Shape extends React.Component {

  constructor(props) {
    super(props);
    this.wktEmpty = (this.props.shape == 'Circle' ? 'POINT' : this.props.shape.toUpperCase())  + ' EMPTY';
    this.state = {
      vectorSource: new ol.source.Vector({
        wrapX: false,
        features: new ol.Collection(),
      }),
      wkt: this.wktEmpty,
      radius: '0',
    };

    this.handleInputFocus = this.handleInputFocus.bind(this)
    this.handleInputBlur = this.handleInputBlur.bind(this)
    this.handleInputChange = this.handleInputChange.bind(this)

  }

  componentDidMount() {
    this.configureMap()
  }

  configureMap() {
    var self = this;

    var vectorLayer = new ol.layer.Vector({
      name: 'markers',
      source: this.state.vectorSource
    });
    //null for defaults
    var layers = [
      new ol.layer.Tile({
        source: new ol.source.OSM({
          wrapX: false,
        })
      }),
      vectorLayer
    ];
    var overlays = null;
    var maker = new MapMaker(layers, overlays, null,  'map' + this.props.uniqueID)
    this.map = maker.getMap();

    var drawInteraction = new ol.interaction.Draw({
      source: this.state.vectorSource,
      type: this.props.shape
    })

    drawInteraction.on('drawstart', function(evt) {
      self.handleDrawstart(self);
    })
    this.map.addInteraction(drawInteraction)

    //uncomment below for modifying. initializing the below variable
    //  stops you from doing a this.state.vectorSource.clear()
    // this.modifyInteraction = new ol.interaction.Modify({
    //   source: this.state.vectorSource
    // })
    // this.modifyInteraction.setActive(true)
    // this.map.addInteraction(this.modifyInteraction)


    this.state.vectorSource.on('addfeature', function(evt) {
      self.handleAddfeature(self, evt.feature)
    });



    return this.map;
  }



  handleDrawstart(self) {
    self.state.vectorSource.clear()
    self.setState( {wkt: self.wktEmpty})
    if (this.props.shape == 'Circle') {
      self.setState( {radius: 'None'} )
    }
  }

  handleAddfeature(self, feature) {
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
      var wkt = new ol.format.WKT().writeFeature(feature);
      self.setState( {wkt: wkt} );

    }

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
      if (this.state.wkt == NOTHING) {
        this.setState({
          wkt: this.wktEmpty,
          radius: '0'
        })
        this.state.vectorSource.clear()
      }
      else {
        if (this.props.shape == 'Circle') {
          const ZERO = '0';
          var vecSource = this.state.vectorSource
          var circleExisted = true;

          //read the center point input as a Feature (wkt -> Feature)
          //  get its geometry (Point)
          //  get coordinates from geometry ( [x, y] format )
          var centerCoords = new ol.format.WKT().readFeature(this.state.wkt).getGeometry().getCoordinates();

          //if the radius from text field is '', set it to '0'
          var radiusAsString = this.state.radius || ZERO;
          var radiusAsNum = 0;
          console.log(radiusAsString);
          if(!isNaN(radiusAsString)) {
            //if the radius IS a string/number
            //turn string to number
            radiusAsNum = parseFloat(radiusAsString);
            console.log(radiusAsString);
            console.log(radiusAsNum);

            if (this.state.radius == NOTHING && radiusAsString == ZERO) {
              //if the radius was '' and is therefore turned into '0',
              //    update the text field to be '0' instead of '' (for aesthetics)
              //this won't set the text field to '0' if it was already a number
              console.log('set');
              this.setState({radius: radiusAsString})
            }
          }
          else {
            throw new TypeError("Radius must be a number!")
          }

          //get ol.Collection of features
          var featuresCollection = vecSource.getFeaturesCollection()

          //if there are no features/circles, make a new one at [0, 0] with radiusAsNum
          if (featuresCollection.getLength() == 0) {
            circleExisted = false;
            var feat = new ol.Feature({
              geometry: new ol.geom.Circle([0, 0], radiusAsNum)
            });
            vecSource.addFeature(feat)
          }
          else{ //if circle already exists update it
            //get the Feature from the collection
            var circle = vecSource.getFeaturesCollection().item(0)
            //get the Geometry form the collection (ol.geom.Circle)
            circle = circle.getGeometry()
            //set circle's center and radius
            circle.setCenterAndRadius(centerCoords, radiusAsNum);
          }
        }
        else {
          var opts = this.props.opts;
          var field = opts['field'];

          var inputElem = document.getElementById(field.key)
          var feature = new ol.format.WKT().readFeature(inputElem.value)

          this.state.vectorSource.clear()
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
    if(this.props.shape == 'Polygon') {

    }
    return (
      <div>

        {this.props.shape == 'Circle' &&
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
          </div>
        }

        {this.props.shape != 'Circle' &&
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
          />
        }

        <button type="button" onClick={this.handleOpen}>{this.props.shape}</button>

        <div id={'map' + this.props.uniqueID} className="map"></div>

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
