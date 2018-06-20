import React from 'react';
import Dialog from 'material-ui/Dialog';
import Map from 'ol/map';
import Tile from 'ol/layer/tile';
import View from 'ol/view';
import OSM from 'ol/source/osm';
import Feature from 'ol/feature';
import Point from 'ol/geom/point';
import {default as VectorLayer} from 'ol/layer/vector';
import {default as VectorSource} from 'ol/source/vector';
import FullScreen from 'ol/control/fullscreen';
import coordinate from 'ol/coordinate';
import proj from 'ol/proj';
import Overlay from 'ol/overlay';
import styles from './popup.css';

var mgrs = require('mgrs');

export default class Multipoint extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      map: this.props.map,
      createFeature: this.props.createFeature,
    }
  }

  componentDidMount() {
    var map = this.state.map;

    map.on('click', function(evt) {
    //if overlay is not open, place a marker if not clicked on a marker
    //if a marker WAS clicked, open overlay and don't place a marker

      var features = [];
      map.forEachFeatureAtPixel(evt.pixel,
        function(feature, layer) {
          features.push(feature);
        }
      );

      if (features.length === 1)
      {
        var lonLat = features[0].getGeometry().getCoordinates()
        var coordinates = this.props.convertCoordinates(lonLat)
        features[0].setId('clicked' + this.props.uniqueID)
        map.getOverlays().item(0).setPosition(lonLat);
      }
      else if (features.length === 0)
      {
        if (map.getOverlays().item(0).getPosition() === undefined) {
            this.state.createFeature(evt.coordinate);
        }
        else {
          map.getOverlays().item(0).setPosition(undefined);
        }
      }
    });
  }
}
