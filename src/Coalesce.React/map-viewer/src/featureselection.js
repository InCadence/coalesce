import React from "react";
import Popup from 'react-popup';
import {PromptDropdown} from 'common-components/lib/prompt-dropdown.js'
import {StyleSelection} from './style.js'
import * as ol from 'openlayers';

export class FeatureSelection extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;

    props.selectedLayers.forEach( function(feature) {
      if (feature.layer == null)
      {
        props.addfeature(feature);
      }
    });
  }

  onAddClick() {
    var that = this;

    const {selectedLayers, availableLayers, styles} = this.state;

    if (availableLayers.length !== 0) {

      var filteredLayers = [];

      // Filter out layers that have alreadt been selected
      availableLayers.forEach(function (feature) {

        var found = false;

        for (var ii=0; ii<selectedLayers.length; ii++) {
          if (selectedLayers[ii].name === feature.name) {
            found = true;
            break;
          }
        }

        if (!found) {
          filteredLayers.push({key: feature.name, name: feature.name});
        }
      });

      // Prompt for layer to add
      Popup.plugins().promptAddFeature(filteredLayers, styles, function (value) {

        if (value.type === "WFS") {
          // Create client side style
          if (value.style === 'Custom') {
            // Prompt for custom style
            Popup.plugins().promptStyle(styles, function(style) {
              that.addLayer(value.layer, value.type, value.tiled, createStyle(style));
            });
          } else {
            for (var ii=0; ii<styles.length; ii++)
            {
              if (styles[ii].key === value.style) {
                that.addLayer(value.layer, value.type, value.tiled, createStyle(styles[ii]));
                break;
              }
            }
          }
        } else {
          that.addLayer(value.layer, value.type, value.tiled);
        }

      });
    } else {
      Popup.plugins().promptError('No available layers');
    }
  }

  addLayer(name, type, tiled, style) {

    const {selectedLayers} = this.state;

    var feature = {
      key: name,
      name: name,
      type: type,
      style: style,
      checked: false,
      tiled: tiled,
    };

    selectedLayers.push(feature);

    this.props.addfeature(feature);

    this.setState({
      selectedLayers: selectedLayers
    });
  }

  onRmvClick() {

    const {selectedLayers} = this.state;

    var that = this;

    Popup.plugins().promptRemoveFeature(selectedLayers, 'Remove', function (value) {

      for (var ii=0; ii<selectedLayers.length; ii++) {

        if (selectedLayers[ii].name === value) {
          that.props.rmvfeature(selectedLayers[ii]);
          selectedLayers.splice(ii, 1);
          that.setState({
            selectedLayers: selectedLayers
          });
          break;
        }
      }
    });

  }

  onChange(feature, e) {

    const {selectedLayers} = this.state;

    if (e.target.checked) {
      if (feature.layer != null) {
        feature.layer.setVisible(true);
      } else {
        this.props.addfeature(feature);
        console.log("Adding: " + feature.name);
      }
    } else {
      if (feature.layer != null) {
        feature.layer.setVisible(false);
      } else {
        this.props.rmvfeature(feature);
        console.log("Removing: " + feature.name);
      }
    }

    feature.checked = e.target.checked;

    this.setState({
      selectedLayers: selectedLayers
    })
  }

  moveLayer(feature, up, e) {

    const {selectedLayers, moveLayer} = this.state;

    var idx = selectedLayers.indexOf(feature);

    if (idx != -1) {

      var newIdx = idx;

      if (up && idx - 1 >= 0) {
        newIdx = idx - 1;
      } else if(!up && idx + 1 < selectedLayers.length) {
        newIdx = idx + 1;
      }

      if (idx !== newIdx) {
        // Swap Positions
        selectedLayers[idx] = selectedLayers[newIdx];
        selectedLayers[newIdx] = feature;

        moveLayer(feature, up, idx);

        this.setState({
          selectedLayers: selectedLayers
        });
      }
    }
  }

  render() {

    var features = [];

    console.log("Render Feature Selection");

    if (this.state.selectedLayers != null) {

      for (var ii=0; ii<this.state.selectedLayers.length; ii++) {
        var feature = this.state.selectedLayers[ii];

        var isFirst = ii===0;
        var isLast = ii===this.state.selectedLayers.length - 1;

        features.push(
          <div key={feature.name} className="row">
            <div className="col-sm-2">
              <input id={feature.name} className="form-control" type="checkbox" onChange={this.onChange.bind(this, feature)} checked={feature.checked} />
            </div>
            <div className="col-sm-5">
              <label>{feature.name}</label>
            </div>
            <div className="col-sm-2">
              <label>{feature.type}</label>
            </div>
            <div className="col-sm-3">
              <div className="form-buttons">
                <img src={isFirst ? '' : require('common-components/img/up.ico')} alt={isFirst ? '' : "Up"}  className={isFirst ? "coalesce-img-button small disabled" : "coalesce-img-button small enabled"} onClick={this.moveLayer.bind(this, feature, true)} />
                <img src={isLast ? '' : require('common-components/img/down.ico')} alt={isLast ? '' : "Down"} className={isLast ? "coalesce-img-button small disabled" : "coalesce-img-button small enabled"} onClick={this.moveLayer.bind(this, feature, false)}/>
              </div>
            </div>
          </div>
        )
      }
    }

    return (
      <div className="ui-widget">
        <div className="ui-widget-header">
          <div className="row">
            <div className="col-sm-7">
              <label>Layers</label>
            </div>
            <div className="col-sm-5">
              <label>Source</label>
            </div>
          </div>
        </div>
        <div className="ui-widget-content">
          {features}
          <div className="form-buttons">
            <img src={require('common-components/img/remove.ico')} alt="Remove" title="Remove Layer" className="coalesce-img-button enabled" onClick={this.onRmvClick.bind(this)}/>
            <img src={require('common-components/img/add.ico')} alt="Add" title="Add Layer" className="coalesce-img-button enabled" onClick={this.onAddClick.bind(this)}/>
          </div>
        </div>
      </div>
    )
  }

}

function createStyle(styleData) {
  return new ol.style.Style({
    fill: new ol.style.Fill({
      color: hexToRgbA(styleData.fill.color, styleData.fill.alpha), //'rgba(255, 255, 255, 0.6)'
    }),
    stroke: new ol.style.Stroke({
      color: styleData.stroke.color,
      width: styleData.stroke.width
    }),
    text: new ol.style.Text({
      font: styleData.text.font.size + 'px ' + styleData.text.font.type,
      fill: new ol.style.Fill({
        color: styleData.text.fill.color
      }),
      stroke: new ol.style.Stroke({
        color: styleData.text.stroke.color,
        width: styleData.text.stroke.width
      })
    })
  });
}

function hexToRgbA(hex, alpha){
    var c;
    if(/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)){
        c= hex.substring(1).split('');
        if(c.length === 3){
            c= [c[0], c[0], c[1], c[1], c[2], c[2]];
        }
        c= '0x'+c.join('');
        return 'rgba('+[(c>>16)&255, (c>>8)&255, c&255].join(',')+',' + alpha + ')';
    }
    console.log('Bad Hex: ' + hex);
}

/** Prompt plugin */
Popup.registerPlugin('promptAddFeature', function (data, styles, callback) {

    var styleOptions = [{
      'key': 'Custom',
      'name': 'Custom'
    }].concat(styles);

    let promptValue = {tiled: false};
    let layerChange = function (value) {
        promptValue.layer = value;
    };

    let typeChange = function (value) {
        promptValue.type = value;
    };

    let styleChange = function (value) {
        promptValue.style = value;
    };

    let tileChange = function (value) {
        promptValue.tiled = value.target.checked;
    };

    this.create({
        title: 'Select Feature',
        content: (
          <div>
            <label>Layer</label>
            <PromptDropdown onChange={layerChange} data={data}/>
            <label>Type</label>
            <PromptDropdown onChange={typeChange} data={[
              {key: 'WMS', name:'WMS'},
              {key: 'WFS', name:'WFS'},
              {key: 'WPS', name:'WPS'},
              {key: 'HEATMAP', name:'HEATMAP'}
            ]}/>
            <label>Style (WFS Only)</label>
            <PromptDropdown onChange={styleChange} data={styleOptions} />
            <label>Tiled (WMS Only)</label>
            <div className="row">
              <div className="col-sm-2">
                <input type="checkbox" className="form-control" onChange={tileChange} />
              </div>
            </div>
          </div>
        ),
        buttons: {
            left: ['cancel'],
            right: [{
                text: 'Add',
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }
            }]
        }
    });
})

Popup.registerPlugin('promptRemoveFeature', function (data, buttontext, callback) {
    let promptValue = null;
    let promptChange = function (value) {
        promptValue = value;
    };

    this.create({
        title: 'Select Feature',
        content: <PromptDropdown onChange={promptChange} data={data}/>,
        buttons: {
            left: ['cancel'],
            right: [{
                text: 'Remove',
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }
            }]
        }
    });
})

Popup.registerPlugin('promptStyle', function (styles, callback) {

    let data = null;
    let dataChange = function (value) {
        data = value;
    };

    this.create({
        title: 'Select Style',
        content: <StyleSelection onChange={dataChange} presets={styles} data={styles[0]}/>,
        buttons: {
            left: ['cancel'],
            right: [{
                text: 'OK',
                className: 'success',
                action: function () {
                    callback(data);
                    Popup.close();
                }
            }]
        }
    });
})
