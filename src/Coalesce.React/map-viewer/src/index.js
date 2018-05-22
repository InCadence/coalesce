import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {MapView} from './map.js'
import {registerErrorPrompt} from 'common-components/lib/register.js'
import { searchComplex } from 'common-components/lib/js/searchController'
import { loadProperty } from 'common-components/lib/js/propertyController'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'
import './index.css'

registerErrorPrompt(Popup);

class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = props;
  }

fetchCapabilities(url) {

  var that = this;

  fetch(url + '/wfs?service=wfs&version=1.1.0&request=GetCapabilities')
    .then(res => res.text())
    .then(str => (new window.DOMParser()).parseFromString(str, "text/xml"))
    .then(wfsdoc => {

      fetch(url + '/ows?service=wms&version=1.1.1&request=GetCapabilities')
        .then(wmsres => wmsres.text())
        .then(wmsstr => (new window.DOMParser()).parseFromString(wmsstr, "text/xml"))
        .then(wmsdoc => {

          const {availableLayers} = this.state;

          // Clear Values
          availableLayers.slice(0, availableLayers.length);

          console.log(wmsdoc);
          var wmsLayers = wmsdoc.getElementsByTagName("Layer")[0].getElementsByTagName("Layer");
          console.log('Loading (' + wmsLayers.length  + ') WMS Layers');

          console.log(wfsdoc);
          var wfsLayers = wfsdoc.getElementsByTagName("FeatureTypeList")[0].getElementsByTagName("FeatureType");
          console.log('Loading (' + wfsdoc.length  + ') WFS Layers');

          var layers = [];

          for (var ii=0; ii<wmsLayers.length; ii++) {

            var title = wmsLayers[ii].getElementsByTagName("Title")[0].innerHTML;
            var name = wmsLayers[ii].getElementsByTagName("Name")[0].innerHTML;

            layers.push({name: title, key: name, type: ['WFS']});
          }

          for (var ii=0; ii<wfsLayers.length; ii++) {

            var title = wfsLayers[ii].getElementsByTagName("Title")[0].innerHTML;
            var name = wfsLayers[ii].getElementsByTagName("Name")[0].innerHTML;

            var layer = that.getLayer(layers, name);

            if (layer == null) {
              layers.push({name: title, key: name, type: ['WMS']});
            } else {
              layer.type.push('WMS');
            }
          }

          layers.sort(function(a, b) {
                    var nameA = a.name.toUpperCase();
                    var nameB = b.name.toUpperCase();

                    if (nameA < nameB) {
                      return -1;
                    }
                    if (nameA > nameB) {
                      return 1;
                    }

                    return 0;
                  }).forEach(function(layer) {
                      console.log(layer.name + " : " + JSON.stringify(layer.type));
                      availableLayers.push(layer);
                  })

                  this.setState({
                    availableLayers: availableLayers
                  });

        }).catch(function(error) {
          Popup.plugins().promptError("(FAILED) Loading WMS Capabilities: " + error);
        })
    }).catch(function(error) {
      Popup.plugins().promptError("(FAILED) Loading WFS Capabilities: " + error);
    })

  }

  getLayer(layers, name) {
    for (var ii=0; ii<layers.length; ii++) {
      if (layers[ii].key === name) {
        return layers[ii];
      }
    }

    return null;
  }

  fetchLayers(url) {

    var that = this;

    fetch(url + '/rest/layers.json')
      .then(res => res.json())
      .then(data => {

        const {availableLayers} = this.state;

        // Clear Values
        availableLayers.slice(0, availableLayers.length);

        console.log('(' + data.layers.layer.length  + ') Layers Loaded');

        data.layers.layer.sort(function(a, b) {
                  var nameA = a.name.toUpperCase();
                  var nameB = b.name.toUpperCase();
                  if (nameA < nameB) {
                    return -1;
                  }
                  if (nameA > nameB) {
                    return 1;
                  }

                  return 0;
                }).forEach(function(layer) {
                  layer.key = that.props.workspace + ":" + layer.name;
                  availableLayers.push(layer);
                })

        this.setState({
          availableLayers: availableLayers
        });

      }).catch(function(error) {
        Popup.plugins().promptError("(FAILED) Loading Layers: " + error);
      })
  }

  fetchStyles() {

    var that = this;

    var query = {
      "pageSize": 200,
      "pageNumber": 1,
      "propertyNames": [
        "meta.stylename",
        "Fill Style.color",
        "Fill Style.alpha",
        "Stroke Style.color",
        "Stroke Style.width",
        "Text Style.fontSize",
        "Text Style.fontType",
        "Text Style.fontFillColor",
        "Text Style.fontStrokeColor",
        "Text Style.fontStrokeWidth",
      ],
      "group": {
        "operator": "AND",
        "criteria": [{
          "recordset": "coalesceentity",
          "field": "name",
          "operator": "EqualTo",
          "value": "Style",
          "matchCase": "true"
        }]
      },
      "sortBy": [
          {
            "propertyName": "CoalesceEntity.lastModified",
            "sortOrder": "ASC"
          }
        ],
    };

    searchComplex(query)
      .then(data => {

        const {styles} = that.state;

        if (data.result.length === 1 && data.result[0].status === 'SUCCESS') {
          data.result[0].result.hits.forEach(function (hit) {
            styles.push({
                "key": hit.values[0],
                "name": hit.values[0],
                "fill": {
                  "color": hit.values[1],
                  "alpha": hit.values[2]
                },
                "stroke": {
                  "color": hit.values[3],
                  "width": hit.values[4]
                },
                "text": {
                  "font": {
                    "size": hit.values[5],
                    "type": hit.values[6]
                  },
                  "fill": {
                    "color": hit.values[7]
                  },
                  "stroke": {
                    "color": hit.values[8],
                    "width": hit.values[9]
                  }
                }
            });

          });
        }


        that.setState({
          styles: styles
        });
      }).catch(function(error) {
        console.log('(FAILED) Loading Styles: ' + error);
      });
  }

  componentDidMount() {
    //this.fetchLayers(this.state.geoserver);
    this.fetchStyles();
    this.fetchCapabilities(this.state.geoserver);
  }

  render() {
    return (
        <MapView
          geoserver={this.state.geoserver}
          workspace={this.state.workspace}
          styles={this.state.styles}
          availableLayers={this.state.availableLayers}
          selectedLayers={this.state.selectedLayers}
          />
    )
  }

}

App.defaultProps = {
  geoserver: 'http://bdpgeoserver.bdpdev.incadencecorp.com:8181/geoserver',
  availableLayers: [],
  styles: [],
  // TODO Default layers should be removed for production or load from a saved state.
  selectedLayers: [
    {
      key: 'OSM',
      name: 'OSM',
      type: 'WMS',
      checked: false
    }
    /*
    ,
    {
      key: 'NaturalEarth',
      name: 'NaturalEarth',
      type: 'WMS',
      checked: true
    },
    {
      key: 'cib1',
      name: 'cib1',
      type: 'WMS',
      checked: true
    },
    {
      key: 'OSM_buildings',
      name: 'OSM_buildings',
      type: 'WMS',
      checked: false
    },
    {
      key: 'OSM_highways_line',
      name: 'OSM_highways_line',
      type: 'WMS',
      checked: true
    },
    {
      key: 'OSM_highways_polygon',
      name: 'OSM_highways_polygon',
      type: 'WMS',
      checked: true
    },
    {
      key: 'waterway_line',
      name: 'waterway_line',
      type: 'WMS',
      checked: true
    }, {
      key: 'waterway_polygon',
      name: 'waterway_polygon',
      type: 'WMS',
      checked: true
    }
    */
  ]
}

loadProperty('geoserver.url')
  .then(data => {
    ReactDOM.render(
      <App geoserver={data}/>,
      document.getElementById('main')
    );
}).catch(function(error) {
  Popup.plugins().promptError("(FAILED) Retrieving Geo Server URL: " + error);
  ReactDOM.render(
    <App />,
    document.getElementById('main')
  );
});

ReactDOM.render(
  <Popup />,
  document.getElementById('popupContainer')
);
