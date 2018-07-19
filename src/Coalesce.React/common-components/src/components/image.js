import React from 'react';
import { withTheme } from '@material-ui/core/styles';
import { getRootKarafUrl } from 'common-components/lib/js/common';

var rootUrl = getRootKarafUrl("") + '..';

export class Image extends React.PureComponent {

  render() {
    var { style } = this.props;

    if (!style) {
      style = {};
    }

    style.height = this.props.size;
    style.width = this.props.size;

    var isSvg = this.props.icon.endsWith("svg");
    var view;

    if (this.props.theme && isSvg && window.location.port != 3000) {
      // SVG Does not work with CORS
      view = (
          <svg
            className={this.props.class}
            viewBox="0 0 512 512"
            style={style}
            onClick={this.props.onClick}
            >
              <use
                xlinkHref={`${rootUrl}${this.props.icon}#Selection`}
                fill={this.props.theme.palette.iconColor}
                stroke={this.props.theme.palette.iconBorderColor}
                strokeWidth="1"
              />
          </svg>
      )
    } else if (this.props.theme && isSvg) {
      view = (
        <img
          className={this.props.class}
          src={rootUrl + this.props.icon}
          alt={this.props.title}
          style={style}
          onClick={this.props.onClick}
        />
      )
    } else {
      view = (
        <img
          className={this.props.class}
          src={rootUrl + this.props.icon}
          alt={this.props.title}
          title={this.props.title}
          style={style}
          onClick={this.props.onClick}
        />
      )
    }

    return view;
  }

}

export default withTheme()(Image);
