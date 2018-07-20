import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import Button from '@material-ui/core/Button';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

/**
 * Dialog to display error messages.
 */
export class DialogMessage extends React.PureComponent {

  constructor(props) {
    super(props)
  }
  render() {

    var actions = [];
    //actions prop will be used to decide if user needs to make decision

    if (this.props.actions === 'edit') {
      actions = [
        <Button
          label="Edit"
          secondary={true}
          onClick={this.props.onEditToggle}>Edit</Button>,
        <Button
          label="OK"
          primary={true}
          onClick={this.props.onClose}>OK</Button>,
      ];
    }
    else if (this.props.actions === 'edited') {
      actions = [
        <Button
          label="Cancel"
          secondary={true}
          onClick={this.props.onCancel}>Cancel</Button>,
        <Button
          label="OK"
          primary={true}
          onClick={this.props.onClose}>OK</Button>,
      ];
    }
    else {
    //If actions is not set, this renders a simple message, one ok button input from user
      actions = [
        <Button
          label="OK"
          primary={true}
          onClick={this.props.onClose}>OK</Button>
      ];
    }

    return (
      <Dialog
        open={this.props.opened}
        onClose={this.props.onClose}
        scroll="paper"
        aria-labelledby="scroll-dialog-title"
        style={{width: '100%'}}
      >
        <DialogTitle id="scroll-dialog-title">{this.props.title}</DialogTitle>
        <DialogContent>
          <DialogContentText>
              {this.props.message}
          </DialogContentText>
          {this.props.children}
        </DialogContent>
        <DialogActions>
          {actions}
        </DialogActions>
      </Dialog>
    );
  }
}
