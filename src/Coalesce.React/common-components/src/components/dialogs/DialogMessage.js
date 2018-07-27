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

  render() {

    return (
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

DialogMessage.defaultProps = {
  opened: true,
  confirmation: false,
  title: null,
  message: null,
  onClose: undefined,
  onClick: undefined
}
