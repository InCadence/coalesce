import React from 'react';
import {Collapse} from 'react-collapse';
import {Toggle} from './toggle.js'

export class Accordion extends React.Component  {

  constructor(props) {
    super(props);
    this.state = props;
  }

  render() {
    const {isOpened, label, children, objectkey} = this.state;

    return (
      <div id={objectkey} key={objectkey} className="ui-widget">
        <Toggle
          ontext={label}
          offtext={label}
          isToggleOn={isOpened}
          onToggle={(value) => {
            this.setState({isOpened: value});
          }}
          />
        <Collapse isOpened={isOpened}>
          <div className="ui-widget-content">
            {children}
          </div>
        </Collapse>
      </div>
    );
  }
}

Accordion.defaultProps = {
  label: '',
  isOpened: true
}
