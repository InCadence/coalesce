import React from 'react';
import PropTypes from 'prop-types';

export class IconButton extends React.PureComponent {

  constructor(props) {
    super(props);
  }

  render() {

    var rootUrl;

    if (window.location.port == 3000) {
      rootUrl  = 'http://' + window.location.hostname + ':8181';
    } else {
      rootUrl  = '';
    }

    return (
      <img
        src={rootUrl + this.props.icon}
        alt={this.props.title}
        title={this.props.title}
        className={(this.props.enabled === true) ? "coalesce-img-button enabled" : "coalesce-img-button"}
        onClick={(this.props.enabled === true) ? this.props.onClick : ''}
      />
    )
  }

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
