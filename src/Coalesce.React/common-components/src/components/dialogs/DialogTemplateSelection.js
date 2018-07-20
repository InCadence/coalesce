import React from 'react';
import DialogOptions from './DialogOptions';

export class DialogTemplateSelection extends React.PureComponent {

  constructor(props) {
    super(props);

    this.handleClick = this.handleClick.bind(this);
    this.handleClose = this.handleClose.bind(this);
  }

  handleClose() {
    this.props.onClose();
  };

  handleClick(key) {
    this.props.onClose();
    this.props.onClick(key);
  }

  render() {

    return (
      <DialogOptions
        title="Select a Template"
        options={this.props.templates}
        open={this.props.opened}
        onClose={this.handleClose}
        onClick={this.handleClick}
      >
      </DialogOptions>
    )
  }

}
