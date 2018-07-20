import React from 'react';
import PropTypes from 'prop-types';
import { withTheme } from '@material-ui/core/styles';
import { Image } from 'common-components/lib/components/image'

export class IconButton extends React.PureComponent {

  render() {
    var display = 'inline-block'
    if (this.props.visibility === 'none') {
      display = this.props.visibility
    }

    return (
      <Image
        icon={this.props.icon}
        title={this.props.title}
        size={this.props.size}
        style={{
          backgroundColor: this.props.theme.palette.primary.dark,
          borderColor: this.props.theme.palette.primary.light,
          display: display
        }}
        class={(this.props.enabled === true) ? "coalesce-img-button enabled" : "coalesce-img-button"}
        onClick={(this.props.enabled === true) ? this.props.onClick : ''}
        theme={this.props.theme}
      />
    )
  }
}

IconButton.defaultProps = {
  enabled: true
}

export default withTheme()(IconButton);
