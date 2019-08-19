import React from 'react';
import { withTheme } from '@material-ui/core/styles';
import Image from 'coalesce-components/lib/components/image'

export class IconButton extends React.PureComponent {

  render() {
    var display = 'inline-block'
    if (this.props.visibility === 'none') {
      display = this.props.visibility
    }

    var palette;

    if (this.props.theme) {
      palette = this.props.theme.palette.icons ? this.props.theme.palette.icons : this.props.theme.palette.primary
    } else {
      palette = {
        contract: null,
        main: null,
        dark: null,
        light: null
      }
    }

    return (
      <Image
        icon={this.props.icon}
        title={this.props.title}
        size={this.props.size}
        square={this.props.square}
        style={{
          backgroundColor: palette.dark,
          borderColor: palette.light,
          display: display
        }}
        class={(this.props.enabled === true) ? "coalesce-img-button enabled" : "coalesce-img-button"}
        onClick={(this.props.enabled === true) ? this.props.onClick : ''}
        palette={palette}
      />
    )
  }
}

IconButton.defaultProps = {
  enabled: true
}

export default withTheme()(IconButton);
