import React from 'react';
import PropTypes from 'prop-types';

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = '';
}

export class IconButton extends React.PureComponent {

  constructor(props) {
    super(props);
  }

  render() {
    var display = 'inline-block'
    if (this.props.visibility === 'none') {
      display = this.props.visibility
    }

    return (
      <img
        src={rootUrl + this.props.icon}
        alt={this.props.title}
        style={{
          width: this.props.size,
          height: this.props.size,
          display: display
        }}
        title={this.props.title}
        className={(this.props.enabled === true) ? "coalesce-img-button enabled" : "coalesce-img-button"}
        onClick={(this.props.enabled === true) ? this.props.onClick : ''}
      />
    )
  }
c
}

IconButton.defaultProps = {
  enabled: true
}
/*pTypes = {
  icon: React.PropTypes.string,
  enabled: React.PropTypes.bool,
  onClick: React.PropTypes.func,
  url: React.PropTypes.string,
};
*/
