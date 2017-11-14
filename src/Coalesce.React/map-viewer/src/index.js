import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {MapView} from './map.js'
import $ from 'jquery'

import './index.css'
import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = 'http://' + window.location.hostname + ':' + window.location.port;
}

class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = props;
  }

fetchCapabilities(url) {

  fetch(url + '/wfs?service=wfs&version=1.1.0&request=GetCapabilities')
    .then(res => res.text())
    .then(str => (new window.DOMParser()).parseFromString(str, "text/xml"))
    .then(doc => {

      const {availableLayers} = this.state;

      console.log(doc);
      var featureTypes = doc.getElementsByTagName("FeatureTypeList")[0].getElementsByTagName("FeatureType");

      console.log('(' + featureTypes.length  + ') Layers Loaded');

      var layers = [];

      for (var ii=0; ii<featureTypes.length; ii++) {

        var title = featureTypes[ii].getElementsByTagName("Title")[0].innerHTML;
        var name = featureTypes[ii].getElementsByTagName("Name")[0].innerHTML;

        layers.push({name: title, key: title});

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
                // TODO Populate availableLayers
                //console.log(layer.name);
              })
      /*
      for (var ii=0; ii<availableLayers.length; ii++) {
        console.log(availableLayers[ii].name);
      }
      */
    }).catch(function(error) {
      alert("(FAILED) Loading Capabilities: " + error);
    })

  }

  fetchLayers(url) {
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
                  availableLayers.push(layer);
                })

        this.setState({
          availableLayers: availableLayers
        });

      }).catch(function(error) {
        alert("(FAILED) Loading Layers: " + error);
      })
  }

  fetchStyles(url) {

    var that = this;

    $.ajax({
      type : 'POST',
      url: url + '/cxf/data/search/complex',
      data: JSON.stringify({
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
          "booleanComparer": "AND",
          "criteria": [{
            "recordset": "coalesceentity",
            "field": "name",
            "comparer": "=",
            "value": "Style",
            "matchCase": "true"
          }]
        }
      }),
      contentType : "application/json; charset=utf-8",
      success : function(data, status, jqXHR) {

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
      },
      error : function(jqXHR, status) {
        alert('(FAILED) Loading Styles: ' + JSON.stringify(jqXHR));
        // error handler
        console.log(jqXHR);
      }
    });
  }

  componentDidMount() {
    this.fetchLayers(this.state.geoserver);
    this.fetchStyles(this.state.karafserver);
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
  workspace: 'OE_Repository',
  karafserver: rootUrl,
  availableLayers: [],
  styles: [],
  // TODO Default layers should be removed for production or load from a saved state.
  selectedLayers: [
    {
      key: 'Ariana All',
      name: 'Ariana All',
      type: 'WMS',
      checked: true
    }, {
      key: 'Ariana Cities',
      name: 'Ariana Cities',
      type: 'WMS',
      checked: false
    },{
      key: 'Ariana Pipelines',
      name: 'Ariana Pipelines',
      type: 'WFS',
      checked: false
    }]
}

fetch(rootUrl + '/cxf/data/property/geoserver.url')
  .then(res => res.text())
  .then(data => {
    ReactDOM.render(
      <App geoserver={data}/>,
      document.getElementById('main')
    );
}).catch(function(error) {
  renderError("Saving: " + error);
});

function renderError(error) {
  Popup.close();
  Popup.create({
      title: 'Error',
      content: error,
      className: 'alert',
      buttons: {
          right: ['ok']
      }
  }, true);
}

ReactDOM.render(
  <Popup />,
  document.getElementById('popupContainer')
);
