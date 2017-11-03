import React from "react";

export class StyleSelection extends React.Component {

  constructor(props) {
    super(props);

    this.state = props;
    this.state.onChange(props.data);

  }
/*
  componentDidMount() {
    fetch('http://bdpgeoserver.bdpdev.incadencecorp.com:8181/geoserver/rest/fonts.json')
      .then(res => res.json())
      .then(data => {
        this.setState({fonts: data.fonts});
      });

  }
*/

  fillColorChange(e) {
    const {data} = this.state;
    data.fill.color = e.target.value;
    this.update(data);
  }

  fillAplhaChange(e) {
    const {data} = this.state;
    data.fill.alpha = e.target.value;
    this.update(data);
  }

  strokeColorChange(e) {
    const {data} = this.state;
    data.stroke.color = e.target.value;
    this.update(data);
  }

  strokeWidthChange(e) {
    const {data} = this.state;
    data.stroke.width = e.target.value;
    this.update(data);
  }

  fontSizeChange(e)  {
    const {data} = this.state;
    data.text.font.size = e.target.value;
    this.update(data);
  }

  fontTypeChange(e) {
    const {data} = this.state;
    data.text.font.type = e.target.value;
    this.update(data);
  }

  textFillColorChange(e) {
    const {data} = this.state;
    data.text.fill.color = e.target.value;
    this.update(data);
  }

  fontStrokeColorChange(e) {
    const {data} = this.state;
    data.text.stroke.color = e.target.value;
    this.update(data);
  }

  fontStrokeWidthChange(e) {
    const {data} = this.state;
    data.text.stroke.width = e.target.value;
    this.update(data);
  }

  presetChange(e) {
    const {presets} = this.state;
    for (var ii=0; ii<presets.length; ii++) {
      if (presets[ii].name === e.target.value) {
        this.update(presets[ii]);
        break;
      }
    }
  }

  update(data) {
    this.setState({data: data});
    this.state.onChange(data);
  }

  render() {

    const {data, presets, fonts} = this.state;

    var presetOptions = [];

    presets.forEach(function(preset) {
        presetOptions.push(<option key={preset.name}>{preset.name}</option>);
    });

    var fontOptions = [];

    fonts.forEach(function(font) {
        fontOptions.push(<option key={font}>{font}</option>);
    });
    return (
      <form>
        <div className="form-row">
          <div className="form-group col-md-12">
            <label>Presets</label>
            <select className="form-control" id="presets" onChange={this.presetChange.bind(this)} >
              {presetOptions}
            </select>
          </div>
        </div>
        <div className="form-row">
          <label>Fill</label>
          <div className="form-row">
            <div className="form-group col-md-6">
              <label>Color</label>
              <input type="color" className="form-control" id="fillcolor" value={data.fill.color} onChange={this.fillColorChange.bind(this)}/>
            </div>
            <div className="form-group col-md-6">
              <label>Alpha</label>
              <input type="text" min="0" max="1" step="0.01" className="form-control" id="fillalpha" value={data.fill.alpha} onChange={this.fillAplhaChange.bind(this)}/>
            </div>
          </div>
        </div>
        <div className="form-row">
          <label>Stroke</label>
          <div className="form-row">
            <div className="form-group col-md-6">
              <label>Color</label>
              <input type="color" className="form-control" id="strokecolor" value={data.stroke.color} onChange={this.strokeColorChange.bind(this)}/>
            </div>
            <div className="form-group col-md-6">
              <label>Width</label>
              <input type="number" className="form-control" id="strokewidth" value={data.stroke.width} onChange={this.strokeWidthChange.bind(this)}/>
            </div>
          </div>
        </div>
        <div className="form-row">
          <label>Text</label>
          <div className="form-row">
            <div className="form-group col-md-3">
              <label>Size</label>
              <input type="number" className="form-control" id="fontsize" value={data.text.font.size} onChange={this.fontSizeChange.bind(this)}/>
            </div>
            <div className="form-group col-md-9">
              <label>Type</label>
              <select className="form-control" id="fonttype" value={data.text.font.type} onChange={this.fontTypeChange.bind(this)}>
                {fontOptions}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group col-md-4">
              <label>Fill</label>
              <input type="color" className="form-control" id="fontfillcolor" value={data.text.fill.color} onChange={this.textFillColorChange.bind(this)}/>
            </div>
            <div className="form-group col-md-4">
              <label>Stroke</label>
              <input type="color" className="form-control" id="fontstrokecolor" value={data.text.stroke.color} onChange={this.fontStrokeColorChange.bind(this)} />
            </div>
            <div className="form-group col-md-4">
              <label>Width</label>
              <input type="number" className="form-control" id="fontstrokewidth" value={data.text.stroke.width} onChange={this.fontStrokeWidthChange.bind(this)}/>
            </div>
          </div>
        </div>
      </form>
    )

  }

}

const presetBlue = {
    name: 'Blue',
    fill: {
      color: '#0000FF',
      alpha: '0.1'
    },
    stroke: {
      color: '#319FD3',
      width: 1
    },
    text: {
      font: {
        size: '12',
        type: 'Calibri,sans-serif'
      },
      fill: {
        color: '#0000FF'
      },
      stroke: {
        color: '#FFFFFF',
        width: 3
      }
    }
}

const presetRed = {
    name: 'red',
    fill: {
      color: '#FF0000',
      alpha: '0.3'
    },
    stroke: {
      color: '#319FD3',
      width: 1
    },
    text: {
      font: {
        size: '12',
        type: 'Calibri,sans-serif'
      },
      fill: {
        color: '#FF0000'
      },
      stroke: {
        color: '#FFFFFF',
        width: 3
      }
    }
}

const presetDefault = {
  name: 'Default',
  fill: {
    color: '#FFFFFF',
    alpha: '0.6'
  },
  stroke: {
    color: '#319FD3',
    width: 1
  },
  text: {
    font: {
      size: '12',
      type: 'Calibri,sans-serif'
    },
    fill: {
      color: '#000000'
    },
    stroke: {
      color: '#FFFFFF',
      width: 3
    }
  }
}

StyleSelection.defaultProps = {
  presets: [presetDefault, presetBlue, presetRed],
  data: presetDefault,
  fonts: [
    'Calibri,sans-serif',
    'Georgia, serif'
  ]
}
