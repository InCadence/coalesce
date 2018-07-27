import React from "react";
import Popup from 'react-popup';
import {PromptDropdown} from 'common-components/lib/prompt-dropdown.js'
import {StyleSelection} from './style.js'
import IconButton from 'common-components/lib/components/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import Checkbox from '@material-ui/core/Checkbox';
import { DialogOptions } from 'common-components/lib/components/dialogs';
import * as ol from 'openlayers';

import 'common-components/css/popup.css'

export class FeatureSelection extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;

    if (props.selectedLayers) {
      props.selectedLayers.forEach( function(feature) {
        if (feature.layer == null)
        {
          props.addfeature(feature);
        }
      });
    }

    this.handleRemoveLayer = this.handleRemoveLayer.bind(this);
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
          if (selectedLayers[ii].key === feature.key) {
            found = true;
            break;
          }
        }

        if (!found) {
          filteredLayers.push({key: feature.key, name: feature.name});
        }
      });

      console.log('GOT HERE');

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
      this.props.handleError('No available layers')
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

  handleRemoveLayer(key) {

    const {selectedLayers} = this.state;

    for (var ii=0; ii<selectedLayers.length; ii++) {

      if (selectedLayers[ii].key === key) {
        this.props.rmvfeature(selectedLayers[ii]);
        selectedLayers.splice(ii, 1);
        this.setState({
          selectedLayers: selectedLayers,
          promptRemoveLayer: false
        });
        break;
      }
    }
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

    if (idx !== -1) {

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

    if (this.state.selectedLayers != null) {

      for (var ii=0; ii<this.state.selectedLayers.length; ii++) {
        var feature = this.state.selectedLayers[ii];

        var isFirst = ii===0;
        var isLast = ii===this.state.selectedLayers.length - 1;

        features.push(
          <ListItem
            key={feature.name}
            dense
            role={undefined}
          >
            <Checkbox
              checked={feature.checked}
              onChange={this.onChange.bind(this, feature)}
              tabIndex={-1}
              disableRipple
            />
            <ListItemText primary={feature.name} secondary={feature.type} />
            <ListItemSecondaryAction>
              {!isFirst &&
              <IconButton
                icon="/images/svg/up.svg"
                title="Move Layer Up"
                size="24px"
                onClick={this.moveLayer.bind(this, feature, true)}
              />
              }
              {!isLast &&
              <IconButton
                icon="/images/svg/down.svg"
                title="Move Layer Down"
                size="24px"
                onClick={this.moveLayer.bind(this, feature, false)}
              />
            }
            </ListItemSecondaryAction>
          </ListItem>
        )
      }
    }

    return (
      <div className="ui-widget">
        <div className="ui-widget-header">
          Layers
        </div>
        <div className="ui-widget-content">
          <List dense>
            {features}
          </List>
          <div className="form-buttons">
            <IconButton
              icon="/images/svg/remove.svg"
              title="Remove Layer"
              size="24px"
              //onClick={this.onRmvClick.bind(this)}
              onClick={() => this.setState(() => {return {promptRemoveLayer: true}})}
             />
            <IconButton
              icon="/images/svg/add.svg"
              title="Add Layer"
              size="24px"
              onClick={this.onAddClick.bind(this)}
            />
          </div>
        </div>
        {this.state.promptRemoveLayer &&
          <DialogOptions
            title="Select Layer to Remove"
            open={true}
            onClose={() => {this.setState({promptRemoveLayer: false})}}
            onClick={this.handleRemoveLayer}
            options={this.state.selectedLayers}
          />
        }
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
