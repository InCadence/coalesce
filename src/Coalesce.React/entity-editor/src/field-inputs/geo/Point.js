import React from 'react';
import MapPoint from './MapPoint.js'
import * as ol from 'openlayers'


export default class Point extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      map: this.props.map,
      createFeature: this.props.createFeature,
      deleteFeature: this.props.deleteFeature,
      wkt: 'POINT EMPTY',
    }
  }

  handlePoint(feature, self, that) {
    var formatted =  new ol.format.WKT().writeFeature(new ol.Feature({geometry: feature.getGeometry()}), {
      decimals: 5
    });
    self.setState({wkt: formatted})
  }

  handleDelete(self, that) {
    var formatted = 'POINT EMPTY'
    self.setState({wkt: formatted});

  }

  handleInput(that) {
    var opts = this.props.opts;
    var field = opts['field'];
    that.setState({visibility: 'hidden'});
    var input = document.getElementById(field.key).getAttribute('value')
    var feature = new ol.format.WKT().readFeature(input)

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
        console.log('hidden');
      }
    }

  }



  render() {
    const uniqueID = Date.now()

    var clickEvt = this.clickEvt
    var parent = this
    return(
      <MapPoint clickEvt={clickEvt} opts={this.props.opts} uniqueID={uniqueID} showLabels={this.props.showLabels} wkt={this.state.wkt} parent={parent} tag='POINT'></MapPoint>
    )
  }

}
