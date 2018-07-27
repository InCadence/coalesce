import React from 'react';
import MapPoint from './MapPoint.js'
import Feature from 'ol/Feature';
import WKT from 'ol/format/WKT';
import HashMap from 'hashmap/hashmap'
import MultiPoint from 'ol/geom/MultiPoint';

export default class Multipoint extends React.Component {

  constructor(props) {
    super(props);

    this.wktEmpty = "MULTIPOINT EMPTY"

    this.opts = this.props.opts;
    this.attr = this.opts['attr'];
    this.field = this.opts['field'];

    if(this.field[this.attr] && this.field[this.attr] !== '') {

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

    this.multipoint = new WKT().readFeature(this.state.wkt).getGeometry();
    console.log('hello');
    console.log(this.state.wkt);
  }

  componentWillReceiveProps(newProps) {
    this.opts = newProps.opts;
    this.attr = this.opts['attr'];
    this.field = this.opts['field'];

    if(this.field[this.attr] && this.field[this.attr] !== '') {

      this.setState({
        wkt: this.stripZAxis(this.field[this.attr]),
        fullWKT: this.field[this.attr]
      });

    }
    else {
      this.setState({
        wkt: this.wktEmpty,
        fullWKT: this.wktEmpty
      });
    }
    this.multipoint = new WKT().readFeature(this.state.wkt).getGeometry();
    console.log(this.multipoint);
  }

  stripZAxis (fullWKT) {
    var pattern = / ?-?[0-9]\d*(\.\d+)?/g

    var coords = fullWKT.match(pattern)
    this.coordsHashmap = new HashMap()
    var coordPos = 1;

    var wkt = 'MULTIPOINT((' + coords[0] + ' ' + coords[1] + ')'
    this.coordsHashmap.set([parseFloat(coords[0]), parseFloat(coords[1])], parseFloat(coords[2]) )

    for (let i = 3; i < coords.length; i++) {
      if (coordPos === 1) {
        wkt += ',(' + coords[i]
        this.coordsHashmap.set([parseFloat(coords[i]), parseFloat(coords[i+1])], parseFloat(coords[i+2]) )
        coordPos++;
      }
      else if (coordPos === 2) {
        wkt += ' ' + coords[i] + ')'
        coordPos++;
      }
      else if ( coordPos === 3) {
        coordPos = 1;
        continue
      }
    }
    wkt += ')'
    return wkt

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
      //var coordinates = that.convertCoordinates(lonLat)
      features[0].setId('clicked')
      //that.setState({visibility: "inline-block"})
      that.convertCoordinates(lonLat)
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
        that.setState({visibility: "none"})
      }
    }
  }

  handlePoint(feature, self, that) {
    that.setState({visibility: 'none'});
    self.multipoint.appendPoint(feature.getGeometry());

    var formatted =  new WKT().writeFeature(new Feature({geometry: self.multipoint}));

    var fullWKT = that.getFullWKT(formatted)

    this.props.handleOnChange(this.attr, fullWKT)

    self.setState({
      wkt: formatted,
      fullWKT: fullWKT
    })
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

    this.props.handleOnChange(this.attr, fullWKT)

    self.setState({
      wkt: formatted,
      fullWKT: fullWKT
    });

  }

  handleInput(that) {

    var input = document.getElementById(this.field.key).getAttribute('value')
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
        handleOnChange={this.props.handleOnChange}
        clickEvt={clickEvt}
        opts={this.opts}
        uniqueID={uniqueID}
        showLabels={this.props.showLabels}
        wkt={this.state.wkt}
        fullWKT={this.field[this.attr]}
        wktEmpty={this.wktEmpty}
        coordsHashmap={this.coordsHashmap}
        parent={parent}
        shape='MULTIPOINT'
      />
    )
  }
}
