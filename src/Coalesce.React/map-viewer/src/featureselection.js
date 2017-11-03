import React from "react";
import Popup from 'react-popup';
import {PromptDropdown} from 'common-components/lib/prompt-dropdown.js'
import {StyleSelection} from './style.js'
import * as ol from 'openlayers';
import $ from 'jquery'

import {MultiSelectField} from './multiexample.js'

var rootUrl = 'http://localhost:8181';

export class FeatureSelection extends React.Component {

  constructor(props) {
    super(props);

    this.state = props;
  }

  componentDidMount() {

      //this.state.geoserver + '/rest/workspaces/' + this.state.workspace + '/featuretypes.json'
    fetch(this.state.geoserver + '/rest/layers.json')
      .then(res => res.json())
      .then(data => {

        this.setState({
          layers: data.layers.layer
          //layers: data.featureTypes.featureType
        });

        console.log('(' + data.layers.layer.length  + ') Layers Loaded');
      }).catch(function(error) {
        alert(error);
      })

/*
      $.ajax({
        type : 'POST',
        url: rootUrl + '/cxf/data/search',
        data : JSON.stringify([{key: 0, recordset: "CoalesceEntity", field: "name", comparer: "=", value: "Style", matchCase: true}]),
        contentType : "application/json; charset=utf-8",
        success : function(data, status, jqXHR) {
          console.log(JSON.stringify(data));
        },
        error : function(jqXHR, status) {
          alert('Failed: ' + JSON.stringify(jqXHR));
          // error handler
          console.log(jqXHR);
        }
      });
*/
  }

  onChange(e) {
    if (e.target.checked) {
      const {features} = this.state;

      for( var ii=0; ii<features.length; ii++) {
        if (features[ii].name === e.target.id) {
          this.props.addfeature(e.target.id, features[ii].style);
          break;
        }
      }
    } else {
      this.props.rmvfeature(e.target.id);
    }
  }

  addFeature() {
    var that = this;

    const {features, layers, styles} = this.state;

    var featureData = [];

    layers.forEach(function (feature) {

      var found = false;

      for (var ii=0; ii<features.length; ii++) {
        if (features[ii].name === feature.name) {
          found = true;
          break;
        }
      }

      if (!found) {

        featureData.push({key: feature.name, name: feature.name});
      }
    });

    Popup.plugins().promptFeature2(featureData, [{key: 'custom', name: 'Custom'}], function (value) {

      Popup.plugins().promptStyle(function(data) {
        //var {features} = that.state;

        var style = new ol.style.Style({
                fill: new ol.style.Fill({
                  color: hexToRgbA(data.fill.color, data.fill.alpha), //'rgba(255, 255, 255, 0.6)'
                }),
                stroke: new ol.style.Stroke({
                  color: data.stroke.color,
                  width: data.stroke.width
                }),
                text: new ol.style.Text({
                  font: data.text.font.size + 'px ' + data.text.font.type,
                  fill: new ol.style.Fill({
                    color: data.text.fill.color
                  }),
                  stroke: new ol.style.Stroke({
                    color: data.text.stroke.color,
                    width: data.text.stroke.width
                  })
                })
              });

        features.push({name: value.layer, style: style});

        that.setState({features: features});
      })

    });
  }

  removeFeature() {

    var that = this;

    const {features} = this.state;

    var featuresData = [];

    features.forEach(function (feature) {
      featuresData.push({key: feature.name, name: feature.name});
    });

    Popup.plugins().promptFeature(featuresData, 'Remove', function (value) {

      for (var ii=0; ii<features.length; ii++) {
        if (features[ii].name === value) {
          that.props.rmvfeature(value);
          features.splice(ii, 1);
          that.setState({
            features: features
          });
          break;
        }
      }
    });

  }

  render() {

    var features = [];

    if (this.state.features != null) {

      for (var ii=0; ii<this.state.features.length; ii++) {
        var name = this.state.features[ii].name;
        features.push(
          <div key={name} className="row">
            <div className="col-sm-2">
              <input id={name} type="checkbox" name={name} onChange={this.onChange.bind(this)}/>
            </div>
            <div className="col-sm-6">
              <label>{name}</label>
            </div>
            <div className="col-sm-4">
              <button>...</button>
            </div>
          </div>
        )
      }
    }

    return (
      <div>
        {features}
        <div className="form-buttons">
          <button className="mm-popup__btn mm-popup__btn--cancel" onClick={this.removeFeature.bind(this)}>Remove</button>
          <button className="mm-popup__btn mm-popup__btn--success" onClick={this.addFeature.bind(this)}>Add</button>
        </div>
      </div>
    )
  }

}

FeatureSelection.defaultProps = {
  features: [],
  layers: []
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
    throw new Error('Bad Hex');
}

/** Prompt plugin */
Popup.registerPlugin('promptMultiFeature', function (data, buttontext, callback) {
    let promptValue = null;
    let promptChange = function (value) {
      console.log(value);
        promptValue = value;
    };

    this.create({
        title: 'Select Feature',
        content: <MultiSelectField onChange={promptChange} data={data}/>,
        buttons: {
            left: ['cancel'],
            right: [{
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }
            }]
        }
    });
})

/** Prompt plugin */
Popup.registerPlugin('promptFeature2', function (data, styles, callback) {
    let promptValue = {};
    let layerChange = function (value) {
        promptValue.layer = value;
    };

    let typeChange = function (value) {
        promptValue.type = value;
    };

    let styleChange = function (value) {
        promptValue.style = value;
    };
    this.create({
        title: 'Select Feature',
        content: (
          <div>
            <label>Layer</label>
            <PromptDropdown onChange={layerChange} data={data}/>
            <label>Type</label>
            <PromptDropdown onChange={typeChange} data={[
              {key: 'WFS', name:'WFS'},
              {key: 'WMS', name:'WMS'},
              {key: 'WPS', name:'WPS'}
            ]}/>
            <label>Style</label>
            <PromptDropdown onChange={styleChange} data={styles}/>
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

Popup.registerPlugin('promptFeature', function (data, buttontext, callback) {
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
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }
            }]
        }
    });
})

Popup.registerPlugin('promptStyle', function (callback) {
    let data = null;
    let dataChange = function (value) {
        data = value;
    };

    this.create({
        title: 'Select Style',
        content: <StyleSelection onChange={dataChange} />,
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
