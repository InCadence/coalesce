import React from 'react';
import { DialogMessage } from 'coalesce-components/lib/components/dialogs';
import { PromptDropdown } from './prompt-dropdown.js'
import { StyleSelection } from './style'

import FieldInput from 'coalesce-components/lib/components';

/**
 * Dialog to display messages.
 */
export class DialogFeatures extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      activeStep: 0,
      styleOptions: [{
        'key': 'Custom',
        'name': 'Custom'
      }].concat(this.props.styles),
      promptValue: { 
        layer: props.data[0].key,
        style: {
          
        },
        tiled: false, 
        type: 'WMS',
      }
    };
  }


  layerChange = (value) => {
    this.setState({promptValue: {...this.state.promptValue, layer: value}});
  };

  typeChange = (value) => {
    this.setState({promptValue: {...this.state.promptValue, type: value}});
  };

  styleChange = (value) => {
    this.setState({promptValue: {...this.state.promptValue, style: value}});
  };

  tileChange = (value) => {
    this.setState({promptValue: {...this.state.promptValue, tiled: value.target.checked}});
  };

  handleSubmit = () => {
    this.props.onClick(this.state.promptValue);
  }

  handleNext = () => {
    this.setState({activeStep: 1});
  }

  getStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <React.Fragment>
            <label>Layer</label>
            <PromptDropdown onChange={this.layerChange} data={this.props.data} />
            <label>Type</label>
            <PromptDropdown onChange={this.typeChange} data={[
              { key: 'WMS', name: 'WMS' },
              { key: 'WFS', name: 'WFS' },
              { key: 'WPS', name: 'WPS' },
              { key: 'HEATMAP', name: 'HEATMAP' }
            ]} />
            { this.state.promptValue.type === 'WMS' &&
              <React.Fragment>
                <label>Tiled (WMS Only)</label>
                <div className="row">
                  <div className="col-sm-2">
                    <input type="checkbox" className="form-control" onChange={this.tileChange} />
                  </div>
                </div>
              </React.Fragment>
            }
          </React.Fragment>          
        );
      default:
        return (<StyleSelection onChange={this.styleChange} presets={this.props.styles} />);
    }
  }

  hasNext = () => {
    return this.state.activeStep === 0 && this.state.promptValue.type === 'WFS';
  }

  render() {

    return (
      <DialogMessage
        title="Select Feature"
        opened={true}
        onClose={this.props.onClose}
        onClick={this.hasNext() ? this.handleNext : this.handleSubmit}
        submitText={this.hasNext() ? 'Next' : "OK"}
        confirmation
      >
        {this.getStepContent(this.state.activeStep)}        
      </DialogMessage>
    )
  }
}


