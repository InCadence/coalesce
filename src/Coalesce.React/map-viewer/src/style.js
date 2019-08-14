import React from "react";

export class StyleSelection extends React.Component {

  fillColorChange = (e) => {
    console.log(e);
    const {data} = this.props;
    data.fill.color = e.target ? e.target.value : e;
    this.update(data);
  }

  fillAplhaChange = (e) => {
    console.log(e);
    const {data} = this.props;
    data.fill.alpha = e.target.value;
    this.update(data);
  }

  strokeColorChange = (e) => {
    console.log(e);
    console.log(e.target.id)
    const {data} = this.props;
    data.stroke.color = e.target.value;
    this.update(data);
  }

  strokeWidthChange = (e) => {
    const {data} = this.props;
    data.stroke.width = e.target.value;
    this.update(data);
  }

  fontSizeChange = (e) =>  {
    const {data} = this.props;
    data.text.font.size = e.target.value;
    this.update(data);
  }

  fontTypeChange = (e) => {
    const {data} = this.props;
    data.text.font.type = e.target.value;
    this.update(data);
  }

  frontFillColorChange = (e) => {
    const {data} = this.props;
    data.text.fill.color = e.target.value;
    this.update(data);
  }

  fontStrokeColorChange = (e) => {
    const {data} = this.props;
    data.text.stroke.color = e.target.value;
    this.update(data);
  }

  fontStrokeWidthChange = (e) => {
    const {data} = this.props;
    data.text.stroke.width = e.target.value;
    this.update(data);
  }

  presetChange = (e) => {
    const {presets} = this.props;
    for (var ii=0; ii<presets.length; ii++) {
      if (presets[ii].name === e.target.value) {
        this.update(presets[ii]);
        break;
      }
    }
  }

  update(data) {
    this.props.onChange(data);
  }

  render() {

    const { data } = this.props;

    var presetOptions = this.props.presets.map(preset => <option key={preset.name}>{preset.name}</option>);
    var fontOptions = this.props.fonts.map(font => <option key={font}>{font}</option>);

    return (
      <form>
        <div className="form-row">
          <div className="form-group col-md-12">
            <label>Presets</label>
            <select className="form-control" id="presets" onChange={this.presetChange} >
              {presetOptions}
            </select>
          </div>
        </div>
        <div className="form-row">
          <label>Fill</label>
          <div className="form-row">
            <div className="form-group col-md-6">
              <label>Color</label>
              <input type="color" className="form-control" id="fillcolor" value={data.fill.color} onChange={this.fillColorChange}/>
            </div>
            <div className="form-group col-md-6">
              <label>Alpha</label>
              <input type="number" min="0" max="1" step="0.01" className="form-control" id="fillalpha" value={data.fill.alpha} onChange={this.fillAplhaChange}/>
            </div>
          </div>
        </div>
        <div className="form-row">
          <label>Stroke</label>
          <div className="form-row">
            <div className="form-group col-md-6">
              <label>Color</label>
              <input type="color" className="form-control" id="strokecolor" value={data.stroke.color} onChange={this.strokeColorChange}/>
            </div>
            <div className="form-group col-md-6">
              <label>Width</label>
              <input type="number" min="0" className="form-control" id="strokewidth" value={data.stroke.width} onChange={this.strokeWidthChange}/>
            </div>
          </div>
        </div>
        <div className="form-row">
          <label>Text</label>
          <div className="form-row">
            <div className="form-group col-md-3">
              <label>Size</label>
              <input type="number" min="0" className="form-control" id="fontsize" value={data.text.font.size} onChange={this.fontSizeChange}/>
            </div>
            <div className="form-group col-md-9">
              <label>Type</label>
              <select className="form-control" id="fonttype" value={data.text.font.type} onChange={this.fontTypeChange}>
                {fontOptions}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group col-md-4">
              <label>Fill</label>
              <input type="color" className="form-control" id="fontfillcolor" value={data.text.fill.color} onChange={this.frontFillColorChange}/>
            </div>
            <div className="form-group col-md-4">
              <label>Stroke</label>
              <input type="color" className="form-control" id="fontstrokecolor" value={data.text.stroke.color} onChange={this.fontStrokeColorChange} />
            </div>
            <div className="form-group col-md-4">
              <label>Width</label>
              <input type="number" min="0" className="form-control" id="fontstrokewidth" value={data.text.stroke.width} onChange={this.fontStrokeWidthChange}/>
            </div>
          </div>
        </div>
      </form>
    )

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
      color: '#000000',
      width: 3
    }
  }
}

StyleSelection.defaultProps = {
  presets: [presetDefault],
  data: presetDefault,
  fonts: [
    'Calibri,sans-serif',
    'Georgia, serif'
  ]
}
