import * as React from "react";
import * as ol from 'openlayers';
import {FeatureSelection} from './featureselection.js'
import SlidingPane from 'react-sliding-pane';
import 'react-sliding-pane/dist/react-sliding-pane.css';

// Map Controls
import 'openlayers/css/ol.css';

export class MapView extends React.Component {

  constructor(props) {
    super(props);

    this.state = props;
  }

  // Create Open Layer Map
  componentDidMount() {

    // This is required to be able to add the control
    ol.inherits(createOptionControl, ol.control.Control);

    //Creates Map
    let map = new ol.Map({
      target: 'map',
      view: new ol.View({
        center: [5451297.56868106,4897413.82890589],
        zoom: 4
      }),
      controls: ol.control.defaults({
          attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
            collapsible: false
          })
        }).extend([
          new createOptionControl(this)
        ]),
    });

    // Add Map Layer
    map.addLayer(this.state.mapLayer);

    // Saves State
    this.setState({
      map: map
    });
  }

  // Changes the base maps
  changeMapLayerCallback(source) {

    const {map, mapLayer} = this.state;

    map.removeLayer(mapLayer);
    map.getLayers().insertAt(0, source);

    this.setState({
      mapLayer: source
    })

  }

  // Callback to handling adding layers to the map
  addLayerCallBack(feature) {
    this.changeMapLayerCallback(this.state.mapLayer);
    const {map, singleWMSLayer} = this.state;

    var layer;

    switch(feature.type) {
      case 'WMS':
        // Single WMS Layer?
        if (this.state.singleWMSLayer.enabled) {
          // Remove Old WMS Layer
          if (singleWMSLayer.layer != null) {
            map.removeLayer(singleWMSLayer.layer);
          }

          // Add Layer
          singleWMSLayer.layers.push(feature.name);

          // Create New Combined WMS Layer
          singleWMSLayer.layer = createBDPWMSLayer(this.props.geoserver, this.props.workspace, singleWMSLayer.layers);
          layer = singleWMSLayer.layer;
        } else {
          feature.layer = createBDPWMSLayer(this.props.geoserver, this.props.workspace, [feature.name]);
          layer = feature.layer;
        }
        break;
      case 'HEATMAP':
        feature.layer = createBDPHeatmapLayer(this.props.geoserver, this.props.workspace, feature.name);
        layer = feature.layer;
        break;
      case 'WFS':
      default:
        feature.layer = createBDPWFSLayer(this.props.geoserver, this.props.workspace, feature.name, feature.style);

        layer = feature.layer;
        break;
    }

    map.addLayer(layer);
  }

  // Callback to handling removing layers from the map
  rmvLayerCallBack(feature) {
    const {map, singleWMSLayer} = this.state;
    if (singleWMSLayer.enabled && feature.type === 'WMS') {

      if (singleWMSLayer.layer != null) {

        var idx = singleWMSLayer.layers.indexOf(feature.name);

        if (idx !== -1) {

          // Remove WMS Layer
          map.removeLayer(singleWMSLayer.layer);

          // Prune Layer
          singleWMSLayer.layers.splice(idx, 1);

          // Additional Layers?
          if (singleWMSLayer.layers.length >= 1) {
            // Yes; Create WMS Layer
            singleWMSLayer.layer = createBDPWMSLayer(this.props.geoserver, this.props.workspace, singleWMSLayer.layers);
            map.addLayer(singleWMSLayer.layer);
          }
        }

      }
    } else {
      if (feature.layer != null) {
        map.removeLayer(feature.layer);
        //feature.layer.setVisible(false);
      }
    }

  }

  render(){

    console.log("Render Map");

    return (
      <div className="row">
        <SlidingPane
          isOpen={ this.state.isOptionsOpen }
          title='Options'
          from='left'
          width='300px'
          onRequestClose={ () => this.setState({ isOptionsOpen: false }) }>
            <div className="ui-widget">
              <label className="ui-widget-header ui-button">Layers</label>
              <FeatureSelection
                addfeature={(feature) => this.addLayerCallBack(feature)}
                rmvfeature={(feature) => this.rmvLayerCallBack(feature)}
                availableLayers={this.state.availableLayers}
                styles={this.state.styles}
                selectedLayers={this.state.selectedLayers}/>
            </div>
        </SlidingPane>
        <div id="map"> </div>
      </div>
    );
  }

}

// This creates the map control to open the options pane
var createOptionControl = function(view, opt_options) {

  var options = opt_options || {};

  var button = document.createElement('button');
  button.innerHTML = '...';

  var handleRotateNorth = function() {
    view.setState({ isOptionsOpen: true });
  };

  button.addEventListener('click', handleRotateNorth, false);
  button.addEventListener('touchstart', handleRotateNorth, false);

  var element = document.createElement('div');
  element.className = 'options-button ol-unselectable ol-control';
  element.appendChild(button);

  ol.control.Control.call(this, {
    element: element,
    target: options.target
  });
};

// Creates a client side heatmap layer
function createBDPHeatmapLayer(url, workspace, layer) {
  return new ol.layer.Heatmap({
    source: new ol.source.Vector({
      format: new ol.format.GeoJSON({
        options: {
          defaultDataProjection: 'EPSG:4326',
          featureProjection: 'EPSG:4326',
        }
      }),
      url: function(extent) {
        return url + '/wfs?service=WFS&' +
            'version=2.0.0&request=GetFeature&typename=' + workspace + ':' + layer + '&' +
            'outputFormat=application/json&srsname=EPSG:3857';// +
            //'&bbox=' + extent.join(',');
      },
      strategy: ol.loadingstrategy.bbox
    }),
    radius: 5
  });
}

function createBDPWMSLayer(url, workspace, layers) {

  var workingLayers = layers.slice();

  for (var ii=0; ii<layers.length; ii++) {
      workingLayers[ii] = workspace + ":" + layers[ii];
  }

  return new ol.layer.Tile({
    source: new ol.source.TileWMS({
      params: {
        LAYERS: workingLayers.join(","),
        //TILED: true,
        //STYLES: 'heatmap'
      },
      serverType: 'geoserver',
      url:  url + '/wms',
      strategy: ol.loadingstrategy.bbox
    }),
  });
}

function createBDPWFSLayer(url, workspace, feature, style) {
  return new ol.layer.Vector({
    source: new ol.source.Vector({
      format: new ol.format.GeoJSON({
        options: {
          defaultDataProjection: 'EPSG:4326',
          featureProjection: 'EPSG:4326',
        }
      }),
      url: function(extent) {
        return url + '/wfs?service=WFS&' +
            'version=2.0.0&request=GetFeature&typename=' + workspace + ':' + feature + '&' +
            'outputFormat=application/json&srsname=EPSG:4326';// + '&bbox=' + extent.join(',');
      },
      strategy: ol.loadingstrategy.bbox
    }),
    style: style
  });
}

MapView.defaultProps = {
  isOptionsOpen: true,
  singleWMSLayer: {
    enabled: false,
    layers: []
  },
  mapLayer: new ol.layer.Tile({
      source: new ol.source.OSM()
    })

}
