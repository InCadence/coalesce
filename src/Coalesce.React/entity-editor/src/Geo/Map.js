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
import coordinate from 'ol/coordinate';
import proj from 'ol/proj';
import Overlay from 'ol/overlay';
import Map from 'ol/map';


export default class MapMaker {

  //optional parameters
  constructor(layers, overlays, targetID) {
    const lonLat = 'EPSG:4326'
    this.map = new Map({
      view: new View({
        projection: lonLat,
        center: [0, 0],
        zoom: 1,
        minZoom: 2
      }),
      layers: layers || [new Tile({source: new OSM()})],
      overlays: overlays || [],
      target: 'map'
    });
    this.map.addControl(new FullScreen());

    return [this.map];
  }

}
