import React from 'react';
import MapPoint from './MapPoint.js'
import * as ol from 'openlayers'

export default class Multipoint extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      wkt: "MULTIPOINT EMPTY"
    };

    this.multipoint = new ol.geom.MultiPoint();

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
        var test = that.createFeature(evt.coordinate);
        return test
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
    //that.getWKT(self.multipoint)
    var formatted =  new ol.format.WKT().writeFeature(new ol.Feature({geometry: self.multipoint}), {
      decimals: 5
    });
    self.setState({wkt: formatted})
  }

  handleDelete(self, that) {
    self.multipoint = new ol.geom.MultiPoint();
    var features = that.state.vectorSource.getFeaturesCollection();
    var formatted = 'MULTIPOINT EMPTY'
    if (features.getLength() > 0) {

      features.forEach(function(elem, i, arr) {
        self.multipoint.appendPoint(elem.getGeometry());
      })

      formatted =  new ol.format.WKT().writeFeature(new ol.Feature({geometry: self.multipoint}), {
        decimals: 5
      });

    }
    self.setState({wkt: formatted});

  }

  handleInput(that) {
    var opts = this.props.opts;
    var field = opts['field'];

    var input = document.getElementById(field.key).getAttribute('value')
    var feature = new ol.format.WKT().readFeature(input)
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
      <MapPoint clickEvt={clickEvt} opts={this.props.opts} uniqueID={uniqueID} showLabels={this.props.showLabels} wkt={parent.state.wkt} parent={parent} shape='MULTIPOINT'></MapPoint>
    )
  }
}
