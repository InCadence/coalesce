import React from 'react';
import { withTheme } from '@material-ui/core/styles';
import { getRootKarafUrl } from 'common-components/lib/js/common';

var rootUrl = getRootKarafUrl("") + '..';

class Image extends React.PureComponent {

  render() {
    var { style } = this.props;

    if (!style) {
      style = {};
    }

    style.height = this.props.size;
    style.width = this.props.size;

    var isSvg = this.props.icon.endsWith("svg");
    var view;

    const palette = this.props.palette ? this.props.palette : this.props.theme.palette.main;

    if (palette.main && isSvg && window.location.port != 3000) {

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
                fill={palette.main}
                stroke={palette.light}
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

Image.defaultProps = {
  size: '30px'
}

export default withTheme()(Image);
