import 'ol/ol.css';
import Map from 'ol/Map';
import VectorSource from 'ol/source/Vector';
import Collection from 'ol/Collection';
import Point from 'ol/geom/Point';
import Feature from 'ol/Feature';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import toStringHDMS from 'ol/coordinate';
import VectorLayer from 'ol/layer/Vector';
import TileLayer from 'ol/layer/Tile';
import Overlay from 'ol/Overlay';
import OSM from 'ol/source/OSM';
import Draw from 'ol/interaction/Draw';
import Modify from 'ol/interaction/Modify';
import WKT from 'ol/format/WKT';
import View from 'ol/View';
import Circle from 'ol/geom/Circle';
import {defaults as defaultControls, Control} from 'ol/control'
import ScaleLine from 'ol/control/ScaleLine';
import Zoom from 'ol/control/Zoom';
import Rotate from 'ol/control/Rotate';
import Attribution from 'ol/control/Attribution';
import MousePosition from 'ol/control/MousePosition';


import {defaults as defaultInteractions} from 'ol/interaction'

export default class MapMaker {

  //optional parameters
  constructor(layers, overlays, interactions, controls, targetID) {
    const lonLat = 'EPSG:4326';
    const mousePosClassNames = 'ol-unselectable ol-scale-line-inner ol-scale-line mouse-pos-control'
    var controlsArray = controls.concat(
      [
        new ScaleLine(),
        //new ol.control.OverviewMap(),
        new Zoom(),
        new Rotate(),
        new Attribution({
          attributionOptions: {
            collapsible: false
            }
          }),
        new MousePosition( {className: mousePosClassNames})
      ]);

    this.map = new Map({
      overlays: overlays || [],
      controls: controlsArray,
      layers: layers ||
      [
        new TileLayer({
          source: new OSM({
            wrapX: false,
          })
        })
      ],
      target: targetID || 'map',
      view: new View({
        projection: lonLat,
        center: [0, 0],
        zoom: 4,
        minZoom: 1
      }),
      interactions: interactions || defaultInteractions()
    });
  }


  getMap() {
    return this.map
  }



}
