import Tile from 'ol/layer/tile';
import View from 'ol/view';
import OSM from 'ol/source/osm';
import FullScreen from 'ol/control/fullscreen';
import Map from 'ol/map';
import control from 'ol/control';


export default class MapMaker {

  //optional parameters
  constructor(layers, overlays, targetID) {
    const lonLat = 'EPSG:4326';
    this.map = new Map({
      overlays: overlays || [],
      controls: control.defaults(),
      layers: layers ||
      [
        new Tile({
          source: new OSM()
        })
      ],
      target: targetID || 'map',
      view: new View({
        projection: lonLat,
        center: [0, 0],
        zoom: 3,
        minZoom: 3
      }),
    });
  }

  getMap() {
    return this.map
  }

}
