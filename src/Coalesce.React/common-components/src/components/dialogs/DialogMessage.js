import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';

/**
 * Dialog to display error messages.
 */
export class DialogMessage extends React.PureComponent {

  render() {
    const actions = [
      <FlatButton
        label="OK"
        primary={true}
        onClick={this.props.onClose}
      />
    ];

    return (
        <Dialog
          title={this.props.title}
          actions={actions}
          modal={false}
          open={this.props.opened}
          onRequestClose={this.props.onClose}
        >
          {this.props.message}
        </Dialog>
    );
  }
}
