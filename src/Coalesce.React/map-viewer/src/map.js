import * as React from "react";
import * as ol from 'openlayers';
import {FeatureSelection} from './featureselection.js'

// Info Button
import 'openlayers/css/ol.css';

export class MapView extends React.Component {

  constructor(props) {
    super(props);

    this.state = props;
  }

  componentDidMount() {

    let map = new ol.Map({
      target: 'map',
      view: new ol.View({
        center: [5451297.56868106,4897413.82890589],
        zoom: 4
      })
    });

    map.addLayer(new ol.layer.Tile({
      source: new ol.source.OSM()
    }));

    this.setState({map: map});
    this.setState({layers: []});
  }

  createBDPVectorLayer(hostname, workspace, feature, style) {
    return new ol.layer.Vector({
      source: new ol.source.Vector({
        format: new ol.format.GeoJSON({
          options: {
            defaultDataProjection: 'EPSG:4326',
            featureProjection: 'EPSG:4326',
          }
        }),
        url: function(extent) {
          return hostname + '/' + workspace + '/wfs?service=WFS&' +
              'version=2.0.0&request=GetFeature&typename=' + workspace + ':' + feature + '&' +
              'outputFormat=application/json&srsname=EPSG:3857';// +
              //'&bbox=' + extent.join(',');
        },
        strategy: ol.loadingstrategy.bbox
      }),
      style: style
    });
  }

  addFeature(name, style) {
    var map = this.state.map;
    var layers = this.state.layers;
    var layer = this.createBDPVectorLayer(this.props.geoserver, this.props.workspace, name, style);

    map.addLayer(layer);
    layers[name] = layer;

    this.setState({map: map});
    this.setState({layers: layers});
  }

  rmvFeature(name) {
    var map = this.state.map
    var layer = this.state.layers;

    if (layer[name] != null) {
      map.removeLayer(layer[name]);

      this.setState({map: map});
    }

  }

  render(){
    return (
      <div className="row">
        <div className="col-sm-2"><FeatureSelection geoserver={this.props.geoserver} workspace={this.props.workspace} addfeature={(name, style) => this.addFeature(name, style)} rmvfeature={(name) => this.rmvFeature(name)}/></div>
        <div className="col-sm-10" id="map"> </div>
      </div>
    );
  }
}
