import React from 'react';
import Dialog from 'material-ui/Dialog';
import Map from 'ol/map';
import Tile from 'ol/layer/tile';
import View from 'ol/view';
import OSM from 'ol/source/osm';
import Feature from 'ol/feature';
import Point from 'ol/geom/point';
import Style from 'ol/style/style';
import Icon from 'ol/style/icon';
import {default as VectorLayer} from 'ol/layer/vector';
import {default as VectorSource} from 'ol/source/vector';
import FullScreen from 'ol/control/fullscreen';


export default class OLMap extends React.Component {

  constructor(props) {

    super(props);
    this.state = {
      list: (this.props.list == 'true'),
      features: this.props.value || []
    };
  }

  handleOnChange(feature) {
    const {features} = this.state;
    features.push(feature);
    this.setState({features: features});
    this.props.handleOnChange(null, this.state.features)
  }

  createFeature(coord) {
    var point = new Point(coord);
    var iconFeature = new Feature({
      geometry: point
    });
    var iconStyle = new Style({
       image: new Icon({
         anchor: [.5, 33],
         anchorXUnits: 'fraction',
         anchorYUnits: 'pixels',
         opacity: 0.75,
         src: 'https://i.imgur.com/mDsYhky.png'
       })
     })
    iconFeature.setStyle(iconStyle);

    this.handleOnChange(iconFeature)


    return iconFeature;

  }

  componentDidMount() {
    console.log('mounted')
    var self = this
    var vectorSource = new VectorSource({
      features: this.state.features
    });

    var vectorLayer = new VectorLayer({
      source: vectorSource
    });

    var map = new Map({
      view: new View({
        center: [0, 0],
        zoom: 1,
        minZoom: 2
      }),
      layers: [
        new Tile({
          source: new OSM()
        }),
        vectorLayer,

      ],
      target: 'multimap'
    });
    map.addControl(new FullScreen());

    map.on('click', function(evt) {
      if (self.state.list) {
        vectorSource.addFeature(self.createFeature(evt.coordinate));
      }
      else {
        vectorSource.clear();
        self.setState({features: []})
        vectorSource.addFeature(self.createFeature(evt.coordinate));
      }

    });


  }

  render() {
    return (
      <div id="multimap" tabIndex="0" className="map" ref="olmap"></div>
    );
  }

}
