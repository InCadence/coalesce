import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';

/**
 * Dialog to display messages.
 */
export class DialogMessage extends React.PureComponent {

  constructor(props) {
    super(props)
  }

  render() {
    var actions = [];
    //actions prop will be used to decide if user needs to make decision
    if (this.props.editable && this.props.actions === 'base') {
      actions = [
        <Button
          label="Edit"
          color='secondary'
          onClick={this.props.onSecondary}>Edit</Button>,
        <Button
          label="Save"
          color='primary'
          onClick={this.props.onPrimary}>Save</Button>,
      ];
    }
    else if (!this.props.editable && this.props.actions === 'base') {
      actions = [
        <Button
          label="Close"
          color='primary'
          onClick={this.props.onPrimary}>Close</Button>,
      ];
    }
    else if (this.props.actions === 'editing') {
      actions = [
        <Button
          label="Cancel"
          color='secondary'
          onClick={this.props.onSecondary}>Cancel</Button>,
        <Button
          label="Save"
          color='primary'
          onClick={this.props.onPrimary}>Save</Button>,
      ];
    }
    else if (this.props.actions === 'edited') {
      actions = [
        <Button
          label="Cancel"
          color='secondary'
          onClick={this.props.onSecondary}>Cancel</Button>,
        <Button
          label="Save"
          color='primary'
          onClick={this.props.onPrimary}>Save</Button>,
      ];
    }
    else if (this.props.actions === 'adding') {
      actions = [
        <Button
          label="Cancel"
          color='secondary'
          onClick={this.props.onSecondary}>Cancel</Button>,
        <Button
          label="Add"
          color='primary'
          onClick={this.props.onPrimary}>Add</Button>,
      ];
    }
    else if (this.props.actions === 'reverting') {
      actions = [
        <Button
        label="Cancel"
        color='secondary'
        onClick={this.props.onSecondary}>Cancel</Button>,
        <Button
          label="REVERT"
          color='primary'
          onClick={this.props.onPrimary}>REVERT</Button>,
      ]
    }
    else {

      if (this.props.confirmation) {
        actions.push(<Button onClick={this.props.onClose} color="primary">
          Cancel
        </Button>)
      }

      actions.push(<Button onClick={this.props.confirmation ? this.props.onClick : this.props.onClose} color="primary">
        OK
      </Button>)

    /*If actions is not set, this renders a simple message, one ok button input from user
      actions = [
        <Button
          label="Error"
          color='primary'
          onClick={this.props.onPrimary}>Error</Button>
      ];
      */
    }

    return (
      (this.props.opened &&
      <Dialog
        open={this.props.opened}
        onClose={this.props.onClose}
        scroll="paper"
        aria-labelledby="scroll-dialog-title"
        PaperProps={{style: {width: '100%'}}}
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
    )
    );
  }
}

DialogMessage.defaultProps = {
  opened: true,
  confirmation: false,
  title: null,
  message: null,
  onClose: undefined,
  onClick: undefined
}
