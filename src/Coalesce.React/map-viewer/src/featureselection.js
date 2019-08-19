import React from "react";
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import Checkbox from '@material-ui/core/Checkbox';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

import * as ol from 'openlayers';

import IconButton from 'coalesce-components/lib/components/IconButton';
import { DialogOptions } from 'coalesce-components/lib/components/dialogs';
import { DialogFeatures } from "./DialogFeatures.js";

export class FeatureSelection extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;

    if (props.selectedLayers) {
      props.selectedLayers.forEach(function (feature) {
        if (feature.layer == null) {
          props.addfeature(feature);
        }
      });
    }

    this.handleRemoveLayer = this.handleRemoveLayer.bind(this);
  }

  onAddClick = () => {
    const { selectedLayers, availableLayers, styles } = this.state;

    if (availableLayers.length !== 0) {

      var filteredLayers = [];

      // Filter out layers that have alreadt been selected
      availableLayers.forEach(function (feature) {

        var found = false;

        for (var ii = 0; ii < selectedLayers.length; ii++) {
          if (selectedLayers[ii].key === feature.key) {
            found = true;
            break;
          }
        }

        if (!found) {
          filteredLayers.push({ key: feature.key, name: feature.name });
        }
      });

      this.setState({ layers: filteredLayers, styles: styles, promptAddLayer: true });
    }
  }

  addNewLayer = (value) => {

    const that = this;
    if (value.type === "WFS") {
      that.addLayer(value.layer, value.type, value.tiled, createStyle(value.style));
    } else {
      console.log(value)
      that.addLayer(value.layer, value.type, value.tiled);
    }

  }

  addLayer(name, type, tiled, style) {

    const { selectedLayers } = this.state;

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
      promptAddLayer: false,
      selectedLayers: selectedLayers
    });
  }

  handleRemoveLayer(key) {

    const { selectedLayers } = this.state;

    for (var ii = 0; ii < selectedLayers.length; ii++) {

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

    const { selectedLayers } = this.state;

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

    const { selectedLayers, moveLayer } = this.state;

    var idx = selectedLayers.indexOf(feature);

    if (idx !== -1) {

      var newIdx = idx;

      if (up && idx - 1 >= 0) {
        newIdx = idx - 1;
      } else if (!up && idx + 1 < selectedLayers.length) {
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

  toggleDialogRemove = () => {
    this.setState(() => { return { promptRemoveLayer: !this.state.promptRemoveLayer } });
  }

  toggleLayerPrompt = () => {
    this.setState({ promptAddLayer: !this.state.promptAddLayer });
  }

  render() {

    var features = [];

    if (this.state.selectedLayers != null) {

      for (var ii = 0; ii < this.state.selectedLayers.length; ii++) {
        var feature = this.state.selectedLayers[ii];

        var isFirst = ii === 0;
        var isLast = ii === this.state.selectedLayers.length - 1;

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
                  square
                  onClick={this.moveLayer.bind(this, feature, true)}
                />
              }
              {!isLast &&
                <IconButton
                  icon="/images/svg/down.svg"
                  title="Move Layer Down"
                  size="24px"
                  square
                  onClick={this.moveLayer.bind(this, feature, false)}
                />
              }
            </ListItemSecondaryAction>
          </ListItem>
        )
      }
    }

    return (
      <React.Fragment>
        <AppBar position="static" color="default">
          <Toolbar>
            <Typography variant="h6" style={{ flexGrow: 1 }}>Layers</Typography>
            <IconButton
              icon="/images/svg/remove.svg"
              title="Remove Layer"
              size="24px"
              square
              onClick={this.toggleDialogRemove}
            />
            <IconButton
              icon="/images/svg/add.svg"
              title="Add Layer"
              size="24px"
              square
              onClick={this.onAddClick}
            />
          </Toolbar>
        </AppBar>
        <List dense>
          {features}
        </List>
        {this.state.promptRemoveLayer &&
          <DialogOptions
            title="Select Layer to Remove"
            open={true}
            onClose={this.toggleDialogRemove}
            onClick={this.handleRemoveLayer}
            options={this.state.selectedLayers}
          />
        }
        {this.state.promptAddLayer &&
          <DialogFeatures data={this.state.layers} styles={this.state.styles} onClick={this.addNewLayer} onClose={this.toggleLayerPrompt} />
        }
      </React.Fragment>
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


function hexToRgbA(hex, alpha) {
  var c;
  if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
    c = hex.substring(1).split('');
    if (c.length === 3) {
      c = [c[0], c[0], c[1], c[1], c[2], c[2]];
    }
    c = '0x' + c.join('');
    return 'rgba(' + [(c >> 16) & 255, (c >> 8) & 255, c & 255].join(',') + ',' + alpha + ')';
  }
  console.log('Bad Hex: ' + hex);
}

