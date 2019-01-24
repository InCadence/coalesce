import React from "react";
import {MapView} from './map.js'
import { searchComplex } from 'common-components/lib/js/searchController'
import { DialogMessage } from 'common-components/lib/components/dialogs';

import 'common-components/css/coalesce.css'
import './index.css'

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      error: props.error,
      availableLayers: props.availableLayers,
      selectedLayers: props.selectedLayers,
      styles: props.styles
    }

    this.handleError = this.handleError.bind(this);
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
          console.log('Loading (' + wfsLayers.length  + ') WFS Layers');

          var layers = [];
          var title;
          var name;

          for (var ii=0; ii<wmsLayers.length; ii++) {

            title = wmsLayers[ii].getElementsByTagName("Title")[0].innerHTML;
            name = wmsLayers[ii].getElementsByTagName("Name")[0].innerHTML;

            layers.push({name: title, key: name, type: ['WFS']});
          }

          for (var jj=0; jj<wfsLayers.length; jj++) {

            title = wfsLayers[jj].getElementsByTagName("Title")[0].innerHTML;
            name = wfsLayers[jj].getElementsByTagName("Name")[0].innerHTML;

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
          this.handleError(`(FAILED) Loading WMS Capabilities: ${error.message}`)
        })
    }).catch(function(error) {
      this.handleError(`(FAILED) Loading WFS Capabilities: ${error.message}`)
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
        this.handleError(`(FAILED) Loading Layers: ${error.message}`)
      })
  }

  handleError(message) {
    this.setState(() => {return {error: message}})
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
    //this.fetchLayers(this.props.geoserver);
    this.fetchStyles();
    this.fetchCapabilities(this.props.geoserver);
  }

  render() {
    return (
      <div>
        <MapView
          geoserver={this.props.geoserver}
          workspace={this.props.workspace}
          styles={this.state.styles}
          availableLayers={this.state.availableLayers}
          selectedLayers={this.state.selectedLayers}
          onError={this.handleError}
          />
          { this.state.error &&
            <DialogMessage
              title="Error"
              opened={true}
              message={this.state.error}
              onClose={() => {this.setState({error: null})}}
            />
          }
      </div>
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

export default App
