import React from 'react';

export class Toggle extends React.PureComponent  {

  constructor(props) {
    super(props);

    this.state = props;

    // This binding is necessary to make `this` work in the callback
    this.handleClick = this.handleClick.bind(this);
  }

	handleClick() {
		this.setState(function(prevState) {
    this.props.onToggle(!prevState.isToggleOn);
			return {isToggleOn: !prevState.isToggleOn};
		});
	}

  render() {
    return (
        <button className="ui-widget-header ui-button" onClick={this.handleClick}>
          {this.state.isToggleOn ? this.state.offtext : this.state.ontext}
        </button>
    );
  }
}

Toggle.defaultProps = {
  label: '',
  isToggleOn: true,
  ontext: 'ON',
  offtext: 'OFF',
  onToggle: () => {}
}
