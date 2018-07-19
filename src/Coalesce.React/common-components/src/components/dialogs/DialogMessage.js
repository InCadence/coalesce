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

  render() {

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
          {this.props.confirmation &&
          <Button onClick={this.props.onClose} color="primary">
            Cancel
          </Button>
          }
          <Button onClick={this.props.confirmation ? this.props.onClick : this.props.onClose} color="primary">
            OK
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
}
