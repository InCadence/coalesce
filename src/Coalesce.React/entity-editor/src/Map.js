import Tile from 'ol/layer/tile';
import View from 'ol/view';
import OSM from 'ol/source/osm';
import FullScreen from 'ol/control/fullscreen';
import Map from 'ol/map';
import control from 'ol/control';
import Draw from 'ol/interaction/draw';
import interaction from 'ol/interaction';


export default class MapMaker {

  //optional parameters
  constructor(layers, overlays, interactions, targetID) {
    const lonLat = 'EPSG:4326';

    this.source =
    this.map = new Map({
      overlays: overlays || [],
      controls: control.defaults(),
      layers: layers ||
      [
        new Tile({
          source: new OSM({
            wrapX: false,
          })
        })
      ],
      target: targetID || 'map',
      view: new View({
        projection: lonLat,
        center: [0, 0],
        zoom: 3,
        minZoom: 3
      }),
      interactions: interactions || interaction.defaults()
    });
  }

  getSource() {
    return this.source;
  }

  getMap() {
    return this.map
  }

}
