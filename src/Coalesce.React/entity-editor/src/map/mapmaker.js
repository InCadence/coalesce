import * as ol from 'openlayers'
import 'openlayers/css/ol.css';


export default class MapMaker {

  //optional parameters
  constructor(layers, overlays, interactions, targetID) {
    const lonLat = 'EPSG:4326';

    this.map = new ol.Map({
      overlays: overlays || [],
      controls: ol.control.defaults(),
      layers: layers ||
      [
        new ol.layer.Tile({
          source: new ol.source.OSM({
            wrapX: false,
          })
        })
      ],
      target: targetID || 'map',
      view: new ol.View({
        projection: lonLat,
        center: [0, 0],
        zoom: 3,
        minZoom: 3
      }),
      interactions: interactions || ol.interaction.defaults()
    });
  }


  getMap() {
    return this.map
  }

}
