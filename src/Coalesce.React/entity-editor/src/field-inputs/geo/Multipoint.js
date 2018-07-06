import React from 'react';
import MapPoint from './MapPoint.js'
import Map from 'ol/Map';
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
import {defaults as defaultControls} from 'ol/control'
import {defaults as defaultInteractions} from 'ol/interaction'
import MultiPoint from 'ol/geom/MultiPoint';

export default class Multipoint extends React.Component {

  constructor(props) {
    super(props);

    this.wktEmpty = "MULTIPOINT EMPTY"

    this.state = {
      wkt: this.wktEmpty
    };
    this.multipoint = new MultiPoint();

  }

  clickEvt(evt, that) {
  //if overlay is not open, place a marker if not clicked on a marker
  //if a marker WAS clicked, open overlay and don't place a marker
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
        var point = that.createFeature(evt.coordinate);

        return point
      }
      else {
        that.map.getOverlays().item(0).setPosition(undefined);
        var clicked = that.state.vectorSource.getFeatureById('clicked')
        clicked.setId('')
        that.setState({visibility: "hidden"})
      }
    }
  }

  handlePoint(feature, self, that) {
    that.setState({visibility: 'hidden'});
    self.multipoint.appendPoint(feature.getGeometry());

    var formatted =  new WKT().writeFeature(new Feature({geometry: self.multipoint}));
    self.setState({wkt: formatted})
  }

  //also handles changes from the points table, since this
  //  method simply grabs current features and sets the mulitpoint, then creates WKT
  handleChangeFeature(self, that) {
    self.multipoint = new MultiPoint();
    var features = that.state.vectorSource.getFeaturesCollection();
    var formatted = 'MULTIPOINT EMPTY'
    if (features.getLength() > 0) {

      features.forEach(function(elem, i, arr) {
        self.multipoint.appendPoint(elem.getGeometry());
      })

      formatted =  new WKT().writeFeature(new Feature({geometry: self.multipoint}), {
        decimals: 5
      });

    }

    var fullWKT = that.getFullWKT(formatted)
    self.setState({
      wkt: formatted,
      fullWKT: fullWKT
    });

  }

  handleInput(that) {
    var opts = this.props.opts;
    var field = opts['field'];

    var input = document.getElementById(field.key).getAttribute('value')
    var feature = new WKT().readFeature(input)
    this.multipoint = feature.getGeometry();

    var vecSource = that.state.vectorSource
    vecSource.clear(true)

    var coords = this.multipoint.getCoordinates()
    for (let coord of coords) {
      that.createFeature(coord);
    }

  }

  render() {
    const uniqueID = Date.now()
    var parent = this
    var clickEvt = this.clickEvt
    return(
      <MapPoint
        clickEvt={clickEvt}
        opts={this.props.opts}
        uniqueID={uniqueID}
        showLabels={this.props.showLabels}
        wkt={this.state.wkt}
        wktEmpty={this.wktEmpty}
        parent={parent}
        shape='MULTIPOINT'
      />
    )
  }
}
