import React from 'react';
import TextField from 'material-ui/TextField';
  import MapMaker from '../../Map.js';
import {default as VectorLayer} from 'ol/layer/vector';
import {default as VectorSource} from 'ol/source/vector';
import Feature from 'ol/feature';
import coordinate from 'ol/coordinate';
import Draw from 'ol/interaction/draw';
import OSM from 'ol/source/osm';
import Tile from 'ol/layer/tile';
import Collection from 'ol/collection';
import WKT from 'ol/format/wkt';
import Modal from 'react-responsive-modal';
import 'common-components/css/ol.css'
import 'common-components/css/mapping.css'
var mgrs = require('mgrs');


export default class Shape extends React.Component {

  constructor(props) {
    super(props);
    this.wktEmpty = this.props.shape.toUpperCase() + ' EMPTY';
    this.state = {
      vectorSource: new VectorSource({
        wrapX: false,
        features: new Collection(),
      }),
      wkt: this.wktEmpty
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

    var vectorLayer = new VectorLayer({
      name: 'markers',
      source: this.state.vectorSource
    });
    //null for defaults
    var layers = [
      new Tile({
        source: new OSM({
          wrapX: false,
        })
      }),
      vectorLayer
    ];
    var overlays = null;
    var interaction = null;
    var maker = new MapMaker(layers, overlays, interaction,  'map' + this.props.uniqueID)
    this.map = maker.getMap();
    interaction = new Draw({
      source: this.state.vectorSource,
      type: this.props.shape
    })

    interaction.on('drawstart', function(evt) {
      self.handleDrawstart(self);
    })

    this.state.vectorSource.on('addfeature', function(evt) {
      self.handleAddfeature(self, evt.feature)
    });


    this.map.addInteraction(interaction)

    return this.map;
  }

  handleDrawstart(self) {
    self.state.vectorSource.clear();
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
      var wkt = new WKT().writeFeature(feature);
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
      if (this.props.shape == 'Circle') {
        const ZERO = '0';
        const NOTHING = '';
        var vecSource = this.state.vectorSource
        var circle = vecSource.getFeaturesCollection().item(0).getGeometry()
        var centerCoords = new WKT().readFeature(this.state.wkt).getGeometry().getCoordinates();

        //if the radius from text field is '', set it to '0'
        var radiusAsString = this.state.radius || ZERO;

        if(!isNaN(radiusAsString)) {
          var radiusAsInt = parseInt(radiusAsString);
          circle.setCenterAndRadius(centerCoords, radiusAsInt);
          if (this.state.radius == NOTHING && radiusAsString == ZERO) {
            //if the radius was '' and is now '0',
            //    update the text field to be '0' instead of '' (for aesthetics)
            //this won't set the text field to '0' if it was already a number
            this.setState({radius: radiusAsString})
          }
        }
        else {
          throw new TypeError("Radius must be a number!")
        }
      }
      else {
        var opts = this.props.opts;
        var field = opts['field'];

        var inputElem = document.getElementById(field.key)
        var feature = new WKT().readFeature(inputElem.value)

        this.state.vectorSource.clear()
        this.state.vectorSource.addFeature(feature)
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

  render() {
    var opts = this.props.opts;
    var field = opts['field'];
    var style = opts['style'];
    var label = opts['label'];
    var attr = opts['attr'];

    var self = this;
    console.log(field.key);

    return (
      <div>

        {this.props.shape == 'Circle' &&
          <div>
            <TextField
              id={'center' + this.props.uniqueID}
              fullWidth={true}
              floatingLabelText={label + " - CENTER POINT"}
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
              floatingLabelText={label + " - RADIUS"}
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
            floatingLabelText={label + " - " + self.props.shape.toUpperCase() + " (x1 y1 z1, x2 y2 z2, ...)"}
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
