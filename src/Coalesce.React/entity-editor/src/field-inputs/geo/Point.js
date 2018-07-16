import React from 'react';
import MapPoint from './MapPoint.js'
import Map from 'ol/Map';
import VectorSource from 'ol/source/Vector';
import Collection from 'ol/Collection';
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
import HashMap from 'hashmap/hashmap'
import {defaults as defaultControls} from 'ol/control'
import {defaults as defaultInteractions} from 'ol/interaction'


export default class Point extends React.Component {

  constructor(props) {
    super(props);

    this.wktEmpty = 'POINT EMPTY'

    this.opts = this.props.opts;
    this.attr = this.opts['attr'];
    this.field = this.opts['field'];

    if(this.field[this.attr]) {

      this.state = {
        wkt: this.stripZAxis(this.field[this.attr]),
        fullWKT: this.field[this.attr]
      };

    }
    else {
      this.state = {
        wkt: this.wktEmpty,
        fullWKT: this.wktEmpty
      };
    }
  }

  stripZAxis(fullWKT) {
    var pattern = / ?-?[0-9]\d*(\.\d+)?/g //matches the point's 3 values
    var coords = fullWKT.match(pattern) //[x, y, z]
    this.coordsHashmap = new HashMap()
    this.coordsHashmap.set([parseFloat(coords[0]), parseFloat(coords[1])], parseFloat(coords[2]))
    console.log(this.coordsHashmap);
    var wkt = 'POINT(' + coords[0] + ' ' + coords[1] + ')'

    return wkt
  }

  handlePoint(feature, self, that) {
    var formatted =  new WKT().writeFeature(new Feature({geometry: feature.getGeometry()}));

    var fullWKT = that.getFullWKT(formatted)

    this.props.handleOnChange(this.attr, fullWKT)

    self.setState({
      wkt: formatted,
      fullWKT: fullWKT
    });
  }

  handleChangeFeature(self, that) {
    var features = that.state.vectorSource.getFeatures();
    var formatted = 'POINT EMPTY'
    var fullWKT = ''
    if(features.length == 0) {
      fullWKT = that.getFullWKT(formatted)
    }
    else {
      formatted = new WKT().writeFeature(new Feature({geometry: features[0].getGeometry()}));
      console.log(formatted);
      fullWKT = that.getFullWKT(formatted)
    }

    this.props.handleOnChange(this.attr, fullWKT)

    self.setState({
      wkt: formatted,
      fullWKT: fullWKT
    });

  }

  handleInput(that) {

    that.setState({visibility: 'hidden'});
    var input = document.getElementById(this.field.key).getAttribute('value')
    var feature = new WKT().readFeature(input)

    var point = feature.getGeometry()

    var coord = point.getCoordinates()
    this.moveFeature(coord, that);
  }

  moveFeature(coord, that) {
    var feature = that.state.vectorSource.getFeaturesCollection().item(0)
    if (feature) {
      feature.getGeometry().setCoordinates(coord)
      return feature
    }
    else {
      that.createFeature(coord)
    }
  }

  clickEvt(evt, that) {
    var features = [];
    that.map.forEachFeatureAtPixel(evt.pixel,
      function(feature, layer) {
        features.push(feature);
      }
    );

    if (features.length === 1)
    {
      var lonLat = features[0].getGeometry().getCoordinates()
      var coordinates = that.convertCoordinates(lonLat)
      features[0].setId('clicked')
      that.setState({visibility: "visible"})
      that.map.getOverlays().item(0).setPosition(lonLat);
    }
    else if (features.length === 0)
    {
      if (that.map.getOverlays().item(0).getPosition() === undefined) {
        var feature = that.state.vectorSource.getFeaturesCollection().item(0)
        if (feature) {
          feature.getGeometry().setCoordinates(evt.coordinate)
          return feature
        }
        else {
          return that.createFeature(evt.coordinate);
        }
      }
      else {
        that.map.getOverlays().item(0).setPosition(undefined);
        that.state.vectorSource.getFeatureById('clicked').setId('')
        that.setState({visibility: "hidden"})
      }
    }

  }



  render() {
    const uniqueID = Date.now()

    var clickEvt = this.clickEvt
    var parent = this
    return(
      <MapPoint
        handleOnChange={this.props.handleOnChange}
        clickEvt={clickEvt}
        opts={this.props.opts}
        uniqueID={uniqueID}
        showLabels={this.props.showLabels}
        wkt={this.state.wkt}
        wktEmpty={this.wktEmpty}
        parent={parent}
        coordsHashmap={this.coordsHashmap}
        shape='POINT'/>
    )
  }

}
